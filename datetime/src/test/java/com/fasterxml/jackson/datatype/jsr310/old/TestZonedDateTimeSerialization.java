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

package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

import static org.junit.jupiter.api.Assertions.*;

public class TestZonedDateTimeSerialization extends ModuleTestBase {
    private static final ZoneId Z1 = ZoneId.of("America/Chicago");

    private static final ZoneId Z2 = ZoneId.of("America/Anchorage");

    private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");

    private static final ZoneId UTC = ZoneOffset.UTC;

    private static final ZoneId DEFAULT_TZ = UTC;

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp()
    {
        this.mapper = newMapper();
    }

    @Test
    public void testSerializationAsTimestamp01Nanoseconds() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("0.0", value);
    }

    @Test
    public void testSerializationAsTimestamp01Milliseconds() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("0", value);
    }

    @Test
    public void testSerializationAsTimestamp02Nanoseconds() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("123456789.183917322", value);
    }

    @Test
    public void testSerializationAsTimestamp02Milliseconds() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("123456789183", value);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals(DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), value);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals(Long.toString(date.toInstant().toEpochMilli()), value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals('"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals('"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals('"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationAsStringWithZoneIdOff() throws Exception {
        // old behaviour is to write with zone id no matter what
        ZonedDateTime date = ZonedDateTime.now(Z3);
        this.mapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String value = mapper.writeValueAsString(date);

        assertEquals("The value is incorrect", "\"" + date.toString() + "\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("The value is not correct.",
                "[\"" + ZonedDateTime.class.getName() + "\",123456789.183917322]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("The value is not correct.",
                "[\"" + ZonedDateTime.class.getName() + "\",123456789183]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("The value is not correct.",
                "[\"" + ZonedDateTime.class.getName() + "\",\"" + date.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsFloat01WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        ZonedDateTime value = this.mapper.readValue("0.000000000", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat01WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue("0.000000000", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat02WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        ZonedDateTime value = this.mapper.readValue("123456789.183917322", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat02WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue("123456789.183917322", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat03WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        ZonedDateTime value = this.mapper.readValue(
                DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), ZonedDateTime.class
                );

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat03WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue(
                DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), ZonedDateTime.class
                );

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        ZonedDateTime value = this.mapper.readValue("0", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue("0", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        ZonedDateTime value = this.mapper.readValue("0", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue("0", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        ZonedDateTime value = this.mapper.readValue("123456789", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue("123456789", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        ZonedDateTime value = this.mapper.readValue("123456789422", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue("123456789422", ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        ZonedDateTime value = this.mapper.readValue(Long.toString(date.toEpochSecond()), ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue(Long.toString(date.toEpochSecond()), ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        ZonedDateTime value =
                this.mapper.readValue(Long.toString(date.toInstant().toEpochMilli()), ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value =
                this.mapper.readValue(Long.toString(date.toInstant().toEpochMilli()), ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString01WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString01WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString01WithTimeZoneTurnedOff() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(Z1, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString02WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString02WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString02WithTimeZoneTurnedOff() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(Z2, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString03WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(DEFAULT_TZ, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString03WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(ZoneId.systemDefault().normalized(), value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString03WithTimeZoneTurnedOff() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        ZonedDateTime value = this.mapper.readValue('"' + date.toString() + '"', ZonedDateTime.class);

        assertNotNull(value);
        assertEquals(date, value);
        assertEquals(Z3, value.getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo01WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",123456789.183917322]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(DEFAULT_TZ, ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo01WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",123456789.183917322]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(ZoneId.systemDefault().normalized(), ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo02WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",123456789]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(DEFAULT_TZ, ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo02WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",123456789]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(ZoneId.systemDefault().normalized(), ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo03WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",123456789422]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(DEFAULT_TZ, ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo03WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",123456789422]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(ZoneId.systemDefault().normalized(), ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo04WithoutTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",\"" + date.toString() + "\"]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(DEFAULT_TZ, ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZone() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",\"" + date.toString() + "\"]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(ZoneId.systemDefault().normalized(), ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZoneTurnedOff() throws Exception
    {
        ZonedDateTime date = ZonedDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + ZonedDateTime.class.getName() + "\",\"" + date.toString() + "\"]", Temporal.class
        );

        assertNotNull(value);
        assertInstanceOf(ZonedDateTime.class, value, "The value should be an ZonedDateTime.");
        assertEquals(date, (ZonedDateTime) value);
        assertEquals(Z3, ((ZonedDateTime) value).getZone(), "The time zone is not correct.");
    }

}
