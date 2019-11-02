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

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

public class LocalTimeDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(LocalTime.class);

    private final TypeReference<Map<String, LocalTime>> MAP_TYPE_REF = new TypeReference<Map<String, LocalTime>>() { };

    final static class StrictWrapper {
        @JsonFormat(pattern="HH:mm", lenient = OptBoolean.FALSE)
        public LocalTime value;

        public StrictWrapper() { }
        public StrictWrapper(LocalTime v) { value = v; }
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43);
        LocalTime value = READER.readValue("[15,43]");
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);
        LocalTime value = READER.readValue("[9,22,57]");
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Nanoseconds() throws Exception
    {
        LocalTime value = READER
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[9,22,0,57]");
        assertEquals("The value is not correct.", LocalTime.of(9, 22, 0, 57), value);
    }

    @Test
    public void testDeserializationAsTimestamp03Milliseconds() throws Exception
    {
        LocalTime value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[9,22,0,57]");
        assertEquals("The value is not correct.", LocalTime.of(9, 22, 0, 57000000), value);
    }

    @Test
    public void testDeserializationAsTimestamp04Nanoseconds() throws Exception
    {
        LocalTime value = READER
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[22,31,5,829837]");
        assertEquals("The value is not correct.", LocalTime.of(22, 31, 5, 829837), value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds01() throws Exception
    {
        LocalTime value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[22,31,5,829837]");
        assertEquals("The value is not correct.", LocalTime.of(22, 31, 5, 829837), value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds02() throws Exception
    {
        LocalTime value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[22,31,5,829]");
        assertEquals("The value is not correct.", LocalTime.of(22, 31, 5, 829000000), value);
    }

    @Test
    public void testDeserializationFromString() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43);
        LocalTime value = READER.readValue('"' + time.toString() + '"');
        assertEquals("The value is not correct.", time, value);

        expectSuccess(LocalTime.of(12, 0), "'12:00'");

        time = LocalTime.of(9, 22, 57);
        value = READER.readValue('"' + time.toString() + '"');
        assertEquals("The value is not correct.", time, value);

        time = LocalTime.of(22, 31, 5, 829837);
        value = READER.readValue('"' + time.toString() + '"');
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testBadDeserializationFromString() throws Throwable
    {
        expectFailure("'notalocaltime'");
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue(aposToQuotes("['12:00']"));
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_STRING) within Array");
        }

        // 25-Jul-2017, tatu: Why does it work? Is it supposed to?
        // works even without the feature enabled
        assertNull(READER.readValue("[]"));
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        LocalTime value = READER
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
               .readValue(aposToQuotes("['12:00']"));
        expect(LocalTime.of(12, 0), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        LocalTime value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue(aposToQuotes("[]"));
        assertNull(value);
    }    

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper mapper = newMapperBuilder()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = mapper.readerFor(Temporal.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[\"" + LocalTime.class.getName() + "\",[22,31,5,829837]]");

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a LocalTime.", value instanceof LocalTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422000000);

        ObjectMapper mapper = newMapperBuilder()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = mapper.readerFor(Temporal.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[\"" + LocalTime.class.getName() + "\",[22,31,5,422]]");
        assertTrue("The value should be a LocalTime.", value instanceof LocalTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper mapper = newMapperBuilder()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = mapper.readValue(
                "[\"" + LocalTime.class.getName() + "\",\"" + time.toString() + "\"]", Temporal.class
                );
        assertTrue("The value should be a LocalTime.", value instanceof LocalTime);
        assertEquals("The value is not correct.", time, value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "localTime";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String dateValAsEmptyStr = "";

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, LocalTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        LocalTime actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, dateValAsEmptyStr));
        Map<String, LocalTime> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        LocalTime actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting",null, actualDateFromEmptyStr);
    }

    @Test( expected =  MismatchedInputException.class)
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "localTime";
        final ObjectMapper mapper = mapperBuilder()
                .withConfigOverride(LocalTime.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, LocalTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap("date", ""));
        objectReader.readValue(valueFromEmptyStr);
    }

    /*
    /**********************************************************************
    /* Strict JsonFormat tests
    /**********************************************************************
     */

    // [modules-java8#148]: handle strict deserializaiton for date/time

    @Test(expected = InvalidFormatException.class)
    public void testStrictCustomFormatInvalidTime() throws Exception
    {
        /*StrictWrapper w =*/ MAPPER.readValue("{\"value\":\"25:45\"}", StrictWrapper.class);
    }

    private void expectFailure(String aposJson) throws Throwable {
        try {
            READER.readValue(aposToQuotes(aposJson));
            fail("expected DateTimeParseException");
        } catch (JsonProcessingException e) {
            if (e.getCause() == null) {
                throw e;
            }
            if (!(e.getCause() instanceof DateTimeParseException)) {
                throw e.getCause();
            }
        }
    }

    private void expectSuccess(Object exp, String aposJson) throws IOException {
        final LocalTime value = READER.readValue(aposToQuotes(aposJson));
        expect(exp, value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
