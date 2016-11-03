package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestConfigureAbsentsAsNulls extends ModuleTestBase
{
    @JsonAutoDetect(fieldVisibility=Visibility.ANY)
    public static final class OptionalData {
        public Optional<String> myString = Optional.empty();
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    public void testConfigAbsentsAsNullsTrue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module().configureAbsentsAsNulls(true));

        OptionalData data = new OptionalData();
        String value = mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(data);
        assertEquals("{}", value);
    }

    public void testConfigAbsentsAsNullsFalse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module().configureAbsentsAsNulls(false));

        OptionalData data = new OptionalData();
        String value = mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL).writeValueAsString(data);
        assertEquals("{\"myString\":null}", value);
    }

    public void testConfigNonAbsentAbsentsAsNullsTrue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module().configureAbsentsAsNulls(true));

        OptionalData data = new OptionalData();
        String value = mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT).writeValueAsString(data);
        assertEquals("{}", value);
    }

    public void testConfigNonAbsentAbsentsAsNullsFalse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module().configureAbsentsAsNulls(false));

        OptionalData data = new OptionalData();
        String value = mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT).writeValueAsString(data);
        assertEquals("{}", value);
    }
}
