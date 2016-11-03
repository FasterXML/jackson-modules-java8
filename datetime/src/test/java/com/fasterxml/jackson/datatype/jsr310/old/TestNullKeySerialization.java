package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestNullKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<LocalDate, String>> TYPE_REF = new TypeReference<Map<LocalDate, String>>() {
    };

    private ObjectMapper om;
    private Map<LocalDate, String> map;

    @Before
    public void setUp() {
        this.om = newMapper();
        map = new HashMap<>();
    }

    @Test
    public void testSerialization() throws Exception {
        om.getSerializerProvider().setNullKeySerializer(new Jsr310NullKeySerializer());

        map.put(null, "test");
        String value = om.writeValueAsString(map);

        Assert.assertEquals(map(Jsr310NullKeySerializer.NULL_KEY, "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        Map<LocalDate, String> value = om.readValue(map(Jsr310NullKeySerializer.NULL_KEY, "test"), TYPE_REF);

        map.put(null, "test");
        Assert.assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }
}
