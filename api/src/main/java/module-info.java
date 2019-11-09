module com.jtk.matching.api {
    requires org.apache.avro;
    requires org.slf4j;
    exports com.jtk.matching.api.gen;
    exports com.jtk.matching.api.gen.enums;
}