package com.fasterxml.jackson.datatype.jsr310;

import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class TestMonthDayKeySerialization
{
    private static final TypeReference<Map<MonthDay, String>> TYPE_REF = new TypeReference<Map<MonthDay, String>>() {
    };
    private static final MonthDay MONTH_DAY = MonthDay.of(3, 14);
    private static final String MONTH_DAY_STRING = "--03-14";

    private final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();

    @Test
    public void testSerialization() throws Exception {
        Map<MonthDay, String> map = new HashMap<>();
        map.put(MONTH_DAY, "test");
        String value = MAPPER.writeValueAsString(map);
        Assert.assertEquals("Value is incorrect", map(MONTH_DAY_STRING, "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<MonthDay, String> value = MAPPER.readValue(map(MONTH_DAY_STRING, "test"), TYPE_REF);
        Map<MonthDay, String> map = new HashMap<>();
        map.put(MONTH_DAY, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }
}
