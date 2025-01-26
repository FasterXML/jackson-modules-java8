package com.fasterxml.jackson.module.paramnames;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ConstructorDetector;

import static org.junit.jupiter.api.Assertions.*;

public class ConstructorDetectorConfigTest
    extends ModuleTestBase
{
    static class SingleArgImmutable {
        final String v;

        public SingleArgImmutable(String value) { v = value; }
    }
    
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSimpleImmutable() throws Exception
    {
        ImmutableBean input = new ImmutableBean("value", 42);
        final String json = MAPPER.writeValueAsString(input);
        ImmutableBean result = MAPPER.readValue(json, ImmutableBean.class);
        assertEquals(input, result);
    }

    // [databind#2852]
    @Test
    public void testSimpleArgNonAnnotatedProperties() throws Exception
    {
        final ObjectMapper mapper = mapperBuilder()
                .constructorDetector(ConstructorDetector.USE_PROPERTIES_BASED)
                .build();
        SingleArgImmutable result = mapper.readValue("{\"value\": \"foo\"}",
                SingleArgImmutable.class);
        assertEquals("foo", result.v);
    }

    // [databind#2852]
    @Test
    public void testSimpleArgNonAnnotatedDelegating() throws Exception
    {
        final ObjectMapper mapper = mapperBuilder()
                .constructorDetector(ConstructorDetector.USE_DELEGATING)
                .build();
        SingleArgImmutable result = mapper.readValue("\"bar\"",
                SingleArgImmutable.class);
        assertEquals("bar", result.v);
    }
}
