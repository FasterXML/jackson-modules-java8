package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer;

import org.junit.Assert;
import org.junit.Test;

public class TestNullKeySerialization extends ModuleTestBase
{
    private static final TypeReference<Map<LocalDate, String>> TYPE_REF = new TypeReference<Map<LocalDate, String>>() { };

    @Test
    public void testSerialization() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .addModule(new SimpleModule()
                        .setDefaultNullKeySerializer(new Jsr310NullKeySerializer()))
                .build();

        Map<LocalDate, String> map = new HashMap<>();
        map.put(null, "test");
        String value = mapper.writeValueAsString(map);

        Assert.assertEquals(map(Jsr310NullKeySerializer.NULL_KEY, "test"), value);
    }

    @Test
    public void testDeserialization() throws Exception {
        ObjectMapper mapper = newMapper();
        Map<LocalDate, String> map = new HashMap<>();
        map.put(null, "test");
        Map<LocalDate, String> value = mapper.readValue(map(Jsr310NullKeySerializer.NULL_KEY, "test"), TYPE_REF);
        Assert.assertEquals(map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }
}
