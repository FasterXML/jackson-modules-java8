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
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

public class TestOffsetDateTimeSerialization extends ModuleTestBase
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static final DateTimeFormatter FORMATTER_UTC = FORMATTER.withZone(ZoneOffset.UTC);

    private static final ZoneId Z1 = ZoneId.of("America/Chicago");

    private static final ZoneId Z2 = ZoneId.of("America/Anchorage");

    private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");

    private ObjectMapper mapper;

    @BeforeEach
    public void setUp()
    {
        this.mapper = newMapper();
    }

    @Test
    public void testSerializationAsTimestamp01Nanoseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("0.0", value);
    }

    @Test
    public void testSerializationAsTimestamp01Milliseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("0", value);
    }

    @Test
    public void testSerializationAsTimestamp02Nanoseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("123456789.183917322", value);
    }

    @Test
    public void testSerializationAsTimestamp02Milliseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("123456789183", value);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals(DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), value);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = mapper.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals(Long.toString(date.toInstant().toEpochMilli()), value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        String value = mapper.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals('"' + FORMATTER.withZone(Z1).format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = mapper.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals('"' + FORMATTER.withZone(Z2).format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = mapper.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals('"' + FORMATTER.withZone(Z3).format(date) + '"', value);
    }

    @Test
    public void testSerializationAsStringWithMapperTimeZone01() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        String value = newMapper()
                .setTimeZone(TimeZone.getTimeZone(Z1))
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .writeValueAsString(date);
        assertEquals('"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsStringWithMapperTimeZone02() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = newMapper()
                .setTimeZone(TimeZone.getTimeZone(Z2))
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .writeValueAsString(date);
        assertEquals('"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsStringWithMapperTimeZone03() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = newMapper()
                .setTimeZone(TimeZone.getTimeZone(Z3))
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .writeValueAsString(date);
        assertEquals('"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
                .writeValueAsString(date);
        assertEquals("[\"" + OffsetDateTime.class.getName() + "\",123456789.183917322]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .writeValueAsString(date);
        assertEquals("[\"" + OffsetDateTime.class.getName() + "\",123456789183]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = newMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .writeValueAsString(date);
        assertEquals("[\"" + OffsetDateTime.class.getName() + "\",\"" +
                        FORMATTER_UTC.withZone(Z3).format(date) + "\"]", value);
    }

    @Test
    public void testSerializationWithTypeInfoAndMapperTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        String value = newMapper()
                .setTimeZone(TimeZone.getTimeZone(Z3))
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .writeValueAsString(date);
        assertEquals("[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", value);
    }

    @Test
    public void testDeserializationAsFloat01WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        OffsetDateTime value = this.mapper.readValue("0.000000000", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue("0.000000000", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat02WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        OffsetDateTime value = this.mapper.readValue("123456789.183917322", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue("123456789.183917322", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        OffsetDateTime value = this.mapper.readValue(
                DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), OffsetDateTime.class
                );

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsFloat03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue(
                DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), OffsetDateTime.class
                );

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        OffsetDateTime value = this.mapper.readValue("0", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue("0", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        OffsetDateTime value = this.mapper.readValue("0", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue("0", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        OffsetDateTime value = this.mapper.readValue("123456789", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue("123456789", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        OffsetDateTime value = this.mapper.readValue("123456789422", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue("123456789422", OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        OffsetDateTime value = this.mapper.readValue(Long.toString(date.toEpochSecond()), OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue(Long.toString(date.toEpochSecond()), OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        OffsetDateTime value =
                this.mapper.readValue(Long.toString(date.toInstant().toEpochMilli()), OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000), ChronoUnit.NANOS);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value =
                this.mapper.readValue(Long.toString(date.toInstant().toEpochMilli()), OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString01WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString01WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getOffset(value, Z1), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString02WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString02WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getOffset(value, Z2), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(ZoneOffset.UTC, value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getDefaultOffset(date), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationAsString03WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = this.mapper.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull(value);
        assertIsEqual(date, value);
        assertEquals(getOffset(value, Z3), value.getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo01WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789.183917322]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(ZoneOffset.UTC, ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789.183917322]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(getDefaultOffset(date), ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo02WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(ZoneOffset.UTC, ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(getDefaultOffset(date), ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789422]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(ZoneOffset.UTC, ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789422]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(getDefaultOffset(date), ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo04WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(ZoneOffset.UTC, ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals(getDefaultOffset(date), ((OffsetDateTime) value).getOffset(), "The time zone is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        this.mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
        this.mapper.setTimeZone(TimeZone.getDefault());
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(OffsetDateTime.class, value, "The value should be an OffsetDateTime.");
        OffsetDateTime cast = (OffsetDateTime) value;
        assertIsEqual(date, cast);
        assertEquals(getOffset(cast, Z3), cast.getOffset(), "The time zone is not correct.");
    }

    private static void assertIsEqual(OffsetDateTime expected, OffsetDateTime actual)
    {
        assertTrue(expected.isEqual(actual),
                "The value is not correct. Expected timezone-adjusted <" + expected + ">, actual <" + actual + ">.");
    }

    private static ZoneOffset getDefaultOffset(OffsetDateTime date)
    {
        return ZoneId.systemDefault().getRules().getOffset(date.toLocalDateTime());
    }

    private static ZoneOffset getOffset(OffsetDateTime date, ZoneId zone)
    {
        return zone.getRules().getOffset(date.toLocalDateTime());
    }
}
