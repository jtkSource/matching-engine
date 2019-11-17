/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.jtk.matching.api.gen;

@org.apache.avro.specific.AvroGenerated
public interface OrderProtocol {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"OrderProtocol\",\"namespace\":\"com.jtk.matching.api.gen\",\"types\":[{\"type\":\"enum\",\"name\":\"OrderType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"LIMIT\",\"MKT\"]},{\"type\":\"enum\",\"name\":\"ProductType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"Bond\",\"Repo\"]},{\"type\":\"enum\",\"name\":\"PriceType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"Cash\",\"Spread\"]},{\"type\":\"enum\",\"name\":\"Side\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"Buy\",\"Sell\"]},{\"type\":\"enum\",\"name\":\"MsgType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"New\",\"Amend\",\"Cancel\",\"IOI\"]},{\"type\":\"record\",\"name\":\"Order\",\"fields\":[{\"name\":\"orderId\",\"type\":\"string\"},{\"name\":\"productId\",\"type\":\"string\"},{\"name\":\"orderType\",\"type\":\"com.jtk.matching.api.gen.enums.OrderType\",\"default\":\"LIMIT\"},{\"name\":\"productType\",\"type\":\"com.jtk.matching.api.gen.enums.ProductType\"},{\"name\":\"priceType\",\"type\":\"com.jtk.matching.api.gen.enums.PriceType\",\"default\":\"Cash\"},{\"name\":\"side\",\"type\":\"com.jtk.matching.api.gen.enums.Side\"},{\"name\":\"msgType\",\"type\":\"com.jtk.matching.api.gen.enums.MsgType\",\"default\":\"New\"},{\"name\":\"orderCreation\",\"type\":{\"type\":\"long\",\"logicalType\":\"timestamp-millis\"}},{\"name\":\"submitDate\",\"type\":{\"type\":\"int\",\"logicalType\":\"date\"}},{\"name\":\"price\",\"type\":{\"type\":\"bytes\",\"logicalType\":\"decimal\",\"precision\":8,\"scale\":8},\"default\":\"\\u0000\"},{\"name\":\"discretionaryOffset\",\"type\":\"double\",\"default\":0.0},{\"name\":\"quantity\",\"type\":\"long\"}]}],\"messages\":{}}");

  @SuppressWarnings("all")
  public interface Callback extends OrderProtocol {
    public static final org.apache.avro.Protocol PROTOCOL = com.jtk.matching.api.gen.OrderProtocol.PROTOCOL;
  }
}