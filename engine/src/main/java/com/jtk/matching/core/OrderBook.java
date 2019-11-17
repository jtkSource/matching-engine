package com.jtk.matching.core;

import static com.jtk.matching.api.avro.AvroUtil.convertToBigDecimal;
import com.jtk.matching.api.gen.Execution;
import com.jtk.matching.api.gen.Order;
import com.jtk.matching.api.gen.enums.MsgType;
import com.jtk.matching.api.gen.enums.PriceType;
import com.jtk.matching.api.gen.enums.Side;
import static com.jtk.matching.core.OrderBookValidation.validateAmendOrder;
import static com.jtk.matching.core.OrderBookValidation.validateCancelOrder;
import static com.jtk.matching.core.OrderBookValidation.validateNewOrder;
import com.jtk.matching.core.exp.ValidationException;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.tuple.Tuples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class OrderBook {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBook.class);

    private final static int PRICE_SCALE = 8; // should be set from system property

    private final String productId;

    private final PriceType priceType;

    private final boolean reverseOrder;

    private final TreeSortedSet<OrderBookEntry> bidSet;

    private final TreeSortedSet<OrderBookEntry> askSet;

    private final UnifiedSet<String> orderIdSet = new UnifiedSet<>();

    private final Comparator<OrderBookEntry> descPriceTimeComparator = Comparator.comparing(OrderBookEntry::getPrice, Comparator.reverseOrder())
            .thenComparingLong(OrderBookEntry::getOrderBookEntryTimeInMillis).thenComparing(OrderBookEntry::getOrderId);

    private final Comparator<OrderBookEntry> ascPriceTimeComparator = Comparator.comparing(OrderBookEntry::getPrice)
            .thenComparing(OrderBookEntry::getOrderBookEntryTimeInMillis).thenComparing(OrderBookEntry::getOrderId);

    private final EmitterProcessor<Pair<OrderBookEntry, OrderBookEntry>> negoProcessor;
    //Pair<bid,ask>
    private final EmitterProcessor<Pair<Optional<BigDecimal>, Optional<BigDecimal>>> topLevelPriceProcessor;

    private final EmitterProcessor<Execution> executionProcessor;

    volatile private AtomicReference<BigDecimal> bestAsk = new AtomicReference(BigDecimal.ZERO);

    volatile private AtomicReference<BigDecimal> bestBid = new AtomicReference(BigDecimal.ZERO);

    private final ReentrantReadWriteLock bidRWLock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock askRWLock = new ReentrantReadWriteLock();

    public OrderBook(String productId, PriceType priceType, boolean reverseOrder) {
        this.productId = productId;
        this.priceType = priceType;
        this.reverseOrder = reverseOrder;
        if (!this.reverseOrder) {
            this.bidSet = TreeSortedSet.newSet(descPriceTimeComparator);
            this.askSet = TreeSortedSet.newSet(ascPriceTimeComparator);
        } else {
            this.bidSet = TreeSortedSet.newSet(ascPriceTimeComparator);
            this.askSet = TreeSortedSet.newSet(descPriceTimeComparator);
        }
        negoProcessor = EmitterProcessor.create();
        topLevelPriceProcessor = EmitterProcessor.create();
        executionProcessor = EmitterProcessor.create();
    }

    public String getProductId() {
        return this.productId;
    }

    public PriceType getPriceType() {
        return this.priceType;
    }

    public static int getPriceScale() {
        return PRICE_SCALE;
    }

    /**
     * Subscribing to the FluxProcessor returned by this method will tap into orders that fall within a configured
     * discretionary offset when added to the order book.
     *
     * @return
     */
    public EmitterProcessor<Pair<OrderBookEntry, OrderBookEntry>> getNegotiationSource() {
        return negoProcessor;
    }

    /**
     * Subscribing to the FluxProcessor returned by this method will tap into top-level prices when order is added to
     * order book
     *
     * @return
     */
    public EmitterProcessor<Pair<Optional<BigDecimal>, Optional<BigDecimal>>> getTopLevelPriceSource() {
        return topLevelPriceProcessor;
    }

    /**
     * Subscribing to the FluxProcessor returned by this method will tap into executions generated when order is added to
     * order book
     *
     * @return
     */
    public EmitterProcessor<Execution> getExecutionProcessor() {
        return executionProcessor;
    }

    public void addOrder(Order order) throws ValidationException {
        if (orderIdSet.contains(order.getOrderId()))
            throw new IllegalArgumentException("OrderId " + order.getOrderId() + " is already in order-book");
        validateNewOrder(order);
        OrderBookEntry orderEntry =
                new OrderBookEntry(order.getOrderId(),
                        convertToBigDecimal(order.getPrice(), PRICE_SCALE),
                        order.getQuantity(), order.getDiscretionaryOffset(), order.getSide());
        if (orderEntry.getSide() == Side.Buy) {
            addBuyOrder(orderEntry);
        } else {
            addSellOrder(orderEntry);
        }

        Mono.just(Tuples.pair(Optional.ofNullable(getBestBid()), Optional.ofNullable(getBestAsk())))
                .publishOn(Schedulers.newSingle("top-level-source"))
                .log("orderbook.marketdata.", Level.FINE)
                .subscribe(topLevelPriceProcessor::onNext);
        Flux.fromIterable(findOrdersWithinDO(orderEntry))
                .publishOn(Schedulers.newSingle("nego-source"))
                .log("orderbook.nego.", Level.FINE)
                .subscribe(negoProcessor::onNext);
    }

    public boolean cancelOrder(Order cancelOrder) throws ValidationException {
        boolean isCancelled;
        if (!orderIdSet.contains(cancelOrder.getOrderId()))
            throw new IllegalArgumentException("OrderId " + cancelOrder.getOrderId() + " is doesnt exist in order-book");
        validateCancelOrder(cancelOrder);

        if (cancelOrder.getSide() == Side.Sell) {
            try {
                askRWLock.writeLock().lock();
                isCancelled = askSet.remove(askSet.select(p -> p.getOrderId().equals(cancelOrder.getOrderId())).getFirst());
                updateBestAsk();
                orderIdSet.remove(cancelOrder.getOrderId());
            } finally {
                askRWLock.writeLock().unlock();
            }
        } else {
            try {
                bidRWLock.writeLock().lock();
                isCancelled = bidSet.remove(bidSet.select(p -> p.getOrderId().equals(cancelOrder.getOrderId())).getFirst());
                updateBestBid();
                orderIdSet.remove(cancelOrder.getOrderId());
            } finally {
                bidRWLock.writeLock().unlock();
            }
        }
        return isCancelled;
    }

    public boolean amendOrder(Order amendOrder) throws ValidationException {
        validateAmendOrder(amendOrder);
        if (cancelOrder(Order.newBuilder(amendOrder).setMsgType(MsgType.Cancel).build())) {
            Order newOrder = Order.newBuilder(amendOrder).setMsgType(MsgType.New).build();
            addOrder(newOrder);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }


    public BigDecimal getBestAsk() {
        return bestAsk.get();
    }

    public BigDecimal getBestBid() {
        return bestBid.get();
    }

    public static OrderBook getInstance(String productId, PriceType priceType) {
        return new OrderBook(productId, priceType, false);
    }

    public static OrderBook getInstance(String productId, PriceType priceType, boolean reverseOrder) {
        return new OrderBook(productId, priceType, reverseOrder);
    }

    MutableSortedSet<OrderBookEntry> getBids() {
        return bidSet.asUnmodifiable();
    }

    MutableSortedSet<OrderBookEntry> getAsks() {
        return askSet.asUnmodifiable();
    }

    String printOrderBook() {
        MutableList<Pair<String, String>> bidPrices = getBids().collect(orderBookEntry -> Tuples.pair(orderBookEntry.getPrice().toPlainString(), String.valueOf(orderBookEntry.getQuantity())));
        MutableList<Pair<String, String>> askPrices = getAsks().collect(orderBookEntry -> Tuples.pair(orderBookEntry.getPrice().toPlainString(), String.valueOf(orderBookEntry.getQuantity())));
        int minIndex = Math.min(bidPrices.size(), askPrices.size());
        StringBuilder builder = new StringBuilder();
        builder.append(this.toString());
        builder.append("\n")
                .append(String.format("%10s | %20s | %-20s | %10s%n", "QTY", "BID", "ASK", "QTY"))
                .append(String.format("%10s | %20s | %-20s | %10s%n", "----------", "-------------------", "-------------------", "----------"));
        for (int i = 0; i < minIndex; i++) {
            builder.append(String.format("%10s | %20s | %-20s | %10s%n", bidPrices.get(i).getTwo(), bidPrices.get(i).getOne(), askPrices.get(i).getOne(), askPrices.get(i).getTwo()));
        }
        if (bidPrices.size() < askPrices.size()) {
            for (int i = minIndex; i < askPrices.size(); i++) {
                builder.append(String.format("%10s | %20s | %-20s | %10s%n", "", "", askPrices.get(i).getOne(), askPrices.get(i).getTwo()));
            }
        }

        if (bidPrices.size() > askPrices.size()) {
            for (int i = minIndex; i < bidPrices.size(); i++) {
                builder.append(String.format("%10s | %20s | %-20s | %10s%n", bidPrices.get(i).getTwo(), bidPrices.get(i).getOne(), "", ""));
            }
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "OrderBook{" +
                "productId='" + productId + '\'' +
                ", priceType=" + priceType +
                ", reverseOrder=" + reverseOrder +
                '}';
    }

    private List<Pair<OrderBookEntry, OrderBookEntry>> findOrdersWithinDO(OrderBookEntry orderBookEntry) {

        final BigDecimal price = orderBookEntry.getPrice();
        List<Pair<OrderBookEntry, OrderBookEntry>> nego;
        if (orderBookEntry.getSide() == Side.Buy) {
            BigDecimal offSetPrice = reverseOrder ?
                    price.subtract(BigDecimal.valueOf(orderBookEntry.getDiscretionaryOffset())) :
                    price.add(BigDecimal.valueOf(orderBookEntry.getDiscretionaryOffset()));
            try {
                askRWLock.readLock().lock();

                nego = askSet
                        .select(oe -> reverseOrder ?
                                oe.getPrice().compareTo(offSetPrice) >= 0 && oe.getPrice().compareTo(price) <= 0 :
                                oe.getPrice().compareTo(offSetPrice) <= 0 && oe.getPrice().compareTo(price) >= 0)
                        .stream()
                        .map(oe -> Tuples.pair(orderBookEntry, oe))
                        .collect(Collectors.toList());
            } finally {
                askRWLock.readLock().unlock();
            }
        } else {
            BigDecimal offSetPrice = reverseOrder ?
                    price.add(BigDecimal.valueOf(orderBookEntry.getDiscretionaryOffset())) :
                    price.subtract(BigDecimal.valueOf(orderBookEntry.getDiscretionaryOffset()));
            try {
                bidRWLock.readLock().lock();
                nego = bidSet.select(
                        oe -> reverseOrder ?
                                oe.getPrice().compareTo(offSetPrice) <= 0 && oe.getPrice().compareTo(price) >= 0 :
                                oe.getPrice().compareTo(offSetPrice) >= 0 && oe.getPrice().compareTo(price) <= 0)
                        .stream()
                        .map(oe -> Tuples.pair(orderBookEntry, oe))
                        .collect(Collectors.toList());
            } finally {
                bidRWLock.readLock().unlock();
            }
        }
        return nego;
    }

    private void addSellOrder(OrderBookEntry orderEntry) {
        if (!isAskMatching(orderEntry)) {
            try {
                askRWLock.writeLock().lock();
                askSet.add(orderEntry);
                orderIdSet.add(orderEntry.getOrderId());
                updateBestAsk();
            } finally {
                askRWLock.writeLock().unlock();
            }
        } else {
            LOGGER.info("Execute {} ", orderEntry);
            long remainingQty = reduceBidsOnOrderBook(orderEntry);
            try {
                askRWLock.writeLock().lock();
                if (remainingQty > 0) {
                    orderEntry = new OrderBookEntry(orderEntry.getOrderId(), orderEntry.getPrice().setScale(PRICE_SCALE, RoundingMode.DOWN),
                            remainingQty, orderEntry.getDiscretionaryOffset(), orderEntry.getSide());
                    askSet.add(orderEntry);
                    orderIdSet.add(orderEntry.getOrderId());
                    updateBestAsk();
                }
            } finally {
                askRWLock.writeLock().unlock();
            }
        }
    }

    private void addBuyOrder(OrderBookEntry orderEntry) {
        if (!isBidMatching(orderEntry)) {
            try {
                bidRWLock.writeLock().lock();
                bidSet.add(orderEntry);
                orderIdSet.add(orderEntry.getOrderId());
                updateBestBid();
            } finally {
                bidRWLock.writeLock().unlock();
            }
        } else {
            long remainingQty = reduceAsksOnOrderBook(orderEntry);
            try {
                bidRWLock.writeLock().lock();
                if (remainingQty > 0) {
                    orderEntry = new OrderBookEntry(orderEntry.getOrderId(), orderEntry.getPrice().setScale(PRICE_SCALE, RoundingMode.DOWN),
                            remainingQty, orderEntry.getDiscretionaryOffset(), orderEntry.getSide());
                    bidSet.add(orderEntry);
                    orderIdSet.add(orderEntry.getOrderId());
                    updateBestBid();
                }
            } finally {
                bidRWLock.writeLock().unlock();
            }
        }
    }

    private long reduceAsksOnOrderBook(OrderBookEntry bidEntry) {
        try {
            askRWLock.writeLock().lock();
            long remainingQuantity = bidEntry.getQuantity();

            while (remainingQuantity > 0 || !askSet.isEmpty()) {
                OrderBookEntry askEntry = askSet.getFirst();
                if (askEntry.getQuantity() <= remainingQuantity) {
                    if (askSet.remove(askEntry)) {
                        long executedQuantity = askEntry.getQuantity();
                        createExecution(executedQuantity, bidEntry.getPrice(), bidEntry.getOrderId(), Side.Buy);
                        createExecution(executedQuantity, bidEntry.getPrice(), askEntry.getOrderId(), Side.Sell);
                        remainingQuantity = bidEntry.getQuantity() - executedQuantity;
                        updateBestAsk();
                    }
                } else if (askEntry.getQuantity() > bidEntry.getQuantity()) {
                    long executedQuantity = askEntry.getQuantity() - bidEntry.getQuantity();
                    remainingQuantity = 0;
                    askEntry.quantity = executedQuantity;
                    createExecution(executedQuantity, bidEntry.getPrice(), bidEntry.getOrderId(), Side.Buy);
                    createExecution(executedQuantity, bidEntry.getPrice(), askEntry.getOrderId(), Side.Sell);

                }
                if (remainingQuantity <= 0 || !isBidMatching(bidEntry))
                    break;
                bidEntry = new OrderBookEntry(bidEntry.getOrderId(), bidEntry.getPrice(), remainingQuantity, bidEntry.getDiscretionaryOffset(),
                        bidEntry.getSide());
            }
            return remainingQuantity;
        } finally {
            askRWLock.writeLock().unlock();
        }
    }

    private long reduceBidsOnOrderBook(OrderBookEntry askEntry) {

        try {
            bidRWLock.writeLock().lock();
            long remainingQuantity = askEntry.getQuantity();

            while (remainingQuantity > 0 || !bidSet.isEmpty()) {
                OrderBookEntry bidEntry = bidSet.getFirst();
                if (bidEntry.getQuantity() <= remainingQuantity) {
                    if (bidSet.remove(bidEntry)) {
                        long executedQuantity = bidEntry.getQuantity();
                        createExecution(executedQuantity, askEntry.getPrice(), bidEntry.getOrderId(), Side.Buy);
                        createExecution(executedQuantity, askEntry.getPrice(), askEntry.getOrderId(), Side.Sell);
                        remainingQuantity = askEntry.getQuantity() - executedQuantity;
                        updateBestBid();
                    }
                } else if (bidEntry.getQuantity() > askEntry.getQuantity()) {
                    long executedQuantity = bidEntry.getQuantity() - askEntry.getQuantity();
                    remainingQuantity = 0;
                    bidEntry.quantity = executedQuantity;
                    createExecution(executedQuantity, askEntry.getPrice(), bidEntry.getOrderId(), Side.Buy);
                    createExecution(executedQuantity, askEntry.getPrice(), askEntry.getOrderId(), Side.Sell);

                }
                if (remainingQuantity <= 0 || !isAskMatching(askEntry))
                    break;
                askEntry = new OrderBookEntry(askEntry.getOrderId(), askEntry.getPrice(), remainingQuantity, askEntry.getDiscretionaryOffset(),
                        askEntry.getSide());
            }
            return remainingQuantity;
        } finally {
            bidRWLock.writeLock().unlock();
        }
    }

    private void createExecution(long executedQuantity, BigDecimal executedPrice, String bidOrderId, Side side) {
        Mono.just(Execution.newBuilder()
                .setExecCreation(Instant.now())
                .setExecutedQuantity(executedQuantity)
                .setExecutedPrice(executedPrice)
                .setOrderId(bidOrderId)
                .setSide(side)
                .build())
                .publishOn(Schedulers.newSingle("execution-source"))
                .log("orderbook.execution.", Level.FINE)
                .subscribe(executionProcessor::onNext);
    }

    private boolean isAskMatching(OrderBookEntry newAsk) {
        if (!reverseOrder) {
            return newAsk.getPrice().compareTo(bestBid.get()) <= 0;
        } else {
            return newAsk.getPrice().compareTo(bestBid.get()) >= 0 && bestBid.get() != BigDecimal.ZERO;
        }
    }

    private boolean isBidMatching(OrderBookEntry newBid) {
        if (!reverseOrder) {
            return newBid.getPrice().compareTo(bestAsk.get()) >= 0 && bestAsk.get() != BigDecimal.ZERO;
        } else {
            return newBid.getPrice().compareTo(bestAsk.get()) <= 0;
        }
    }

    private void updateBestBid() {
        bestBid.set(bidSet.getFirst().price);
    }

    private void updateBestAsk() {
        bestAsk.set(askSet.getFirst().price);
    }

    static final class OrderBookEntry {
        private final CharSequence orderId;
        private final BigDecimal price;
        private final long orderBookEntryTimeInMillis;
        private long quantity;
        private double discretionaryOffset;
        private Side side;

        public OrderBookEntry(CharSequence orderId, BigDecimal price, long quantity, double discretionaryOffset, Side side) {
            this.orderId = orderId;
            this.price = price;
            this.quantity = quantity;
            this.discretionaryOffset = discretionaryOffset;
            this.side = side;
            this.orderBookEntryTimeInMillis = Instant.now().toEpochMilli();
        }

        public Side getSide() {
            return side;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public String getOrderId() {
            return orderId.toString();
        }

        public long getOrderBookEntryTimeInMillis() {
            return orderBookEntryTimeInMillis;
        }

        public long getQuantity() {
            return quantity;
        }

        public double getDiscretionaryOffset() {
            return discretionaryOffset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderBookEntry that = (OrderBookEntry) o;
            return orderId.equals(that.orderId) &&
                    price.equals(that.price) &&
                    orderBookEntryTimeInMillis == that.orderBookEntryTimeInMillis;
        }

        @Override
        public int hashCode() {
            return Objects.hash(orderId, price, orderBookEntryTimeInMillis);
        }

        @Override
        public String toString() {
            return "OrderBookEntry{" +
                    "orderId=" + orderId +
                    ", price=" + price +
                    ", quantity=" + quantity +
                    ", orderBookEntryTimeInMillis=" + orderBookEntryTimeInMillis +
                    '}';
        }
    }

}
