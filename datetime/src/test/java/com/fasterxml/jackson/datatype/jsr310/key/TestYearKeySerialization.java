package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestYearKeySerialization {

    private static final TypeReference<Map<Year, String>> TYPE_REF = new TypeReference<Map<Year, String>>() {
    };
    private final ObjectMapper om = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private Map<Year, String> map;

    @Before
    public void setUp() {
        map = new HashMap<>();
    }

    @Test
    public void testSerialization() throws Exception {
        map.put(Year.of(3141), "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map("3141", "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<Year, String> value = om.readValue(map("3141", "test"), TYPE_REF);

        map.put(Year.of(3141), "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
