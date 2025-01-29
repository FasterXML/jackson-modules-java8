// Java-Time (unit) Test Module descriptor
module tools.jackson.datatype.jsr310
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.jsr310;
    opens tools.jackson.datatype.jsr310.deser;
    opens tools.jackson.datatype.jsr310.deser.key;
    opens tools.jackson.datatype.jsr310.key;
    opens tools.jackson.datatype.jsr310.misc;
    opens tools.jackson.datatype.jsr310.ser;
    opens tools.jackson.datatype.jsr310.testutil.failure;
    opens tools.jackson.datatype.jsr310.tofix;
    opens tools.jackson.datatype.jsr310.util;
}
