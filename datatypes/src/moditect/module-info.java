// Generated 28-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.jdk8 {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports com.fasterxml.jackson.datatype.jdk8;

    // Since 3.0 bogus NOP module so let's NOT expose via SPI
//    provides com.fasterxml.jackson.databind.JacksonModule with
//    com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
}
