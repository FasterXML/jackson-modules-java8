package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestInstantKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<Instant, String>> TYPE_REF = new TypeReference<Map<Instant, String>>() {
    };
    private static final Instant INSTANT_0 = Instant.ofEpochMilli(0);
    private static final String INSTANT_0_STRING = "1970-01-01T00:00:00Z";
    private static final Instant INSTANT = Instant.ofEpochSecond(1426325213l, 590000000l);
    private static final String INSTANT_STRING = "2015-03-14T09:26:53.590Z";

    private ObjectMapper om;
    private Map<Instant, String> map;

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
        map.put(INSTANT_0, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(INSTANT_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(INSTANT, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(INSTANT_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<Instant, String> value = om.readValue(map(INSTANT_0_STRING, "test"), TYPE_REF);

        map.put(INSTANT_0, "test");
        assertEquals(map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<Instant, String> value = om.readValue(map(INSTANT_STRING, "test"), TYPE_REF);

        map.put(INSTANT, "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
