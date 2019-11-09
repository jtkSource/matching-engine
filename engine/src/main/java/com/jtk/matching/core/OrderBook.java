package com.jtk.matching.core;

import com.jtk.matching.api.gen.Order;
import com.jtk.matching.api.gen.enums.PriceType;
import com.jtk.matching.api.gen.enums.Side;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

public class OrderBook {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderBook.class);
    private final String productId;
    private final PriceType priceType;
    private boolean reverseOrder;
    private final TreeSortedSet<OrderBookEntry> bidSet;
    private final TreeSortedSet<OrderBookEntry> askSet;
    private final Comparator<OrderBookEntry> descPriceTimeComparator = Comparator.comparing(OrderBookEntry::getPrice, Comparator.reverseOrder())
            .thenComparingLong(OrderBookEntry::getOrderBookEntryTimeInMillis).thenComparing(OrderBookEntry::getOrderId);
    private final Comparator<OrderBookEntry> ascPriceTimeComparator = Comparator.comparing(OrderBookEntry::getPrice)
            .thenComparing(OrderBookEntry::getOrderBookEntryTimeInMillis).thenComparing(OrderBookEntry::getOrderId);

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

    public void addOrder(Order order) {
        if (order.getSide() == Side.Buy) {
            bidSet.add(new OrderBookEntry(order.getOrderId(), order.getPrice()));
        } else {
            askSet.add(new OrderBookEntry(order.getOrderId(), order.getPrice()));
        }
    }

    public MutableSortedSet<OrderBookEntry> getBids() {
        return bidSet.asUnmodifiable();
    }

    public MutableSortedSet<OrderBookEntry> getAsks() {
        return askSet.asUnmodifiable();
    }

    class OrderBookEntry {
        private final CharSequence orderId;
        private final BigDecimal price;
        private final long orderBookEntryTimeInMillis;

        public OrderBookEntry(CharSequence orderId, BigDecimal price) {
            this.orderId = orderId;
            this.price = price;
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
            return "OrderBookOrder{" +
                    "orderId=" + orderId +
                    ", price=" + price +
                    ", orderBookEntryTime=" + orderBookEntryTimeInMillis +
                    '}';
        }
    }
}
