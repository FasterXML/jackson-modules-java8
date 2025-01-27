package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.MonthDay;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestMonthDayKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<MonthDay, String>> TYPE_REF = new TypeReference<Map<MonthDay, String>>() {
    };
    private static final MonthDay MONTH_DAY = MonthDay.of(3, 14);
    private static final String MONTH_DAY_STRING = "--03-14";

    private ObjectMapper om;
    private Map<MonthDay, String> map;

    @BeforeEach
    public void setUp() {
        this.om = newMapper();
        map = new HashMap<>();
    }

    /*
     * ObjectMapper configuration is not respected at deserialization and serialization at the moment.
     */

    @Test
    public void testSerialization() throws Exception {
        map.put(MONTH_DAY, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(MONTH_DAY_STRING, "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<MonthDay, String> value = om.readValue(map(MONTH_DAY_STRING, "test"), TYPE_REF);

        map.put(MONTH_DAY, "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
