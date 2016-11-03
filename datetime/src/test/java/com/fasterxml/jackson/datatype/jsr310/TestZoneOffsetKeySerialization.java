package com.fasterxml.jackson.datatype.jsr310;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestZoneOffsetKeySerialization {

    private static final TypeReference<Map<ZoneOffset, String>> TYPE_REF = new TypeReference<Map<ZoneOffset, String>>() {
    };
    private static final ZoneOffset OFFSET_0 = ZoneOffset.UTC;
    private static final String OFFSET_0_STRING = "Z";
    private static final ZoneOffset OFFSET_1 = ZoneOffset.ofHours(6);
    private static final String OFFSET_1_STRING = "+06:00";

    private ObjectMapper om;
    private Map<ZoneOffset, String> map;

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
        map.put(OFFSET_0, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(OFFSET_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(OFFSET_1, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(OFFSET_1_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<ZoneOffset, String> value = om.readValue(map(OFFSET_0_STRING, "test"), TYPE_REF);

        map.put(OFFSET_0, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<ZoneOffset, String> value = om.readValue(map(OFFSET_1_STRING, "test"), TYPE_REF);

        map.put(OFFSET_1, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
