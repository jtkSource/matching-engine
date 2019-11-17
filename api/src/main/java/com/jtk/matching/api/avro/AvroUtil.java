package com.jtk.matching.api.avro;

import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

import java.math.BigDecimal;
import java.nio.ByteBuffer;

public class AvroUtil {

    private static final Conversions.DecimalConversion DECIMAL_CONVERSION = new Conversions.DecimalConversion();
    private AvroUtil(){}

    public static BigDecimal convertToBigDecimal(ByteBuffer buffer, int scale) {
        buffer.clear();
        BigDecimal bigDecimal = DECIMAL_CONVERSION.fromBytes(buffer, Schema.create(Schema.Type.BYTES), LogicalTypes.decimal(scale, scale));
        buffer.clear();
        return bigDecimal;
    }

    public static ByteBuffer convertToByteBuffer(BigDecimal decimal, int scale) {
        return DECIMAL_CONVERSION
                .toBytes(decimal, Schema.create(Schema.Type.BYTES), LogicalTypes.decimal(scale, scale))
                .clear();
    }
}
