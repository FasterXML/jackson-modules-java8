package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.Year;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class YearAsKeyTest extends ModuleTestBase
{
    private static final TypeReference<Map<Year, String>> TYPE_REF = new TypeReference<Map<Year, String>>() {
    };
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString("3141", "test"),
                MAPPER.writeValueAsString(asMap(Year.of(3141), "test")));
    }

    @Test
    public void testDeserialization() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(Year.of(3141), "test"),
                READER.readValue(mapAsString("3141", "test")));
    }
}
