package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestDurationKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<Duration, String>> TYPE_REF = new TypeReference<Map<Duration, String>>() {
    };
    private static final Duration DURATION = Duration.ofMinutes(13).plusSeconds(37).plusNanos(120 * 1000 * 1000);
    private static final String DURATION_STRING = "PT13M37.12S";

    private ObjectMapper om;
    private Map<Duration, String> map;

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
        map.put(DURATION, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(DURATION_STRING, "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<Duration, String> value = om.readValue(
                map(DURATION_STRING, "test"),
                TYPE_REF);

        map.put(DURATION, "test");
        assertEquals(map, value, "Value is not correct");
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
