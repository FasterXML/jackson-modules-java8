// Generated 28-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.jsr310 {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports com.fasterxml.jackson.datatype.jsr310;
    exports com.fasterxml.jackson.datatype.jsr310.deser;
    exports com.fasterxml.jackson.datatype.jsr310.deser.key;
    exports com.fasterxml.jackson.datatype.jsr310.ser;
    exports com.fasterxml.jackson.datatype.jsr310.ser.key;

    // 27-Jan-2021, tatu: Likely needed for access to (de)serializers via
    //    annotations (see [modules-java#202])
    opens com.fasterxml.jackson.datatype.jsr310.deser;
    opens com.fasterxml.jackson.datatype.jsr310.deser.key;
    opens com.fasterxml.jackson.datatype.jsr310.ser;
    opens com.fasterxml.jackson.datatype.jsr310.ser.key;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
}
