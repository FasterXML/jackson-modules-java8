package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.ZoneOffset;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class ZoneOffsetAsKeyTest extends ModuleTestBase
{
    private static final TypeReference<Map<ZoneOffset, String>> TYPE_REF = new TypeReference<Map<ZoneOffset, String>>() {
    };
    private static final ZoneOffset OFFSET_0 = ZoneOffset.UTC;
    private static final String OFFSET_0_STRING = "Z";
    private static final ZoneOffset OFFSET_1 = ZoneOffset.ofHours(6);
    private static final String OFFSET_1_STRING = "+06:00";

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerialization0() throws Exception {
        String value = MAPPER.writeValueAsString(asMap(OFFSET_0, "test"));
        Assert.assertEquals("Value is incorrect", mapAsString(OFFSET_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        String value = MAPPER.writeValueAsString(asMap(OFFSET_1, "test"));
        Assert.assertEquals("Value is incorrect", mapAsString(OFFSET_1_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<ZoneOffset, String> value = MAPPER.readValue(mapAsString(OFFSET_0_STRING, "test"), TYPE_REF);
        Assert.assertEquals("Value is incorrect", asMap(OFFSET_0, "test"), value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<ZoneOffset, String> value = MAPPER.readValue(mapAsString(OFFSET_1_STRING, "test"), TYPE_REF);
        Assert.assertEquals("Value is incorrect", asMap(OFFSET_1, "test"), value);
    }
}
