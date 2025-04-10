package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalDateKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<LocalDate, String>> TYPE_REF = new TypeReference<Map<LocalDate, String>>() {
    };
    private static final LocalDate DATE = LocalDate.of(2015, 3, 14);
    private static final String DATE_STRING = "2015-03-14";

    private ObjectMapper om;
    private Map<LocalDate, String> map;

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
        map.put(DATE, "test");

        String value = om.writeValueAsString(map);

        assertEquals(map(DATE_STRING, "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<LocalDate, String> value = om.readValue(
                map(DATE_STRING, "test"),
                TYPE_REF);

        map.put(DATE, "test");
        assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }
}
