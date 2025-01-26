package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestPeriodKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<Period, String>> TYPE_REF = new TypeReference<Map<Period, String>>() {
    };
    private static final Period PERIOD_0 = Period.of(0, 0, 0);
    private static final String PERIOD_0_STRING = "P0D";
    private static final Period PERIOD = Period.of(3, 1, 4);
    private static final String PERIOD_STRING = "P3Y1M4D";

    private ObjectMapper om;
    private Map<Period, String> map;

    @BeforeEach
    public void setUp() {
        this.om = newMapper();
        map = new HashMap<>();
    }

    /*
     * ObjectMapper configuration is not respected at deserialization and serialization at the moment.
     */

    @Test
    public void testSerialization0() throws Exception {
        map.put(PERIOD_0, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(PERIOD_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(PERIOD, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(PERIOD_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<Period, String> value = om.readValue(map(PERIOD_0_STRING, "test"), TYPE_REF);

        map.put(PERIOD_0, "test");
        assertEquals(map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<Period, String> value = om.readValue(map(PERIOD_STRING, "test"), TYPE_REF);

        map.put(PERIOD, "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
