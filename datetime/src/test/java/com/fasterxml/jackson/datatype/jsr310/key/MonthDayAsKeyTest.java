package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.MonthDay;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MonthDayAsKeyTest extends ModuleTestBase
{
    private static final MonthDay MONTH_DAY = MonthDay.of(3, 14);
    private static final String MONTH_DAY_STRING = "--03-14";

    private static final TypeReference<Map<MonthDay, String>> TYPE_REF = new TypeReference<Map<MonthDay, String>>() {
    };
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(MONTH_DAY_STRING, "test"),
                MAPPER.writeValueAsString(asMap(MONTH_DAY, "test")));
    }

    @Test
    public void testDeserialization() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(MONTH_DAY, "test"),
                READER.readValue(mapAsString(MONTH_DAY_STRING, "test")));
    }
}
