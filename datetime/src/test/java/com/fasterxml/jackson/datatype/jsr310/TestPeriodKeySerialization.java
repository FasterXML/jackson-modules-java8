package com.fasterxml.jackson.datatype.jsr310;

import java.time.Period;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestPeriodKeySerialization {

    private static final TypeReference<Map<Period, String>> TYPE_REF = new TypeReference<Map<Period, String>>() {
    };
    private static final Period PERIOD_0 = Period.of(0, 0, 0);
    private static final String PERIOD_0_STRING = "P0D";
    private static final Period PERIOD = Period.of(3, 1, 4);
    private static final String PERIOD_STRING = "P3Y1M4D";

    private ObjectMapper om;
    private Map<Period, String> map;

    @Before
    public void setUp() {
        this.om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        map = new HashMap<>();
    }

    /*
     * ObjectMapper configuration is not respected at deserialization and serialization at the moment.
     */

    @Test
    public void testSerialization0() throws Exception {
        map.put(PERIOD_0, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(PERIOD_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(PERIOD, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(PERIOD_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<Period, String> value = om.readValue(map(PERIOD_0_STRING, "test"), TYPE_REF);

        map.put(PERIOD_0, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<Period, String> value = om.readValue(map(PERIOD_STRING, "test"), TYPE_REF);

        map.put(PERIOD, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
