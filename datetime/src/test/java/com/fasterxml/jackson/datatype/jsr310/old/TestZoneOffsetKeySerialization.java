package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestZoneOffsetKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<ZoneOffset, String>> TYPE_REF = new TypeReference<Map<ZoneOffset, String>>() {
    };
    private static final ZoneOffset OFFSET_0 = ZoneOffset.UTC;
    private static final String OFFSET_0_STRING = "Z";
    private static final ZoneOffset OFFSET_1 = ZoneOffset.ofHours(6);
    private static final String OFFSET_1_STRING = "+06:00";

    private ObjectMapper om;
    private Map<ZoneOffset, String> map;

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
        map.put(OFFSET_0, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(OFFSET_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(OFFSET_1, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(OFFSET_1_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<ZoneOffset, String> value = om.readValue(map(OFFSET_0_STRING, "test"), TYPE_REF);

        map.put(OFFSET_0, "test");
        assertEquals(map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<ZoneOffset, String> value = om.readValue(map(OFFSET_1_STRING, "test"), TYPE_REF);

        map.put(OFFSET_1, "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
