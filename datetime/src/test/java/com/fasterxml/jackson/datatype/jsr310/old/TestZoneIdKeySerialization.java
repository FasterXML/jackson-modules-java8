package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestZoneIdKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<ZoneId, String>> TYPE_REF = new TypeReference<Map<ZoneId, String>>() {
    };
    private static final ZoneId ZONE_0 = ZoneId.of("UTC");
    private static final String ZONE_0_STRING = "UTC";
    private static final ZoneId ZONE_1 = ZoneId.of("+06:00");
    private static final String ZONE_1_STRING = "+06:00";
    private static final ZoneId ZONE_2 = ZoneId.of("Europe/London");
    private static final String ZONE_2_STRING = "Europe/London";

    private ObjectMapper om;
    private Map<ZoneId, String> map;

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
        map.put(ZONE_0, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(ZONE_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(ZONE_1, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(ZONE_1_STRING, "test"), value);
    }

    @Test
    public void testSerialization2() throws Exception {
        map.put(ZONE_2, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(ZONE_2_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<ZoneId, String> value = om.readValue(map(ZONE_0_STRING, "test"), TYPE_REF);

        map.put(ZONE_0, "test");
        assertEquals(map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<ZoneId, String> value = om.readValue(map(ZONE_1_STRING, "test"), TYPE_REF);

        map.put(ZONE_1, "test");
        assertEquals(map, value);
    }

    @Test
    public void testDeserialization2() throws Exception {
        Map<ZoneId, String> value = om.readValue(map(ZONE_2_STRING, "test"), TYPE_REF);

        map.put(ZONE_2, "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
