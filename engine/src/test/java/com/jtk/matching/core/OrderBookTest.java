package com.jtk.matching.core;

import com.jtk.matching.api.gen.Order;
import com.jtk.matching.api.gen.enums.OrderType;
import com.jtk.matching.api.gen.enums.PriceType;
import com.jtk.matching.api.gen.enums.Side;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.jtk.matching.api.gen.enums.ProductType.Bond;

public class OrderBookTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(OrderBookTest.class);

    @Test
    public void create_orderBook_based_on_productID_and_priceType() {
        String productId = "XSS";
        PriceType cash = PriceType.Cash;
        OrderBook book = createOrderBook(productId, cash);
        Assert.assertEquals("OrderBook should have productId", book.getProductId(), productId);
        Assert.assertEquals("OrderBook should have priceType", book.getPriceType(), cash);
    }

    @Test
    public void add_three_bid_order_to_order_book_should_create_a_list_of_three_bids() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        Assert.assertTrue("There should be three bid orders", book.getBids().size() == 3);
    }

    @Test
    public void add_three_sell_order_to_order_book_should_create_a_list_of_three_asks() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Sell));
        Assert.assertTrue("There should be three sell orders", book.getAsks().size() == 3);
    }

    @Test
    public void add_to_order_book_should_sort_bid_from_hi_to_low_price_and_ask_from_low_to_hi_and_long_to_short_time() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 100.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.03, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));

        LOGGER.info("Bid Orders {}", book.getBids().makeString("\n"));
        Assert.assertTrue("There should be three buy orders", book.getBids().size() == 4);
        Iterator<OrderBook.OrderBookEntry> it = book.getBids().iterator();
        OrderBook.OrderBookEntry prev = null;
        while (it.hasNext()) {
            OrderBook.OrderBookEntry oe = it.next();
            if (prev == null) {
                prev = oe;
            } else {
                if (prev.getPrice().compareTo(oe.getPrice()) < 0) {
                    Assert.fail("Desc Price priority is not maintained " + prev.getPrice() + " < " + oe.getPrice());
                } else if (prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)) {
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis() + " < " + oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }

        LOGGER.info("Ask Orders {}", book.getAsks().makeString("\n"));
        Assert.assertTrue("There should be three sell orders", book.getAsks().size() == 4);
        it = book.getAsks().iterator();
        prev = null;
        while (it.hasNext()) {
            OrderBook.OrderBookEntry oe = it.next();
            if (prev == null) {
                prev = oe;
            } else {
                if (prev.getPrice().compareTo(oe.getPrice()) > 0) {
                    Assert.fail("Asc Price priority is not maintained " + prev.getPrice() + " > " + oe.getPrice());
                } else if (prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)) {
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis() + " > " + oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }

        LOGGER.info("Print Book {}", book.printOrderBook());

    }

    @Test
    public void bigDecimal_precision_should_truncate_to_nearest_precision() {
        BigDecimal dec = new BigDecimal(32423599.03451);
        String actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0345 but is " + actual, "32423599.0345", actual);
        dec = new BigDecimal(32423599.03459);
        actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0345 but is " + actual, "32423599.0345", actual);
        dec = new BigDecimal(32423599.03455);
        actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0345 but is " + actual, "32423599.0345", actual);
        dec = new BigDecimal(32423599.034);
        actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0340 but is " + actual, "32423599.0340", actual);

    }

    @Test
    public void reverse_order_book_sorting_should_sort_bid_price_from_lo_to_hi_and_ask_price_from_hi_to_lo_and_long_to_short_time() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Spread;
        OrderBook book = createOrderBook(productId, pricetype, true);
        book.addOrder(createOrder(productId, 5.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 5.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 5.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 5.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 4.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 4.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 4.03, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 4.34, 1000, Side.Sell));

        LOGGER.info("Bid Orders {}", book.getBids().makeString("\n"));
        Assert.assertTrue("There should be three buy orders", book.getBids().size() == 4);
        Iterator<OrderBook.OrderBookEntry> it = book.getBids().iterator();
        OrderBook.OrderBookEntry prev = null;
        while (it.hasNext()) {
            OrderBook.OrderBookEntry oe = it.next();
            if (prev == null) {
                prev = oe;
            } else {
                if (prev.getPrice().compareTo(oe.getPrice()) > 0) {
                    Assert.fail("Asc Price priority is not maintained " + prev.getPrice() + " < " + oe.getPrice());
                } else if (prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)) {
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis() + " < " + oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }


        LOGGER.info("Ask Orders {}", book.getAsks().makeString("\n"));
        Assert.assertTrue("There should be three sell orders", book.getAsks().size() == 4);
        it = book.getAsks().iterator();
        prev = null;
        while (it.hasNext()) {
            OrderBook.OrderBookEntry oe = it.next();
            if (prev == null) {
                prev = oe;
            } else {
                if (prev.getPrice().compareTo(oe.getPrice()) < 0) {
                    Assert.fail("Desc Price priority is not maintained " + prev.getPrice() + " > " + oe.getPrice());
                } else if (prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)) {
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis() + " > " + oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }
        LOGGER.info("Print Book: {}", book.printOrderBook());

    }

    @Test
    public void add_to_order_book_should_return_best_bid_and_best_ask() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 100.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.03, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));

        BigDecimal bestBid = book.getBestBid();
        BigDecimal bestAsk = book.getBestAsk();
        Assert.assertEquals("Best bid should be 99.34000000 but is", "99.34000000", bestBid.toPlainString());
        Assert.assertEquals("Best ask should be 100.01000000 but is", "100.01000000", bestAsk.toPlainString());

    }

    @Test
    public void add_to_order_book_should_return_best_bid_and_best_ask_for_reversedorder() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Spread;
        OrderBook book = createOrderBook(productId, pricetype, true);
        book.addOrder(createOrder(productId, 5.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 5.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 5.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 5.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 4.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 4.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 4.03, 1000, Side.Sell));

        BigDecimal bestBid = book.getBestBid();
        BigDecimal bestAsk = book.getBestAsk();
        Assert.assertEquals("Best bid should be 5.01000000", "5.01000000", bestBid.toPlainString());
        Assert.assertEquals("Best ask should be 4.34000000", "4.34000000", bestAsk.toPlainString());

    }


    @Test
    public void add_matching_bid_to_order_book_should_result_in_removing_top_level_on_ask_and_bid_is_partially_executed_at_five_hundered() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 100.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.03, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));

        LOGGER.info("Before Execution {}", book.printOrderBook());

        Order matchingBid = createOrder(productId, 100.01, 1500, Side.Buy);
        book.addOrder(matchingBid);

        LOGGER.info("After Execution {}", book.printOrderBook());

        Assert.assertEquals("ask top level should be 3", 3, book.getAsks().size());

        Assert.assertEquals("best bid Quantity should be 500", 500, book.getBids().getFirst().getQuantity());

        String orderId = book.getBids().getFirst().getOrderId();
        Assert.assertEquals("The remaining best bid should be at top level but it is " + orderId, matchingBid.getOrderId(), orderId);
    }

    @Test
    public void add_matching_ask_to_order_book_should_result_in_removing_first_bid_level_and_reducing_the_second_bidlevel_and_ask_is_fully_executed() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 100.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.03, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));

        LOGGER.info("Before Execution {}", book.printOrderBook());

        Order matchingAsk = createOrder(productId, 99.34, 1500, Side.Sell);
        book.addOrder(matchingAsk);

        LOGGER.info("After Execution {}", book.printOrderBook());

        Assert.assertEquals("bid top level should be 3", 3, book.getBids().size());

        Assert.assertEquals("best bid Quantity should be 500", 500, book.getBids().getFirst().getQuantity());

        String orderId = book.getAsks().getFirst().getOrderId();

        Assert.assertNotEquals("The top level ask should be the same but is " + orderId, matchingAsk.getOrderId(), orderId);

    }

    @Test
    public void ask_order_within_discretionary_offset_must_trigger_negotiation(){
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        List<OrderBook.OrderBookEntry> listOfNego = new ArrayList<>();
        book.getNegotiationFlux().subscribe(listOfNego::add);

        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 100.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.03, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));

        LOGGER.info("Before {}", book.printOrderBook());

        double price = 99.35;
        book.addOrder(createOrder(productId, price,1000,Side.Sell));

        LOGGER.info("After {}", book.printOrderBook());

        Assert.assertEquals("There should 2 orders to negotiate against but there is "+listOfNego.size(),
                2, listOfNego.size());
        Assert.assertTrue("Price is within DO of 0.01",price - listOfNego.get(0).getPrice().doubleValue() <= 0.01);
        Assert.assertTrue("Price is within DO of 0.01",price - listOfNego.get(1).getPrice().doubleValue() <= 0.01);

    }


    @Test
    public void bid_order_within_discretionary_offset_must_trigger_negotiation(){
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        List<OrderBook.OrderBookEntry> listOfNego = new ArrayList<>();
        book.getNegotiationFlux().subscribe(listOfNego::add);

        book.addOrder(createOrder(productId, 99.01, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.03, 1000, Side.Buy));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Buy));

        book.addOrder(createOrder(productId, 100.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.03, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 100.34, 1000, Side.Sell));

        LOGGER.info("Before {}", book.printOrderBook());

        double price = 100.00;
        book.addOrder(createOrder(productId, price,1000,Side.Buy));

        LOGGER.info("After {}", book.printOrderBook());

        Assert.assertEquals("There should 1 orders to negotiate against but there is "+listOfNego.size(),
                1, listOfNego.size());
        Assert.assertTrue("Price is within DO of 0.01",listOfNego.get(0).getPrice().subtract(BigDecimal.valueOf(price)).toString().startsWith("0.01"));
    }

    private Order createOrder(String productId, double price, int quantity, Side side) {
        return Order.newBuilder()
                .setOrderId(UUID.randomUUID().toString())
                .setProductId(productId)
                .setProductType(Bond)
                .setOrderType(OrderType.LIMIT)
                .setPrice(new BigDecimal(String.valueOf(price)))
                .setQuantity(quantity)
                .setOrderCreation(Instant.now())
                .setSubmitDate(LocalDate.now())
                .setSide(side)
                .setDiscretionaryOffset(0.01)
                .build();
    }

    private OrderBook createOrderBook(String productId, PriceType cash) {
        return OrderBook.getInstance(productId, cash);
    }

    private OrderBook createOrderBook(String productId, PriceType cash, boolean reverseSort) {
        return OrderBook.getInstance(productId, cash, reverseSort);
    }
}