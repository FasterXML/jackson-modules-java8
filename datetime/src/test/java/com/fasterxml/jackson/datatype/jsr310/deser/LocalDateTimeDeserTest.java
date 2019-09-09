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
import java.time.*;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocalDateTimeDeserTest
    extends ModuleTestBase
{
    private final static ObjectMapper MAPPER = newMapper();
    private final static ObjectReader READER = MAPPER.readerFor(LocalDateTime.class);
    private final TypeReference<Map<String, LocalDateTime>> MAP_TYPE_REF = new TypeReference<Map<String, LocalDateTime>>() { };

    /*
    /**********************************************************
    /* Tests for deserializing from int array
    /**********************************************************
     */

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalDateTime value = READER.readValue("[1986,1,17,15,43]");
        LocalDateTime time = LocalDateTime.of(1986, Month.JANUARY, 17, 15, 43);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalDateTime value = READER.readValue("[2013,8,21,9,22,57]");
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 57);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Nanoseconds() throws Exception
    {
        ObjectReader r = READER
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2013,8,21,9,22,0,57]");
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Milliseconds() throws Exception
    {
        ObjectReader r = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2013,8,21,9,22,0,57]");
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57000000);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Nanoseconds() throws Exception
    {
        ObjectReader r = MAPPER.readerFor(LocalDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2005,11,5,22,31,5,829837]");
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds01() throws Exception
    {
        ObjectReader r = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2005,11,5,22,31,5,829837]");

        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds02() throws Exception
    {
        ObjectReader r = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2005,11,5,22,31,5,829]");
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829000000);
        assertEquals("The value is not correct.", time, value);
    }

    /*
    /**********************************************************
    /* Tests for deserializing from textual representation
    /**********************************************************
     */
    
    @Test
    public void testDeserializationAsString01() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(1986, Month.JANUARY, 17, 15, 43);
        LocalDateTime value = MAPPER.readValue('"' + time.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", time, value);

        assertEquals("The value is not correct.",
                LocalDateTime.of(2000, Month.JANUARY, 1, 12, 0),
                READER.readValue(quote("2000-01-01T12:00")));
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 57);
        LocalDateTime value = MAPPER.readValue('"' + time.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        LocalDateTime value = MAPPER.readValue('"' + time.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsString04() throws Exception
    {
        Instant instant = Instant.now();
        LocalDateTime value = MAPPER.readValue('"' + instant.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", LocalDateTime.ofInstant(instant, ZoneOffset.UTC), value);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            READER.readValue(quote("notalocaldatetime"));
            fail("expected fail");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type");
            verifyException(e, "from String \"");
        }
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
     */
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "datetime";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String dateValAsNullStr = null;
        String dateValAsEmptyStr = "";

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, dateValAsNullStr));
        Map<String, LocalDateTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        LocalDateTime actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, dateValAsEmptyStr));
        Map<String, LocalDateTime> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        LocalDateTime actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting",actualDateFromNullStr, actualDateFromEmptyStr);
    }

    @Test( expected =  MismatchedInputException.class)
    public void testStrictDeserializFromEmptyString() throws Exception {

        final String key = "datetime";
        final ObjectMapper mapper = mapperBuilder()
                .withConfigOverride(LocalDateTime.class,
                        c -> c.setFormat(JsonFormat.Value.forLeniency(false))
                )
                .build();
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);
        final String dateValAsNullStr = null;

        // even with strict, null value should be deserialized without throwing an exception
        String valueFromNullStr = mapper.writeValueAsString(asMap(key, dateValAsNullStr));
        Map<String, LocalDateTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String dateValAsEmptyStr = "";
        // TODO: nothing stops us from writing an empty string, maybe there should be a check there too?
        String valueFromEmptyStr = mapper.writeValueAsString(asMap("date", dateValAsEmptyStr));
        // with strict, deserializing an empty string is not permitted
        objectReader.readValue(valueFromEmptyStr);
    }

    /*
    /**********************************************************
    /* Tests for alternate array handling
    /**********************************************************
     */

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[\"2000-01-01T12:00\"]");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_STRING) within Array");
        }
    }
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        // works even without the feature enabled
        assertNull(READER.readValue("[]"));
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        LocalDateTime value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue("[\"2000-01-01T12:00\"]");
        assertEquals("The value is not correct.",
                LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        LocalDateTime value = READER
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
               .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
               .readValue("[]");
        assertNull(value);
    }

    /*
    /**********************************************************
    /* Tests for polymorphic handling
    /**********************************************************
     */
    
    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        final ObjectMapper m = newMapperBuilder()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = m.readerFor(Temporal.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(
                "[\"" + LocalDateTime.class.getName() + "\",[2005,11,5,22,31,5,829837]]");
        assertTrue("The value should be a LocalDateTime.", value instanceof LocalDateTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 422000000);

        final ObjectMapper m = newMapperBuilder()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = m.readerFor(Temporal.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(
                "[\"" + LocalDateTime.class.getName() + "\",[2005,11,5,22,31,5,422]]");
        assertTrue("The value should be a LocalDateTime.", value instanceof LocalDateTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        final ObjectMapper m = newMapperBuilder().
                addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = m.readValue(
                "[\"" + LocalDateTime.class.getName() + "\",\"" + time.toString() + "\"]", Temporal.class
        );
        assertTrue("The value should be a LocalDateTime.", value instanceof LocalDateTime);
        assertEquals("The value is not correct.", time, value);
    }

    /*
    /**********************************************************
    /* Tests for `DeserialiazationProblemHandler` usage
    /**********************************************************
     */
    
    @Test
    public void testDateTimeExceptionIsHandled() throws Throwable
    {
        LocalDateTime now = LocalDateTime.now();
        DeserializationProblemHandler handler = new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
                   String valueToConvert, String failureMsg) throws IOException {
                if (LocalDateTime.class == targetType) {
                    if ("now".equals(valueToConvert)) {
                        return now;
                    }
                }
                return NOT_HANDLED;
            }
        };
        ObjectMapper handledMapper = mapperBuilder().addHandler(handler).build();
        assertEquals(now, handledMapper.readValue(quote("now"), LocalDateTime.class));
    }

    @Test
    public void testUnexpectedTokenIsHandled() throws Throwable
    {
        LocalDateTime now = LocalDateTime.now();
        DeserializationProblemHandler handler = new DeserializationProblemHandler() {
            @Override
            public Object handleUnexpectedToken(DeserializationContext ctxt, Class<?> targetType,
                   JsonToken t, JsonParser p, String failureMsg) throws IOException {
                if (LocalDateTime.class == targetType) {
                    if (t.isBoolean()) {
                        return now;
                    }
                }
                return NOT_HANDLED;
            }
        };
        ObjectMapper handledMapper = mapperBuilder().addHandler(handler).build();
        assertEquals(now, handledMapper.readValue("true", LocalDateTime.class));
    }
    
    /*
    /**********************************************************
    /* Tests for specific reported issues
    /**********************************************************
     */

    // [datatype-jrs310#54]
    @Test
    public void testDeserializeToDate() throws Exception
    {
        ObjectMapper m = newMapperBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
        String localDateTimeJson = m.writeValueAsString(LocalDateTime.of(1999,10,12,13,45,5));
        assertEquals("\"1999-10-12T13:45:05\"", localDateTimeJson);
        Date date = m.readValue(localDateTimeJson,Date.class);
        assertNotNull(date);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(date.getTime());
        assertEquals(1999, cal.get(Calendar.YEAR));
        assertEquals(12, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(13, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(45, cal.get(Calendar.MINUTE));
        assertEquals(5, cal.get(Calendar.SECOND));
    }

    // [modules-java8#47]: should indicate why timestamp won't work
    @Test
    public void testDeserilizeFromSimpleTimestamp() throws Exception
    {
        ObjectReader r = MAPPER.readerFor(LocalDateTime.class);
        LocalDateTime value;
        try {
            value = r.readValue("1235");
            fail("Should not succeed, instead got: "+value);
        } catch (MismatchedInputException e) {
            verifyException(e, "raw timestamp (1235) not allowed for `java.time.LocalDateTime`");
        }
    }
}
