package com.jtk.matching.core;

import static com.jtk.matching.api.avro.AvroUtil.*;
import com.jtk.matching.api.gen.Execution;
import com.jtk.matching.api.gen.Order;
import com.jtk.matching.api.gen.enums.MsgType;
import com.jtk.matching.api.gen.enums.OrderType;
import com.jtk.matching.api.gen.enums.PriceType;
import com.jtk.matching.api.gen.enums.Side;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.multimap.set.sorted.TreeSortedSetMultimap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03, 100.01, 100.34, 100.03);

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
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype, true), 5.01, 5.34, 5.03, 4.01, 4.34, 4.03);

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
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03, 100.01, 100.34, 100.03);

        BigDecimal bestBid = book.getBestBid();
        BigDecimal bestAsk = book.getBestAsk();
        Assert.assertEquals("Best bid should be 99.34000000 but is", "99.34000000", bestBid.toPlainString());
        Assert.assertEquals("Best ask should be 100.01000000 but is", "100.01000000", bestAsk.toPlainString());

    }

    @Test
    public void add_to_order_book_should_return_best_bid_and_best_ask_for_reversedorder() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Spread;
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype, true), 5.01, 5.34, 5.03,
                4.01, 4.34, 4.03);

        BigDecimal bestBid = book.getBestBid();
        BigDecimal bestAsk = book.getBestAsk();
        Assert.assertEquals("Best bid should be 5.01000000", "5.01000000", bestBid.toPlainString());
        Assert.assertEquals("Best ask should be 4.34000000", "4.34000000", bestAsk.toPlainString());

    }


    @Test
    public void add_matching_bid_to_order_book_should_result_in_removing_top_level_on_ask_and_bid_is_partially_executed_at_five_hundered() {
        String productId = "XSS";

        PriceType pricetype = PriceType.Cash;

        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03, 100.01, 100.34, 100.03);

        Order matchingBid = createOrder(productId, 100.01, 1500, Side.Buy);

        book.addOrder(matchingBid);

        LOGGER.info("After Execution {}", book.printOrderBook());

        Assert.assertEquals("ask top level should be 3", 3, book.getAsks().size());

        Assert.assertEquals("best bid Quantity should be 500", 500, book.getBids().getFirst().getQuantity());

        String orderId = book.getBids().getFirst().getOrderId();

        Assert.assertEquals("The remaining best bid should be at top level but it is " + orderId, matchingBid.getOrderId(), orderId);
    }

    @Test
    public void add_matching_bid_should_create_two_executions() throws InterruptedException {

        String productId = "XSS";

        PriceType pricetype = PriceType.Cash;

        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03, 100.01, 100.34, 100.03);

        List<Execution> executionList = new ArrayList<>();

        book.getExecutionProcessor().subscribe(executionList::add);

        Order matchingBid = createOrder(productId, 100.01, 1500, Side.Buy);

        book.addOrder(matchingBid);

        int count = 0;

        while (count < 3) {
            count++;
            Thread.sleep(1);
        }

        LOGGER.info("After Execution {}", book.printOrderBook());
        Assert.assertEquals("There should be two executions created ", 2, executionList.size());
        Assert.assertTrue("Buy side Execution OrderId should be " + matchingBid.getOrderId(), executionList.stream()
                .filter(p -> p.getSide().equals(Side.Buy))
                .filter(p -> p.getOrderId().equals(matchingBid.getOrderId()))
                .count() == 1
        );
        Assert.assertTrue("Executions are at 100.01",
                executionList.get(0).getExecutedPrice().equals(executionList.get(1).getExecutedPrice()) &&
                        executionList.get(0).getExecutedPrice().toPlainString().equals("100.01000000")
        );

    }

    @Test
    public void add_matching_bid_should_create_four_executions() throws InterruptedException {

        String productId = "XSS";

        PriceType pricetype = PriceType.Cash;

        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03, 100.01, 100.34, 100.03);

        List<Execution> executionList = new ArrayList<>();

        book.getExecutionProcessor().subscribe(executionList::add);

        Order matchingBid = createOrder(productId, 100.04, 1500, Side.Buy);

        Assert.assertTrue("Final Best Ask should be 100.01000000", book.getBestAsk().toPlainString().equals("100.01000000"));
        Assert.assertTrue("Top level quantity is 1000 ", book.getAsks().getFirst().getQuantity() == 1000);

        book.addOrder(matchingBid);

        int count = 0;

        while (count < 3) {
            count++;
            Thread.sleep(1);
        }

        LOGGER.info("After Execution {}", book.printOrderBook());
        Assert.assertEquals("There should be four executions created ", 4, executionList.size());
        Assert.assertTrue("Buy side Execution OrderId should be " + matchingBid.getOrderId(), executionList.stream()
                .filter(p -> p.getSide().equals(Side.Buy))
                .filter(p -> p.getOrderId().equals(matchingBid.getOrderId()))
                .count() == 2
        );
        Assert.assertTrue("All executions created at 100.04000000", executionList.stream()
                .filter(p -> p.getExecutedPrice().toPlainString().equals("100.04000000"))
                .count() == 4
        );
        Assert.assertTrue("Final Best Bid should be 100.03000000", book.getAsks().getFirst().getPrice().toPlainString().equals("100.03000000"));

        Assert.assertTrue("Top level Bid quantity is 500 ", book.getAsks().getFirst().getQuantity() == 500);
    }

    @Test
    public void add_matching_ask_to_order_book_should_result_in_removing_first_bid_level_and_reducing_the_second_bidlevel_and_ask_is_fully_executed() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03, 100.01, 100.34, 100.03);

        Order matchingAsk = createOrder(productId, 99.34, 1500, Side.Sell);

        book.addOrder(matchingAsk);

        LOGGER.info("After Execution {}", book.printOrderBook());

        Assert.assertEquals("bid top level should be 3", 3, book.getBids().size());

        Assert.assertEquals("best bid Quantity should be 500", 500, book.getBids().getFirst().getQuantity());

        String orderId = book.getAsks().getFirst().getOrderId();

        Assert.assertNotEquals("The top level ask should be the same but is " + orderId, matchingAsk.getOrderId(), orderId);

    }

    @Test
    public void add_matching_ask_should_create_four_executions() throws InterruptedException {

        String productId = "XSS";

        PriceType pricetype = PriceType.Cash;

        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03, 100.01, 100.34, 100.03);

        List<Execution> executionList = new ArrayList<>();

        book.getExecutionProcessor().subscribe(executionList::add);

        Order matchingAsk = createOrder(productId, 99.33, 1500, Side.Sell);

        Assert.assertTrue("Final Best Bid should be 99.34000000", book.getBestBid().toPlainString().equals("99.34000000"));
        Assert.assertTrue("Top level quantity is 1000 ", book.getBids().getFirst().getQuantity() == 1000);

        book.addOrder(matchingAsk);

        int count = 0;

        while (count < 3) {
            count++;
            Thread.sleep(1);
        }

        LOGGER.info("After Execution {}", book.printOrderBook());
        LOGGER.info("Executions {}", executionList);
        Assert.assertEquals("There should be four executions created ", 4, executionList.size());
        Assert.assertTrue("All sell side executions have same orderId", executionList.stream()
                .filter(p -> p.getSide() == Side.Sell)
                .filter(p -> p.getOrderId().equals(matchingAsk.getOrderId()))
                .count() == 2
        );
        Assert.assertTrue("All executions created at 99.33000000", executionList.stream()
                .filter(p -> p.getExecutedPrice().toPlainString().equals("99.33000000"))
                .count() == 4
        );
        Assert.assertTrue("Final Best Bid should be 99.34000000", book.getBestBid().toPlainString().equals("99.34000000"));

        Assert.assertTrue("Top level Bid quantity is 500 ", book.getBids().getFirst().getQuantity() == 500);
    }


    @Test
    public void ask_order_within_discretionary_offset_must_trigger_negotiation() throws InterruptedException {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03,
                100.01, 100.34, 100.03);
        final List<Pair<OrderBook.OrderBookEntry, OrderBook.OrderBookEntry>> listOfNego = new ArrayList<>();
        book.getNegotiationSource().subscribe(listOfNego::add);
        double price = 99.35;
        book.addOrder(createOrder(productId, price, 1000, Side.Sell));

        LOGGER.info("After {}", book.printOrderBook());
        int count = 0;
        while (listOfNego.size() < 2 && count < 3) {
            count++;
            Thread.sleep(1); // visibility to
        }

        Assert.assertEquals("There should 2 orders to negotiate against but there is " + listOfNego.size(),
                2, listOfNego.size());
        Assert.assertTrue("Price is within DO of 0.01", price - listOfNego.get(0).getTwo().getPrice().doubleValue() <= 0.01);
        Assert.assertTrue("Price is within DO of 0.01", price - listOfNego.get(1).getTwo().getPrice().doubleValue() <= 0.01);

    }


    @Test
    public void bid_order_within_discretionary_offset_must_trigger_negotiation() throws InterruptedException {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03,
                100.01, 100.34, 100.03);
        final List<Pair<OrderBook.OrderBookEntry, OrderBook.OrderBookEntry>> listOfNego = new ArrayList<>();
        book.getNegotiationSource().subscribe(listOfNego::add);
        double price = 100.00;
        book.addOrder(createOrder(productId, price, 1000, Side.Buy));

        LOGGER.info("After {}", book.printOrderBook());

        int count = 0;
        while (listOfNego.size() < 1 && count < 3) {
            count++;
            Thread.sleep(1);
        }

        Assert.assertEquals("There should 1 orders to negotiate against but there is " + listOfNego.size(),
                1, listOfNego.size());
        Assert.assertTrue("Price is within DO of 0.01", listOfNego.get(0).getTwo().getPrice().subtract(BigDecimal.valueOf(price)).toString().startsWith("0.01"));
    }

    @Test
    public void top_level_subscription_should_stream_top_level_prices_in_order_of_order_entry() throws InterruptedException {

        OrderBook book = createOrderBook("XSS", PriceType.Cash);

        List<Pair<Optional<BigDecimal>, Optional<BigDecimal>>> topLevelList = new ArrayList<>();

        book.getTopLevelPriceSource().subscribe(topLevelList::add);

        createTestOrderBook("XSS", book, 99.01, 99.34, 99.03,
                100.34, 100.03, 100.01);

        int count = 0;

        while (topLevelList.size() < 3 && count < 3) {
            count++;
            Thread.sleep(1);
        }
        Assert.assertEquals("There should be 8 top-level market data published ", 8, topLevelList.size());

        Assert.assertTrue("Second Bid should replace the first bid in top level",
                topLevelList.get(0).getOne().get().compareTo(topLevelList.get(1).getOne().get()) != 0);

        List<Pair<Optional<BigDecimal>, Optional<BigDecimal>>> bidList = topLevelList.subList(1, topLevelList.size());
        BigDecimal bestBid = topLevelList.get(topLevelList.size() - 1).getOne().orElse(BigDecimal.ZERO);
        int bids = (int) topLevelList.stream()
                .filter(p -> p.getOne().orElse(BigDecimal.ZERO).equals(bestBid))
                .count();
        Assert.assertTrue("Second Bid is best Bid ", bids == bidList.size());

        BigDecimal bestAsk = topLevelList.get(topLevelList.size() - 1).getTwo().orElse(BigDecimal.ZERO);

        int totalBestAsksCount = (int) topLevelList.stream()
                .filter(p -> p.getTwo().orElse(BigDecimal.ZERO).equals(bestAsk))
                .count();

        List<Pair<Optional<BigDecimal>, Optional<BigDecimal>>> askList = topLevelList.subList(topLevelList.size() - 2,
                topLevelList.size());

        Assert.assertTrue("Last two asks are best ask Bid ", totalBestAsksCount == askList.size());

    }

    @Test
    public void top_level_subscription_should_stream_no_levels_when_there_is_no_orders() throws InterruptedException {
        OrderBook book = createOrderBook("XSS", PriceType.Cash);

        List<Pair<Optional<BigDecimal>, Optional<BigDecimal>>> topLevelList = new ArrayList<>();

        book.getTopLevelPriceSource().subscribe(topLevelList::add);

        int count = 0;

        while (count < 3) {
            count++;
            Thread.sleep(1);
        }
        Assert.assertEquals("There should be 0 top-level market data published ", 0, topLevelList.size());

    }

    @Test(expected = IllegalArgumentException.class)
    public void cancel_order_should_throw_exception_when_calling_wrong_method() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03,
                100.01, 100.34, 100.03);
        Order order = createOrder(productId, 99.02, 400, Side.Buy);
        book.addOrder(order);
        LOGGER.info("After {}", book.printOrderBook());
        Order cancelOrder = Order.newBuilder(order)
                .setMsgType(MsgType.Cancel)
                .setPrice(order.getPrice().clear()) // Avro builder doesnt reset the buffer when copying
                .build();
        book.addOrder(cancelOrder);
        LOGGER.info("After {}", book.printOrderBook());
    }

    @Test
    public void cancel_bid_order_should_remove_bid_order_in_orderbook() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03,
                100.01, 100.34, 100.03);
        Order order = createOrder(productId, 99.35, 400, Side.Buy);
        book.addOrder(order);
        Assert.assertTrue("Best bid should be 99.35000000",book.getBestBid().toPlainString().equals("99.35000000"));
        LOGGER.info("After {}", book.printOrderBook());
        Order cancelOrder = Order.newBuilder(order)
                .setMsgType(MsgType.Cancel)
                .setPrice(order.getPrice().clear()) // Avro builder doesnt reset the buffer when copying
                .build();
        boolean cancelled = book.cancelOrder(cancelOrder);
        Assert.assertTrue("Order should be cancelled", cancelled);
        Assert.assertTrue("Best Bid should be 99.34000000",book.getBestBid().toPlainString().equals("99.34000000"));
        LOGGER.info("After {}", book.printOrderBook());
    }


    @Test
    public void cancel_ask_order_should_remove_ask_order_in_orderbook() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createTestOrderBook(productId, createOrderBook(productId, pricetype), 99.01, 99.34, 99.03,
                100.01, 100.34, 100.03);
        Order order = createOrder(productId, 100.00, 400, Side.Sell);
        book.addOrder(order);
        Assert.assertTrue("Best ask should be 100.00000000",book.getBestAsk().toPlainString().equals("100.00000000"));
        LOGGER.info("After {}", book.printOrderBook());
        Order cancelOrder = Order.newBuilder(order)
                .setMsgType(MsgType.Cancel)
                .setPrice(order.getPrice().clear()) // Avro builder doesnt reset the buffer when copying
                .build();
        boolean cancelled = book.cancelOrder(cancelOrder);
        Assert.assertTrue("Order should be cancelled", cancelled);
        Assert.assertTrue("Best Ask should be 100.01000000",book.getBestAsk().toPlainString().equals("100.01000000"));
        LOGGER.info("After {}", book.printOrderBook());
    }


    private OrderBook createTestOrderBook(String productId, OrderBook orderBook, double bid1, double bid2, double bid3, double ask1, double ask2, double ask3) {
        OrderBook book = orderBook;

        book.addOrder(createOrder(productId, bid1, 1000, Side.Buy));
        book.addOrder(createOrder(productId, bid2, 1000, Side.Buy));
        book.addOrder(createOrder(productId, bid3, 1000, Side.Buy));
        book.addOrder(createOrder(productId, bid2, 1000, Side.Buy));

        book.addOrder(createOrder(productId, ask1, 1000, Side.Sell));
        book.addOrder(createOrder(productId, ask2, 1000, Side.Sell));
        book.addOrder(createOrder(productId, ask3, 1000, Side.Sell));
        book.addOrder(createOrder(productId, ask2, 1000, Side.Sell));


        LOGGER.info("Before {}", book.printOrderBook());
        return book;
    }

    private Order createOrder(String productId, double price, int quantity, Side side) {
        BigDecimal priceInBigDecimal = new BigDecimal(String.valueOf(price)).setScale(8);
        return Order.newBuilder()
                .setOrderId(UUID.randomUUID().toString())
                .setProductId(productId)
                .setProductType(Bond)
                .setOrderType(OrderType.LIMIT)
                .setPrice(convertToByteBuffer(priceInBigDecimal, 8))
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