/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.jtk.matching.api.gen.enums;
@org.apache.avro.specific.AvroGenerated
public enum MsgType implements org.apache.avro.generic.GenericEnumSymbol<MsgType> {
  New, Amend, Cancel, IOI  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"MsgType\",\"namespace\":\"com.jtk.matching.api.gen.enums\",\"symbols\":[\"New\",\"Amend\",\"Cancel\",\"IOI\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}