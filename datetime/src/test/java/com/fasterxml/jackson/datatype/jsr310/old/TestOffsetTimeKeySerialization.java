package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestOffsetTimeKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<OffsetTime, String>> TYPE_REF = new TypeReference<Map<OffsetTime, String>>() {
    };
    private static final OffsetTime TIME_0 = OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC);
    private static final String TIME_0_STRING = "00:00Z";
    private static final OffsetTime TIME_1 = OffsetTime.of(3, 14, 15, 920 * 1000 * 1000, ZoneOffset.UTC);
    private static final String TIME_1_STRING = "03:14:15.920Z";
    private static final OffsetTime TIME_2 = OffsetTime.of(3, 14, 15, 920 * 1000 * 1000, ZoneOffset.ofHours(6));
    private static final String TIME_2_STRING = "03:14:15.920+06:00";

    private ObjectMapper om;
    private Map<OffsetTime, String> map;

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

        Assert.assertEquals(map(TIME_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(TIME_1, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(TIME_1_STRING, "test"), value);
    }

    @Test
    public void testSerialization2() throws Exception {
        map.put(TIME_2, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(TIME_2_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<OffsetTime, String> value = om.readValue(map(TIME_0_STRING, "test"), TYPE_REF);

        map.put(TIME_0, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<OffsetTime, String> value = om.readValue(map(TIME_1_STRING, "test"), TYPE_REF);

        map.put(TIME_1, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization2() throws Exception {
        Map<OffsetTime, String> value = om.readValue(map(TIME_2_STRING, "test"), TYPE_REF);

        map.put(TIME_2, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
