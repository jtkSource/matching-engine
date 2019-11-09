package com.jtk.matching.api;

import com.jtk.matching.api.gen.Order;
import com.jtk.matching.api.gen.enums.OrderType;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static com.jtk.matching.api.gen.enums.ProductType.Bond;

public class TestAvroSchema {

    private final static Logger LOGGER = LoggerFactory.getLogger(TestAvroSchema.class);
    private File store;

    @Before
    public void setup() throws IOException {
    }


    @After
    public void destroy() {
    }

    @Test
    public void test_order_creation_should_return_producttype_bond() {
        Order order = Order.newBuilder()
                .setOrderId(UUID.randomUUID().toString())
                .setProductId("XSS")
                .setProductType(Bond)
                .setOrderType(OrderType.LIMIT)
                .build();
        Assert.assertEquals("order type should be bond", Bond, order.getProductType());
    }

    @Test
    public void serialize_order_creation_should_deserialise_to_same_hash() throws IOException {
        Order order = writeOrderToFile();
        DataFileReader<Order> orderDataFileReader = readOrderFromFile();
        while (orderDataFileReader.hasNext()) {
            Order order1 = orderDataFileReader.next();
            Assert.assertEquals("hashcode should be the same " + order.getOrderId(), order.hashCode(), order1.hashCode());
        }
        if (store != null) {
            store.delete();
        }

    }


    @Test
    public void test_avro_big_decimal_fields() throws IOException {
        Order order = writeOrderToFile();
        DataFileReader<Order> orderDataFileReader = readOrderFromFile();
        while (orderDataFileReader.hasNext()) {
            Order order1 = orderDataFileReader.next();
            Assert.assertEquals("price should be the same " + order.getPrice(), order.getPrice(), order1.getPrice());
            LOGGER.info("Timestamp: {}", order.getOrderCreation());
        }
        if (store != null) {
            store.delete();
        }
    }

    private DataFileReader<Order> readOrderFromFile() throws IOException {
        DatumReader<Order> orderDatumReader = new SpecificDatumReader<>(Order.class);
        return new DataFileReader<>(store, orderDatumReader);
    }

    private Order writeOrderToFile() throws IOException {
        store = File.createTempFile("orders", ".avro");
        LOGGER.info("File path: {}", store.getPath());
        BigDecimal price = new BigDecimal("9988900879009.00099495743594375349536456");
        price.setScale(10, RoundingMode.UP);

        Order order = Order.newBuilder()
                .setOrderId(UUID.randomUUID().toString())
                .setProductId("XSS")
                .setProductType(Bond)
                .setOrderType(OrderType.LIMIT)
                .setPrice(price)
                .setQuantity(999)
                .setOrderCreation(Instant.now())
                .setSubmitDate(LocalDate.now())
                .build();

        LOGGER.info("Order created: {}", order.toString());
        DatumWriter<Order> orderDatumWriter = new SpecificDatumWriter<>(Order.class);
        DataFileWriter<Order> dataFileWriter = new DataFileWriter<>(orderDatumWriter);
        dataFileWriter.create(order.getSchema(), store);
        dataFileWriter.append(order);
        dataFileWriter.close();
        return order;
    }

}
