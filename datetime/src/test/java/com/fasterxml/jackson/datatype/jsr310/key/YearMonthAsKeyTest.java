package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.YearMonth;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class YearMonthAsKeyTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(new TypeReference<Map<YearMonth, String>>() {
    });

    @Test
    public void testSerialization() throws Exception {
        assertEquals(mapAsString("3141-05", "test"),
                MAPPER.writeValueAsString(asMap(YearMonth.of(3141, 5), "test")),
                "Value is incorrect");
    }

    @Test
    public void testDeserialization() throws Exception {
        assertEquals(asMap(YearMonth.of(3141, 5), "test"),
                READER.readValue(mapAsString("3141-05", "test")),
                "Value is incorrect");
    }
}
