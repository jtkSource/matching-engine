package com.jtk.matching.core;

import com.jtk.matching.api.gen.Order;
import com.jtk.matching.api.gen.enums.PriceType;
import com.jtk.matching.api.gen.enums.Side;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.tuple.Tuples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class OrderBook {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBook.class);
    private final String productId;
    private final PriceType priceType;
    private final boolean reverseOrder;
    private final TreeSortedSet<OrderBookEntry> bidSet;
    private final TreeSortedSet<OrderBookEntry> askSet;
    private final static int PRICE_SCALE = 8; // should be set fron system property
    private final Comparator<OrderBookEntry> descPriceTimeComparator = Comparator.comparing(OrderBookEntry::getPrice, Comparator.reverseOrder())
            .thenComparingLong(OrderBookEntry::getOrderBookEntryTimeInMillis).thenComparing(OrderBookEntry::getOrderId);
    private final Comparator<OrderBookEntry> ascPriceTimeComparator = Comparator.comparing(OrderBookEntry::getPrice)
            .thenComparing(OrderBookEntry::getOrderBookEntryTimeInMillis).thenComparing(OrderBookEntry::getOrderId);

    volatile private AtomicReference<BigDecimal> bestAsk = new AtomicReference(BigDecimal.ZERO);
    volatile private AtomicReference<BigDecimal> bestBid = new AtomicReference(BigDecimal.ZERO);

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
    }

    /**
     * not singleton
     *
     * @param productId
     * @param priceType
     * @return
     */
    static OrderBook getInstance(String productId, PriceType priceType) {
        return new OrderBook(productId, priceType, false);
    }

    static OrderBook getInstance(String productId, PriceType priceType, boolean reverseOrder) {
        return new OrderBook(productId, priceType, reverseOrder);
    }


    public String getProductId() {
        return this.productId;
    }

    public PriceType getPriceType() {
        return this.priceType;
    }

    //TODO: validations - price cant be negative, MKT prices to be supported, handling duplicate orderId
    public void addOrder(Order order) {
        OrderBookEntry orderEntry = new OrderBookEntry(order.getOrderId(), order.getPrice().setScale(PRICE_SCALE, RoundingMode.DOWN), order.getQuantity());
        if (order.getSide() == Side.Buy) {
            //TODO bidlock
            if (!isBidMatching(orderEntry)) {
                bidSet.add(orderEntry);
                updateBestBid();
            } else {
                LOGGER.info("Execute {} ", orderEntry);
                long remainingQty = reduceAsksOnOrderBook(orderEntry);
                if (remainingQty > 0) {
                    orderEntry = new OrderBookEntry(orderEntry.getOrderId(), orderEntry.getPrice().setScale(PRICE_SCALE, RoundingMode.DOWN), remainingQty);
                    bidSet.add(orderEntry);
                    updateBestBid();
                }
            }
        } else {
            //TODO askLock
            if (!isAskMatching(orderEntry)) {
                askSet.add(orderEntry);
                updateBestAsk();
            } else {
                //TODO: execute
                LOGGER.info("Execute {} ", orderEntry);
            }
        }
    }

    private long reduceAsksOnOrderBook(OrderBookEntry bidEntry) {

        long remainingQuantity = bidEntry.getQuantity();

        while (remainingQuantity > 0 || !askSet.isEmpty()) {
            OrderBookEntry askEntry = askSet.getFirst();
            if (askEntry.getQuantity() <= remainingQuantity) {
                if (askSet.remove(askEntry)) {
                    long executedQuantity = askEntry.getQuantity();
                    createExecution(executedQuantity, bidEntry.getPrice(), bidEntry.getOrderId(), askEntry.getOrderId(), Instant.now());
                    remainingQuantity = bidEntry.getQuantity() - executedQuantity;
                    updateBestAsk();
                }
            } else if (askEntry.getQuantity() > bidEntry.getQuantity()) {
                long executedQuantity = askEntry.getQuantity() - bidEntry.getQuantity();
                remainingQuantity = 0;
                askEntry.setQuantity(executedQuantity);
                createExecution(executedQuantity, bidEntry.getPrice(), bidEntry.getOrderId(), askEntry.getOrderId(), Instant.now());
            }
            bidEntry = new OrderBookEntry(bidEntry.getOrderId(), bidEntry.getPrice(), remainingQuantity);
            if (remainingQuantity <=0 || !isBidMatching(bidEntry))
                break;
        }
        return remainingQuantity;
    }

    private void createExecution(long executedQuantity, BigDecimal executedPrice, String bidOrderId, String askOrderId, Instant now) {
        LOGGER.info("Execution created executedQuantity: {}, executedPrice {} @{}", executedQuantity, executedPrice, now);
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

    public BigDecimal getBestAsk() {
        return bestAsk.get();
    }

    public BigDecimal getBestBid() {
        return bestBid.get();
    }

    public MutableSortedSet<OrderBookEntry> getBids() {
        return bidSet.asUnmodifiable();
    }

    public MutableSortedSet<OrderBookEntry> getAsks() {
        return askSet.asUnmodifiable();
    }


    public String printOrderBook() {
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

    class OrderBookEntry {
        private final CharSequence orderId;
        private final BigDecimal price;
        private long quantity;
        private final long orderBookEntryTimeInMillis;

        public OrderBookEntry(CharSequence orderId, BigDecimal price, long quantity) {
            this.orderId = orderId;
            this.price = price;
            this.quantity = quantity;
            this.orderBookEntryTimeInMillis = Instant.now().toEpochMilli();
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

        public void setQuantity(long quantity) {
            this.quantity = quantity;
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
