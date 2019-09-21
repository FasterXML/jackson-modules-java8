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

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.jsr310.JSR310Module;
}
