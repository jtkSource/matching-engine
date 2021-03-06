/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.jtk.matching.api.gen;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Order extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 3772051583310984264L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Order\",\"namespace\":\"com.jtk.matching.api.gen\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"productId\",\"type\":\"string\"},{\"name\":\"orderType\",\"type\":{\"type\":\"enum\",\"name\":\"OrderType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"LIMIT\",\"MKT\"]},\"default\":\"LIMIT\"},{\"name\":\"productType\",\"type\":{\"type\":\"enum\",\"name\":\"ProductType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"Bond\",\"Repo\"]}},{\"name\":\"priceType\",\"type\":{\"type\":\"enum\",\"name\":\"PriceType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"Cash\",\"Spread\"]},\"default\":\"Cash\"},{\"name\":\"side\",\"type\":{\"type\":\"enum\",\"name\":\"Side\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"Buy\",\"Sell\"]}},{\"name\":\"msgType\",\"type\":{\"type\":\"enum\",\"name\":\"MsgType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"New\",\"Amend\",\"Cancel\",\"IOI\"]},\"default\":\"New\"},{\"name\":\"orderCreation\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"submitDate\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"price\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":8,\"scale\":8},\"default\":\"\\u0000\"},{\"name\":\"discretionaryOffset\",\"type\":\"double\",\"default\":0.0},{\"name\":\"quantity\",\"type\":\"long\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();
static {
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.DateConversion());
    MODEL$.addLogicalTypeConversion(new org.apache.avro.data.TimeConversions.TimestampMillisConversion());
  }

  private static final BinaryMessageEncoder<Order> ENCODER =
      new BinaryMessageEncoder<Order>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Order> DECODER =
      new BinaryMessageDecoder<Order>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Order> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Order> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Order> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Order>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Order to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Order from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Order instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Order fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  @Deprecated public java.lang.CharSequence orderId;
  @Deprecated public java.lang.CharSequence productId;
  @Deprecated public com.jtk.matching.api.gen.enums.OrderType orderType;
  @Deprecated public com.jtk.matching.api.gen.enums.ProductType productType;
  @Deprecated public com.jtk.matching.api.gen.enums.PriceType priceType;
  @Deprecated public com.jtk.matching.api.gen.enums.Side side;
  @Deprecated public com.jtk.matching.api.gen.enums.MsgType msgType;
  @Deprecated public java.time.Instant orderCreation;
  @Deprecated public java.time.LocalDate submitDate;
  @Deprecated public java.nio.ByteBuffer price;
  @Deprecated public double discretionaryOffset;
  @Deprecated public long quantity;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Order() {}

  /**
   * All-args constructor.
   * @param orderId The new value for orderId
   * @param productId The new value for productId
   * @param orderType The new value for orderType
   * @param productType The new value for productType
   * @param priceType The new value for priceType
   * @param side The new value for side
   * @param msgType The new value for msgType
   * @param orderCreation The new value for orderCreation
   * @param submitDate The new value for submitDate
   * @param price The new value for price
   * @param discretionaryOffset The new value for discretionaryOffset
   * @param quantity The new value for quantity
   */
  public Order(java.lang.CharSequence orderId, java.lang.CharSequence productId, com.jtk.matching.api.gen.enums.OrderType orderType, com.jtk.matching.api.gen.enums.ProductType productType, com.jtk.matching.api.gen.enums.PriceType priceType, com.jtk.matching.api.gen.enums.Side side, com.jtk.matching.api.gen.enums.MsgType msgType, java.time.Instant orderCreation, java.time.LocalDate submitDate, java.nio.ByteBuffer price, java.lang.Double discretionaryOffset, java.lang.Long quantity) {
    this.orderId = orderId;
    this.productId = productId;
    this.orderType = orderType;
    this.productType = productType;
    this.priceType = priceType;
    this.side = side;
    this.msgType = msgType;
    this.orderCreation = orderCreation.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
    this.submitDate = submitDate;
    this.price = price;
    this.discretionaryOffset = discretionaryOffset;
    this.quantity = quantity;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return orderId;
    case 1: return productId;
    case 2: return orderType;
    case 3: return productType;
    case 4: return priceType;
    case 5: return side;
    case 6: return msgType;
    case 7: return orderCreation;
    case 8: return submitDate;
    case 9: return price;
    case 10: return discretionaryOffset;
    case 11: return quantity;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  private static final org.apache.avro.Conversion<?>[] conversions =
      new org.apache.avro.Conversion<?>[] {
      null,
      null,
      null,
      null,
      null,
      null,
      null,
      new org.apache.avro.data.TimeConversions.TimestampMillisConversion(),
      new org.apache.avro.data.TimeConversions.DateConversion(),
      null,
      null,
      null,
      null
  };

  @Override
  public org.apache.avro.Conversion<?> getConversion(int field) {
    return conversions[field];
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: orderId = (java.lang.CharSequence)value$; break;
    case 1: productId = (java.lang.CharSequence)value$; break;
    case 2: orderType = (com.jtk.matching.api.gen.enums.OrderType)value$; break;
    case 3: productType = (com.jtk.matching.api.gen.enums.ProductType)value$; break;
    case 4: priceType = (com.jtk.matching.api.gen.enums.PriceType)value$; break;
    case 5: side = (com.jtk.matching.api.gen.enums.Side)value$; break;
    case 6: msgType = (com.jtk.matching.api.gen.enums.MsgType)value$; break;
    case 7: orderCreation = (java.time.Instant)value$; break;
    case 8: submitDate = (java.time.LocalDate)value$; break;
    case 9: price = (java.nio.ByteBuffer)value$; break;
    case 10: discretionaryOffset = (java.lang.Double)value$; break;
    case 11: quantity = (java.lang.Long)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'orderId' field.
   * @return The value of the 'orderId' field.
   */
  public java.lang.CharSequence getOrderId() {
    return orderId;
  }


  /**
   * Sets the value of the 'orderId' field.
   * @param value the value to set.
   */
  public void setOrderId(java.lang.CharSequence value) {
    this.orderId = value;
  }

  /**
   * Gets the value of the 'productId' field.
   * @return The value of the 'productId' field.
   */
  public java.lang.CharSequence getProductId() {
    return productId;
  }


  /**
   * Sets the value of the 'productId' field.
   * @param value the value to set.
   */
  public void setProductId(java.lang.CharSequence value) {
    this.productId = value;
  }

  /**
   * Gets the value of the 'orderType' field.
   * @return The value of the 'orderType' field.
   */
  public com.jtk.matching.api.gen.enums.OrderType getOrderType() {
    return orderType;
  }


  /**
   * Sets the value of the 'orderType' field.
   * @param value the value to set.
   */
  public void setOrderType(com.jtk.matching.api.gen.enums.OrderType value) {
    this.orderType = value;
  }

  /**
   * Gets the value of the 'productType' field.
   * @return The value of the 'productType' field.
   */
  public com.jtk.matching.api.gen.enums.ProductType getProductType() {
    return productType;
  }


  /**
   * Sets the value of the 'productType' field.
   * @param value the value to set.
   */
  public void setProductType(com.jtk.matching.api.gen.enums.ProductType value) {
    this.productType = value;
  }

  /**
   * Gets the value of the 'priceType' field.
   * @return The value of the 'priceType' field.
   */
  public com.jtk.matching.api.gen.enums.PriceType getPriceType() {
    return priceType;
  }


  /**
   * Sets the value of the 'priceType' field.
   * @param value the value to set.
   */
  public void setPriceType(com.jtk.matching.api.gen.enums.PriceType value) {
    this.priceType = value;
  }

  /**
   * Gets the value of the 'side' field.
   * @return The value of the 'side' field.
   */
  public com.jtk.matching.api.gen.enums.Side getSide() {
    return side;
  }


  /**
   * Sets the value of the 'side' field.
   * @param value the value to set.
   */
  public void setSide(com.jtk.matching.api.gen.enums.Side value) {
    this.side = value;
  }

  /**
   * Gets the value of the 'msgType' field.
   * @return The value of the 'msgType' field.
   */
  public com.jtk.matching.api.gen.enums.MsgType getMsgType() {
    return msgType;
  }


  /**
   * Sets the value of the 'msgType' field.
   * @param value the value to set.
   */
  public void setMsgType(com.jtk.matching.api.gen.enums.MsgType value) {
    this.msgType = value;
  }

  /**
   * Gets the value of the 'orderCreation' field.
   * @return The value of the 'orderCreation' field.
   */
  public java.time.Instant getOrderCreation() {
    return orderCreation;
  }


  /**
   * Sets the value of the 'orderCreation' field.
   * @param value the value to set.
   */
  public void setOrderCreation(java.time.Instant value) {
    this.orderCreation = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
  }

  /**
   * Gets the value of the 'submitDate' field.
   * @return The value of the 'submitDate' field.
   */
  public java.time.LocalDate getSubmitDate() {
    return submitDate;
  }


  /**
   * Sets the value of the 'submitDate' field.
   * @param value the value to set.
   */
  public void setSubmitDate(java.time.LocalDate value) {
    this.submitDate = value;
  }

  /**
   * Gets the value of the 'price' field.
   * @return The value of the 'price' field.
   */
  public java.nio.ByteBuffer getPrice() {
    return price;
  }


  /**
   * Sets the value of the 'price' field.
   * @param value the value to set.
   */
  public void setPrice(java.nio.ByteBuffer value) {
    this.price = value;
  }

  /**
   * Gets the value of the 'discretionaryOffset' field.
   * @return The value of the 'discretionaryOffset' field.
   */
  public double getDiscretionaryOffset() {
    return discretionaryOffset;
  }


  /**
   * Sets the value of the 'discretionaryOffset' field.
   * @param value the value to set.
   */
  public void setDiscretionaryOffset(double value) {
    this.discretionaryOffset = value;
  }

  /**
   * Gets the value of the 'quantity' field.
   * @return The value of the 'quantity' field.
   */
  public long getQuantity() {
    return quantity;
  }


  /**
   * Sets the value of the 'quantity' field.
   * @param value the value to set.
   */
  public void setQuantity(long value) {
    this.quantity = value;
  }

  /**
   * Creates a new Order RecordBuilder.
   * @return A new Order RecordBuilder
   */
  public static com.jtk.matching.api.gen.Order.Builder newBuilder() {
    return new com.jtk.matching.api.gen.Order.Builder();
  }

  /**
   * Creates a new Order RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Order RecordBuilder
   */
  public static com.jtk.matching.api.gen.Order.Builder newBuilder(com.jtk.matching.api.gen.Order.Builder other) {
    if (other == null) {
      return new com.jtk.matching.api.gen.Order.Builder();
    } else {
      return new com.jtk.matching.api.gen.Order.Builder(other);
    }
  }

  /**
   * Creates a new Order RecordBuilder by copying an existing Order instance.
   * @param other The existing instance to copy.
   * @return A new Order RecordBuilder
   */
  public static com.jtk.matching.api.gen.Order.Builder newBuilder(com.jtk.matching.api.gen.Order other) {
    if (other == null) {
      return new com.jtk.matching.api.gen.Order.Builder();
    } else {
      return new com.jtk.matching.api.gen.Order.Builder(other);
    }
  }

  /**
   * RecordBuilder for Order instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Order>
    implements org.apache.avro.data.RecordBuilder<Order> {

    private java.lang.CharSequence orderId;
    private java.lang.CharSequence productId;
    private com.jtk.matching.api.gen.enums.OrderType orderType;
    private com.jtk.matching.api.gen.enums.ProductType productType;
    private com.jtk.matching.api.gen.enums.PriceType priceType;
    private com.jtk.matching.api.gen.enums.Side side;
    private com.jtk.matching.api.gen.enums.MsgType msgType;
    private java.time.Instant orderCreation;
    private java.time.LocalDate submitDate;
    private java.nio.ByteBuffer price;
    private double discretionaryOffset;
    private long quantity;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.jtk.matching.api.gen.Order.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.orderId)) {
        this.orderId = data().deepCopy(fields()[0].schema(), other.orderId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.productId)) {
        this.productId = data().deepCopy(fields()[1].schema(), other.productId);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.orderType)) {
        this.orderType = data().deepCopy(fields()[2].schema(), other.orderType);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.productType)) {
        this.productType = data().deepCopy(fields()[3].schema(), other.productType);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.priceType)) {
        this.priceType = data().deepCopy(fields()[4].schema(), other.priceType);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.side)) {
        this.side = data().deepCopy(fields()[5].schema(), other.side);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.msgType)) {
        this.msgType = data().deepCopy(fields()[6].schema(), other.msgType);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.orderCreation)) {
        this.orderCreation = data().deepCopy(fields()[7].schema(), other.orderCreation);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
      if (isValidValue(fields()[8], other.submitDate)) {
        this.submitDate = data().deepCopy(fields()[8].schema(), other.submitDate);
        fieldSetFlags()[8] = other.fieldSetFlags()[8];
      }
      if (isValidValue(fields()[9], other.price)) {
        this.price = data().deepCopy(fields()[9].schema(), other.price);
        fieldSetFlags()[9] = other.fieldSetFlags()[9];
      }
      if (isValidValue(fields()[10], other.discretionaryOffset)) {
        this.discretionaryOffset = data().deepCopy(fields()[10].schema(), other.discretionaryOffset);
        fieldSetFlags()[10] = other.fieldSetFlags()[10];
      }
      if (isValidValue(fields()[11], other.quantity)) {
        this.quantity = data().deepCopy(fields()[11].schema(), other.quantity);
        fieldSetFlags()[11] = other.fieldSetFlags()[11];
      }
    }

    /**
     * Creates a Builder by copying an existing Order instance
     * @param other The existing instance to copy.
     */
    private Builder(com.jtk.matching.api.gen.Order other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.orderId)) {
        this.orderId = data().deepCopy(fields()[0].schema(), other.orderId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.productId)) {
        this.productId = data().deepCopy(fields()[1].schema(), other.productId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.orderType)) {
        this.orderType = data().deepCopy(fields()[2].schema(), other.orderType);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.productType)) {
        this.productType = data().deepCopy(fields()[3].schema(), other.productType);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.priceType)) {
        this.priceType = data().deepCopy(fields()[4].schema(), other.priceType);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.side)) {
        this.side = data().deepCopy(fields()[5].schema(), other.side);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.msgType)) {
        this.msgType = data().deepCopy(fields()[6].schema(), other.msgType);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.orderCreation)) {
        this.orderCreation = data().deepCopy(fields()[7].schema(), other.orderCreation);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.submitDate)) {
        this.submitDate = data().deepCopy(fields()[8].schema(), other.submitDate);
        fieldSetFlags()[8] = true;
      }
      if (isValidValue(fields()[9], other.price)) {
        this.price = data().deepCopy(fields()[9].schema(), other.price);
        fieldSetFlags()[9] = true;
      }
      if (isValidValue(fields()[10], other.discretionaryOffset)) {
        this.discretionaryOffset = data().deepCopy(fields()[10].schema(), other.discretionaryOffset);
        fieldSetFlags()[10] = true;
      }
      if (isValidValue(fields()[11], other.quantity)) {
        this.quantity = data().deepCopy(fields()[11].schema(), other.quantity);
        fieldSetFlags()[11] = true;
      }
    }

    /**
      * Gets the value of the 'orderId' field.
      * @return The value.
      */
    public java.lang.CharSequence getOrderId() {
      return orderId;
    }


    /**
      * Sets the value of the 'orderId' field.
      * @param value The value of 'orderId'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setOrderId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.orderId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'orderId' field has been set.
      * @return True if the 'orderId' field has been set, false otherwise.
      */
    public boolean hasOrderId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'orderId' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearOrderId() {
      orderId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'productId' field.
      * @return The value.
      */
    public java.lang.CharSequence getProductId() {
      return productId;
    }


    /**
      * Sets the value of the 'productId' field.
      * @param value The value of 'productId'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setProductId(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.productId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'productId' field has been set.
      * @return True if the 'productId' field has been set, false otherwise.
      */
    public boolean hasProductId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'productId' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearProductId() {
      productId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'orderType' field.
      * @return The value.
      */
    public com.jtk.matching.api.gen.enums.OrderType getOrderType() {
      return orderType;
    }


    /**
      * Sets the value of the 'orderType' field.
      * @param value The value of 'orderType'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setOrderType(com.jtk.matching.api.gen.enums.OrderType value) {
      validate(fields()[2], value);
      this.orderType = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'orderType' field has been set.
      * @return True if the 'orderType' field has been set, false otherwise.
      */
    public boolean hasOrderType() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'orderType' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearOrderType() {
      orderType = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'productType' field.
      * @return The value.
      */
    public com.jtk.matching.api.gen.enums.ProductType getProductType() {
      return productType;
    }


    /**
      * Sets the value of the 'productType' field.
      * @param value The value of 'productType'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setProductType(com.jtk.matching.api.gen.enums.ProductType value) {
      validate(fields()[3], value);
      this.productType = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'productType' field has been set.
      * @return True if the 'productType' field has been set, false otherwise.
      */
    public boolean hasProductType() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'productType' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearProductType() {
      productType = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'priceType' field.
      * @return The value.
      */
    public com.jtk.matching.api.gen.enums.PriceType getPriceType() {
      return priceType;
    }


    /**
      * Sets the value of the 'priceType' field.
      * @param value The value of 'priceType'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setPriceType(com.jtk.matching.api.gen.enums.PriceType value) {
      validate(fields()[4], value);
      this.priceType = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'priceType' field has been set.
      * @return True if the 'priceType' field has been set, false otherwise.
      */
    public boolean hasPriceType() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'priceType' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearPriceType() {
      priceType = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'side' field.
      * @return The value.
      */
    public com.jtk.matching.api.gen.enums.Side getSide() {
      return side;
    }


    /**
      * Sets the value of the 'side' field.
      * @param value The value of 'side'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setSide(com.jtk.matching.api.gen.enums.Side value) {
      validate(fields()[5], value);
      this.side = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'side' field has been set.
      * @return True if the 'side' field has been set, false otherwise.
      */
    public boolean hasSide() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'side' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearSide() {
      side = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'msgType' field.
      * @return The value.
      */
    public com.jtk.matching.api.gen.enums.MsgType getMsgType() {
      return msgType;
    }


    /**
      * Sets the value of the 'msgType' field.
      * @param value The value of 'msgType'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setMsgType(com.jtk.matching.api.gen.enums.MsgType value) {
      validate(fields()[6], value);
      this.msgType = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'msgType' field has been set.
      * @return True if the 'msgType' field has been set, false otherwise.
      */
    public boolean hasMsgType() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'msgType' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearMsgType() {
      msgType = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'orderCreation' field.
      * @return The value.
      */
    public java.time.Instant getOrderCreation() {
      return orderCreation;
    }


    /**
      * Sets the value of the 'orderCreation' field.
      * @param value The value of 'orderCreation'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setOrderCreation(java.time.Instant value) {
      validate(fields()[7], value);
      this.orderCreation = value.truncatedTo(java.time.temporal.ChronoUnit.MILLIS);
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'orderCreation' field has been set.
      * @return True if the 'orderCreation' field has been set, false otherwise.
      */
    public boolean hasOrderCreation() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'orderCreation' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearOrderCreation() {
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'submitDate' field.
      * @return The value.
      */
    public java.time.LocalDate getSubmitDate() {
      return submitDate;
    }


    /**
      * Sets the value of the 'submitDate' field.
      * @param value The value of 'submitDate'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setSubmitDate(java.time.LocalDate value) {
      validate(fields()[8], value);
      this.submitDate = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'submitDate' field has been set.
      * @return True if the 'submitDate' field has been set, false otherwise.
      */
    public boolean hasSubmitDate() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'submitDate' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearSubmitDate() {
      fieldSetFlags()[8] = false;
      return this;
    }

    /**
      * Gets the value of the 'price' field.
      * @return The value.
      */
    public java.nio.ByteBuffer getPrice() {
      return price;
    }


    /**
      * Sets the value of the 'price' field.
      * @param value The value of 'price'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setPrice(java.nio.ByteBuffer value) {
      validate(fields()[9], value);
      this.price = value;
      fieldSetFlags()[9] = true;
      return this;
    }

    /**
      * Checks whether the 'price' field has been set.
      * @return True if the 'price' field has been set, false otherwise.
      */
    public boolean hasPrice() {
      return fieldSetFlags()[9];
    }


    /**
      * Clears the value of the 'price' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearPrice() {
      price = null;
      fieldSetFlags()[9] = false;
      return this;
    }

    /**
      * Gets the value of the 'discretionaryOffset' field.
      * @return The value.
      */
    public double getDiscretionaryOffset() {
      return discretionaryOffset;
    }


    /**
      * Sets the value of the 'discretionaryOffset' field.
      * @param value The value of 'discretionaryOffset'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setDiscretionaryOffset(double value) {
      validate(fields()[10], value);
      this.discretionaryOffset = value;
      fieldSetFlags()[10] = true;
      return this;
    }

    /**
      * Checks whether the 'discretionaryOffset' field has been set.
      * @return True if the 'discretionaryOffset' field has been set, false otherwise.
      */
    public boolean hasDiscretionaryOffset() {
      return fieldSetFlags()[10];
    }


    /**
      * Clears the value of the 'discretionaryOffset' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearDiscretionaryOffset() {
      fieldSetFlags()[10] = false;
      return this;
    }

    /**
      * Gets the value of the 'quantity' field.
      * @return The value.
      */
    public long getQuantity() {
      return quantity;
    }


    /**
      * Sets the value of the 'quantity' field.
      * @param value The value of 'quantity'.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder setQuantity(long value) {
      validate(fields()[11], value);
      this.quantity = value;
      fieldSetFlags()[11] = true;
      return this;
    }

    /**
      * Checks whether the 'quantity' field has been set.
      * @return True if the 'quantity' field has been set, false otherwise.
      */
    public boolean hasQuantity() {
      return fieldSetFlags()[11];
    }


    /**
      * Clears the value of the 'quantity' field.
      * @return This builder.
      */
    public com.jtk.matching.api.gen.Order.Builder clearQuantity() {
      fieldSetFlags()[11] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Order build() {
      try {
        Order record = new Order();
        record.orderId = fieldSetFlags()[0] ? this.orderId : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.productId = fieldSetFlags()[1] ? this.productId : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.orderType = fieldSetFlags()[2] ? this.orderType : (com.jtk.matching.api.gen.enums.OrderType) defaultValue(fields()[2]);
        record.productType = fieldSetFlags()[3] ? this.productType : (com.jtk.matching.api.gen.enums.ProductType) defaultValue(fields()[3]);
        record.priceType = fieldSetFlags()[4] ? this.priceType : (com.jtk.matching.api.gen.enums.PriceType) defaultValue(fields()[4]);
        record.side = fieldSetFlags()[5] ? this.side : (com.jtk.matching.api.gen.enums.Side) defaultValue(fields()[5]);
        record.msgType = fieldSetFlags()[6] ? this.msgType : (com.jtk.matching.api.gen.enums.MsgType) defaultValue(fields()[6]);
        record.orderCreation = fieldSetFlags()[7] ? this.orderCreation : (java.time.Instant) defaultValue(fields()[7]);
        record.submitDate = fieldSetFlags()[8] ? this.submitDate : (java.time.LocalDate) defaultValue(fields()[8]);
        record.price = fieldSetFlags()[9] ? this.price : (java.nio.ByteBuffer) defaultValue(fields()[9]);
        record.discretionaryOffset = fieldSetFlags()[10] ? this.discretionaryOffset : (java.lang.Double) defaultValue(fields()[10]);
        record.quantity = fieldSetFlags()[11] ? this.quantity : (java.lang.Long) defaultValue(fields()[11]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Order>
    WRITER$ = (org.apache.avro.io.DatumWriter<Order>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Order>
    READER$ = (org.apache.avro.io.DatumReader<Order>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










