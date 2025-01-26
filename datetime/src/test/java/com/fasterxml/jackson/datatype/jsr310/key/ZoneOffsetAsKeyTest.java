package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ZoneOffsetAsKeyTest extends ModuleTestBase
{
    private static final TypeReference<Map<ZoneOffset, String>> TYPE_REF = new TypeReference<Map<ZoneOffset, String>>() {
    };
    private static final ZoneOffset OFFSET_0 = ZoneOffset.UTC;
    private static final String OFFSET_0_STRING = "Z";
    private static final ZoneOffset OFFSET_1 = ZoneOffset.ofHours(6);
    private static final String OFFSET_1_STRING = "+06:00";

    private final ObjectMapper MAPPER = newMapper();

    private Map<ZoneOffset, String> map;

    @Before
    public void setUp() {
        map = new HashMap<>();
    }

    @Test
    public void testSerialization0() throws Exception {
        map.put(OFFSET_0, "test");

        String value = MAPPER.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(OFFSET_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(OFFSET_1, "test");

        String value = MAPPER.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(OFFSET_1_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<ZoneOffset, String> value = MAPPER.readValue(map(OFFSET_0_STRING, "test"), TYPE_REF);

        map.put(OFFSET_0, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<ZoneOffset, String> value = MAPPER.readValue(map(OFFSET_1_STRING, "test"), TYPE_REF);

        map.put(OFFSET_1, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
