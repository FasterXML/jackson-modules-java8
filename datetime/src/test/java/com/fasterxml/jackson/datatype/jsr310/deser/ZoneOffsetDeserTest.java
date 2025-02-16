/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class ZoneOffsetDeserTest extends ModuleTestBase
{
    private final static ObjectMapper MAPPER = newMapper();
    private final static ObjectReader READER = MAPPER.readerFor(ZoneOffset.class);
    private final TypeReference<Map<String, ZoneOffset>> MAP_TYPE_REF = new TypeReference<Map<String, ZoneOffset>>() { };

    @Test
    public void testDeserializationFromString() throws Exception
    {
        assertEquals(ZoneOffset.of("Z"), READER.readValue("\"Z\""),
                "The value is not correct.");
        assertEquals(ZoneOffset.of("+0300"), READER.readValue(q("+0300")),
                "The value is not correct.");
        assertEquals(ZoneOffset.of("-0630"), READER.readValue("\"-06:30\""),
                "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        ObjectMapper mapper = newMapper()
            .addMixIn(ZoneId.class, MockObjectConfiguration.class);
        ZoneId value = mapper.readValue("[\"" + ZoneOffset.class.getName() + "\",\"+0415\"]", ZoneId.class);
        assertTrue(value instanceof ZoneOffset, "The value should be a ZoneOffset.");
        assertEquals(ZoneOffset.of("+0415"), value, "The value is not correct.");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            READER.readValue("\"notazonedoffset\"");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Invalid ID for ZoneOffset");
        }
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[\"+0300\"]");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type `java.time.ZoneOffset` from Array value");
        }
    }

    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[]");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type `java.time.ZoneOffset` from Array value");
        }
        try {
            READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue("[]");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize value of type `java.time.ZoneOffset` from Array value");
        }
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        ZoneOffset value = READER
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
               .readValue("[\"+0300\"]");
        assertEquals(ZoneOffset.of("+0300"), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        ZoneOffset value = READER
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
               .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
               .readValue("[]");
        assertNull(value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "zoneOffset";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, ZoneOffset> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        ZoneId actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        Map<String, ZoneOffset> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        ZoneId actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals(null, actualDateFromEmptyStr, "empty string failed to deserialize to null with lenient setting");
    }

    @Test
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "zoneOffset";
        final ObjectMapper mapper = mapperBuilder().build();
        mapper.configOverride(ZoneOffset.class)
                .setFormat(JsonFormat.Value.forLeniency(false));

        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, ZoneOffset> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        assertThrows(MismatchedInputException.class, () -> objectReader.readValue(valueFromEmptyStr));
    }
}
