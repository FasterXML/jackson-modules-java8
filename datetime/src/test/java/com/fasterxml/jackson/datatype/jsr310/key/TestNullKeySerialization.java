package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Assert;
import org.junit.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestNullKeySerialization extends ModuleTestBase
{
    private static final TypeReference<Map<LocalDate, String>> TYPE_REF = new TypeReference<Map<LocalDate, String>>() { };
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @SuppressWarnings("deprecation")
    @Test
    public void testSerialization() throws Exception {
        final ObjectMapper mapper = newMapper();
        mapper.getSerializerProvider().setNullKeySerializer(new com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer());
        String value = mapper.writeValueAsString(asMap(null, "test"));
        Assert.assertEquals(mapAsString(com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer.NULL_KEY, "test"), value);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialization() throws Exception {
        Map<LocalDate, String> value = READER.readValue(mapAsString(com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer.NULL_KEY, "test"));
        Assert.assertEquals(asMap(null, "test"), value);
    }
}
