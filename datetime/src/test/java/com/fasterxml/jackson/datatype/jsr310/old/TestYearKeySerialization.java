package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestYearKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<Year, String>> TYPE_REF = new TypeReference<Map<Year, String>>() {
    };
    private ObjectMapper om;
    private Map<Year, String> map;

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
        map.put(Year.of(3141), "test");

        String value = om.writeValueAsString(map);

        assertEquals(map("3141", "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<Year, String> value = om.readValue(map("3141", "test"), TYPE_REF);

        map.put(Year.of(3141), "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
