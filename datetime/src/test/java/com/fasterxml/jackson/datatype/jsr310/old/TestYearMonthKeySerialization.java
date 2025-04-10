package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestYearMonthKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<YearMonth, String>> TYPE_REF = new TypeReference<Map<YearMonth, String>>() {
    };

    private ObjectMapper om;
    private Map<YearMonth, String> map;

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
        map.put(YearMonth.of(3141, 5), "test");

        String value = om.writeValueAsString(map);

        assertEquals(map("3141-05", "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<YearMonth, String> value = om.readValue(map("3141-05", "test"), TYPE_REF);

        map.put(YearMonth.of(3141, 5), "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
