package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestLocalTimeKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<LocalTime, String>> TYPE_REF = new TypeReference<Map<LocalTime, String>>() {
    };
    private static final LocalTime TIME_0 = LocalTime.ofSecondOfDay(0);
    /*
     * Seconds are omitted if possible
     */
    private static final String TIME_0_STRING = "00:00";
    private static final LocalTime TIME = LocalTime.of(3, 14, 15, 920 * 1000 * 1000);
    private static final String TIME_STRING = "03:14:15.920";

    private ObjectMapper om;
    private Map<LocalTime, String> map;

    @Before
    public void setUp() {
        this.om = newMapper();
        map = new HashMap<>();
    }

    /*
     * ObjectMapper configuration is not respected at deserialization and serialization at the moment.
     */

    @Test
    public void testSerialization0() throws Exception {
        map.put(TIME_0, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(TIME_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(TIME, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(TIME_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<LocalTime, String> value = om.readValue(map(TIME_0_STRING, "test"), TYPE_REF);

        map.put(TIME_0, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<LocalTime, String> value = om.readValue(map(TIME_STRING, "test"), TYPE_REF);

        map.put(TIME, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
