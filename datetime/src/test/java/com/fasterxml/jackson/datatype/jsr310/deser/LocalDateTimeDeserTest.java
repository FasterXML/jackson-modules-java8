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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

public class LocalDateTimeDeserTest
    extends ModuleTestBase
{
    private final ObjectMapper mapper = newMapper();

    /*
    /**********************************************************
    /* Tests for deserializing from int array
    /**********************************************************
     */
    
    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalDateTime value = mapper.readValue("[1986,1,17,15,43]", LocalDateTime.class);
        LocalDateTime time = LocalDateTime.of(1986, Month.JANUARY, 17, 15, 43);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalDateTime value = mapper.readValue("[2013,8,21,9,22,57]", LocalDateTime.class);
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 57);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Nanoseconds() throws Exception
    {
        ObjectReader r = mapper.readerFor(LocalDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2013,8,21,9,22,0,57]");
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Milliseconds() throws Exception
    {
        ObjectReader r = mapper.readerFor(LocalDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2013,8,21,9,22,0,57]");
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57000000);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Nanoseconds() throws Exception
    {
        ObjectReader r = mapper.readerFor(LocalDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2005,11,5,22,31,5,829837]");
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds01() throws Exception
    {
        ObjectReader r = mapper.readerFor(LocalDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        LocalDateTime value = r.readValue("[2005,11,5,22,31,5,829837]");

        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds02() throws Exception
    {
        ObjectReader r = mapper.readerFor(LocalDateTime.class)
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
        LocalDateTime value = mapper.readValue('"' + time.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 57);
        LocalDateTime value = mapper.readValue('"' + time.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        LocalDateTime value = mapper.readValue('"' + time.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsString04() throws Exception
    {
        Instant instant = Instant.now();
        LocalDateTime value = mapper.readValue('"' + instant.toString() + '"', LocalDateTime.class);
        assertEquals("The value is not correct.", LocalDateTime.ofInstant(instant, ZoneOffset.UTC), value);
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
        final ObjectMapper m = newMapper().addMixIn(Temporal.class, MockObjectConfiguration.class);
        m.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        Temporal value = m.readValue(
                "[\"" + LocalDateTime.class.getName() + "\",[2005,11,5,22,31,5,829837]]", Temporal.class
        );
        assertTrue("The value should be a LocalDateTime.", value instanceof LocalDateTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 422000000);

        final ObjectMapper m = newMapper().addMixIn(Temporal.class, MockObjectConfiguration.class);
        m.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        Temporal value = m.readValue(
                "[\"" + LocalDateTime.class.getName() + "\",[2005,11,5,22,31,5,422]]", Temporal.class
        );
        assertTrue("The value should be a LocalDateTime.", value instanceof LocalDateTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);

        final ObjectMapper m = newMapper().addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + LocalDateTime.class.getName() + "\",\"" + time.toString() + "\"]", Temporal.class
        );
        assertTrue("The value should be a LocalDateTime.", value instanceof LocalDateTime);
        assertEquals("The value is not correct.", time, value);
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
        ObjectMapper m = newMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
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
        ObjectReader r = mapper.readerFor(LocalDateTime.class);
        LocalDateTime value;
        try {
            value = r.readValue("1235");
            fail("Should not succeed, instead got: "+value);
        } catch (MismatchedInputException e) {
            verifyException(e, "raw timestamp (1235) not allowed for `java.time.LocalDateTime`");
        }
    }

    // [modules-java8#80]: handle case-insensitive date/time
    @Test
    public void testDeserializationCaseInsensitiveEnabledOnValue() throws Throwable
    {
        ObjectMapper mapper = newMapper();
        Value format = JsonFormat.Value
            .forPattern("dd-MMM-yyyy HH:mm")
            .withFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        mapper.configOverride(LocalDateTime.class).setFormat(format);
        ObjectReader reader = mapper.readerFor(LocalDateTime.class);
        String[] jsons = new String[] {"'01-Jan-2000 13:14'","'01-JAN-2000 13:14'", "'01-jan-2000 13:14'"};
        for(String json : jsons) {
            expectSuccess(reader, LocalDateTime.of(2000, Month.JANUARY, 1, 13, 14), json);
        }
    }

    @Test
    public void testDeserializationCaseInsensitiveEnabled() throws Throwable
    {
        ObjectMapper mapper = newMapper().configure(DeserializationFeature.ACCEPT_CASE_INSENSITIVE_VALUES, true);
        mapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd-MMM-yyyy HH:mm"));
        ObjectReader reader = mapper.readerFor(LocalDateTime.class);
        String[] jsons = new String[] {"'01-Jan-2000 13:45'","'01-JAN-2000 13:45'", "'01-jan-2000 13:45'"};
        for(String json : jsons) {
            expectSuccess(reader, LocalDateTime.of(2000, Month.JANUARY, 1, 13, 45), json);
        }
    }

    @Test
    public void testDeserializationCaseInsensitiveDisabled() throws Throwable
    {
        ObjectMapper mapper = newMapper().configure(DeserializationFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false);
        mapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd-MMM-yyyy HH:mm"));
        ObjectReader reader = mapper.readerFor(LocalDateTime.class);
        expectSuccess(reader, LocalDateTime.of(2000, Month.JANUARY, 1, 13, 45), "'01-Jan-2000 13:45'");
    }

    @Test
    public void testDeserializationCaseInsensitiveDisabled_InvalidDate() throws Throwable
    {
        ObjectMapper mapper = newMapper().configure(DeserializationFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false);
        mapper.configOverride(LocalDateTime.class).setFormat(JsonFormat.Value.forPattern("dd-MMM-yyyy"));
        ObjectReader reader = mapper.readerFor(LocalDateTime.class);
        String[] jsons = new String[] {"'01-JAN-2000'", "'01-jan-2000'"};
        for(String json : jsons) {
            expectFailure(reader, json);
        }
    }

    private void expectSuccess(ObjectReader reader, Object exp, String json) throws IOException {
        final LocalDateTime value = read(reader, json);
        notNull(value);
        expect(exp, value);
    }

    private void expectFailure(ObjectReader reader, String json) throws Throwable {
        try {
            read(reader, json);
            fail("expected DateTimeParseException");
        } catch (JsonProcessingException e) {
            if (e.getCause() == null) {
                throw e;
            }
            if (!(e.getCause() instanceof DateTimeParseException)) {
                throw e.getCause();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private LocalDateTime read(ObjectReader reader, final String json) throws IOException {
        return reader.readValue(aposToQuotes(json));
    }

    private void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
