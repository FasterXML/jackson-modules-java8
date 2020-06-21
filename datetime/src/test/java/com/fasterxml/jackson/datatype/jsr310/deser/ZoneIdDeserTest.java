package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.ZoneId;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ZoneIdDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final TypeReference<Map<String, ZoneId>> MAP_TYPE_REF = new TypeReference<Map<String, ZoneId>>() { };

    private final ObjectMapper MOCK_OBJECT_MIXIN_MAPPER = mapperBuilder()
            .addMixIn(ZoneId.class, MockObjectConfiguration.class)
            .build();

    @Test
    public void testSimpleZoneIdDeser() throws Exception
    {
        assertEquals("The value is not correct.", ZoneId.of("America/Chicago"),
                MAPPER.readValue("\"America/Chicago\"", ZoneId.class));
        assertEquals("The value is not correct.", ZoneId.of("America/Anchorage"),
                MAPPER.readValue("\"America/Anchorage\"", ZoneId.class));
    }

    @Test
    public void testPolymorphicZoneIdDeser() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .addModule(new JavaTimeModule())
                .build();
        ZoneId value = mapper.readValue("[\"" + ZoneId.class.getName() + "\",\"America/Denver\"]", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Denver"), value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        assertEquals("The value is not correct.", ZoneId.of("America/Chicago"),
                MAPPER.readValue("\"America/Chicago\"", ZoneId.class));
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        assertEquals("The value is not correct.", ZoneId.of("America/Anchorage"),
                MAPPER.readValue("\"America/Anchorage\"", ZoneId.class));
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        ZoneId value = MOCK_OBJECT_MIXIN_MAPPER.readValue("[\"" + ZoneId.class.getName() + "\",\"America/Denver\"]", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Denver"), value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "zoneId";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, ZoneId> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        ZoneId actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        Map<String, ZoneId> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        ZoneId actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting", null, actualDateFromEmptyStr);
    }

    @Test ( expected =  MismatchedInputException.class)
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "zoneId";
        final ObjectMapper mapper = mapperBuilder()
                .disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .build();
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, ZoneId> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        objectReader.readValue(valueFromEmptyStr);
    }

    // [module-java8#68]
    @Test
    public void testZoneOffsetDeserFromEmpty() throws Exception
    {
        // by default, should be fine
        assertNull(MAPPER.readValue(quote("  "), ZoneId.class));
        // but fail if coercion illegal
        try {
            newMapperBuilder()
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .build()
                .readValue(quote(" "), ZoneId.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce");
            verifyException(e, ZoneId.class.getName());
            verifyException(e, "enable `MapperFeature.ALLOW_COERCION_OF_SCALARS`");
        }
    }

}
