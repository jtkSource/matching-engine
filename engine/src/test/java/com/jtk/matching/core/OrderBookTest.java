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
import java.util.Iterator;
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
        Assert.assertTrue("There should be three bid orders",book.getBids().size() == 3);
    }

    @Test
    public void add_three_sell_order_to_order_book_should_create_a_list_of_three_asks() {
        String productId = "XSS";
        PriceType pricetype = PriceType.Cash;
        OrderBook book = createOrderBook(productId, pricetype);
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 99.34, 1000, Side.Sell));
        book.addOrder(createOrder(productId, 99.01, 1000, Side.Sell));
        Assert.assertTrue("There should be three sell orders",book.getAsks().size() == 3);
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
        Assert.assertTrue("There should be three buy orders",book.getBids().size() == 4);
        Iterator<OrderBook.OrderBookEntry> it = book.getBids().iterator();
        OrderBook.OrderBookEntry prev = null;
        while (it.hasNext()){
            OrderBook.OrderBookEntry oe = it.next();
            if(prev == null){
                prev = oe;
            }else {
                if(prev.getPrice().compareTo(oe.getPrice()) < 0){
                    Assert.fail("Desc Price priority is not maintained " + prev.getPrice()+" < "+oe.getPrice());
                }else if(prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)){
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis()+" < "+oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }

        LOGGER.info("Ask Orders {}", book.getAsks().makeString("\n"));
        Assert.assertTrue("There should be three sell orders",book.getAsks().size() == 4);
        it = book.getAsks().iterator();
        prev = null;
        while (it.hasNext()){
            OrderBook.OrderBookEntry oe = it.next();
            if(prev == null){
                prev = oe;
            }else {
                if(prev.getPrice().compareTo(oe.getPrice()) > 0){
                    Assert.fail("Asc Price priority is not maintained " + prev.getPrice()+" > "+oe.getPrice());
                }else if(prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)){
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis()+" > "+oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }

        LOGGER.info("Print Book {}", book.printOrderBook());

    }

    @Test
    public void bigDecimal_precision_should_truncate_to_nearest_precision(){
        BigDecimal dec = new BigDecimal(32423599.03451);
        String actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0345 but is " + actual,"32423599.0345", actual);
        dec = new BigDecimal(32423599.03459);
        actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0345 but is "+ actual,"32423599.0345",actual);
        dec = new BigDecimal(32423599.03455);
        actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0345 but is "+ actual,"32423599.0345",actual);
        dec = new BigDecimal(32423599.034);
        actual = dec.setScale(4, RoundingMode.DOWN).toPlainString();
        Assert.assertEquals("precision should be 32423599.0340 but is "+ actual,"32423599.0340",actual);

    }
    @Test
    public void reverse_order_book_sorting_should_sort_bid_price_from_lo_to_hi_and_ask_price_from_hi_to_lo_and_long_to_short_time(){
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
        Assert.assertTrue("There should be three buy orders",book.getBids().size() == 4);
        Iterator<OrderBook.OrderBookEntry> it = book.getBids().iterator();
        OrderBook.OrderBookEntry prev = null;
        while (it.hasNext()){
            OrderBook.OrderBookEntry oe = it.next();
            if(prev == null){
                prev = oe;
            }else {
                if(prev.getPrice().compareTo(oe.getPrice()) > 0){
                    Assert.fail("Asc Price priority is not maintained " + prev.getPrice()+" < "+oe.getPrice());
                }else if(prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)){
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis()+" < "+oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }


        LOGGER.info("Ask Orders {}", book.getAsks().makeString("\n"));
        Assert.assertTrue("There should be three sell orders",book.getAsks().size() == 4);
        it = book.getAsks().iterator();
        prev = null;
        while (it.hasNext()){
            OrderBook.OrderBookEntry oe = it.next();
            if(prev == null){
                prev = oe;
            }else {
                if(prev.getPrice().compareTo(oe.getPrice()) < 0){
                    Assert.fail("Desc Price priority is not maintained " + prev.getPrice()+" > "+oe.getPrice());
                }else if(prev.getOrderBookEntryTimeInMillis() > oe.getOrderBookEntryTimeInMillis() && (prev.getPrice().compareTo(oe.getPrice()) == 0)){
                    LOGGER.error("previous Price {} current Price {}", prev.getPrice(), oe.getPrice());
                    Assert.fail("Asc Time priority is not maintained " + prev.getOrderBookEntryTimeInMillis()+" > "+oe.getOrderBookEntryTimeInMillis());
                }
                prev = oe;
            }
        }
        LOGGER.info("Print Book: {}",book.printOrderBook());

    }


    private Order createOrder(String productId, double price, int quantity, Side side) {
        return Order.newBuilder()
                .setOrderId(UUID.randomUUID().toString())
                .setProductId(productId)
                .setProductType(Bond)
                .setOrderType(OrderType.LIMIT)
                .setPrice(new BigDecimal(price))
                .setQuantity(quantity)
                .setOrderCreation(Instant.now())
                .setSubmitDate(LocalDate.now())
                .setSide(side)
                .build();
    }

    private OrderBook createOrderBook(String productId, PriceType cash) {
        return OrderBook.getInstance(productId, cash);
    }
    private OrderBook createOrderBook(String productId, PriceType cash, boolean reverseSort) {
        return OrderBook.getInstance(productId, cash, reverseSort);
    }
}