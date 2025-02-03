// Java-Time Main artifact Module descriptor
module tools.jackson.datatype.javatime
{
    requires com.fasterxml.jackson.annotation;

    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    exports tools.jackson.datatype.jsr310;
    exports tools.jackson.datatype.jsr310.deser;
    exports tools.jackson.datatype.jsr310.deser.key;
    exports tools.jackson.datatype.jsr310.ser;
    exports tools.jackson.datatype.jsr310.ser.key;

    // 27-Jan-2021, tatu: Likely needed for access to (de)serializers via
    //    annotations (see [modules-java#202])
    opens tools.jackson.datatype.jsr310.deser;
    opens tools.jackson.datatype.jsr310.deser.key;
    opens tools.jackson.datatype.jsr310.ser;
    opens tools.jackson.datatype.jsr310.ser.key;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.jsr310.JavaTimeModule;
}
