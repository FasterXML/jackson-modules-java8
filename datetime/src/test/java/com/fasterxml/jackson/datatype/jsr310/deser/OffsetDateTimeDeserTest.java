package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.*;

public class OffsetDateTimeDeserTest
    extends ModuleTestBase
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final TypeReference<Map<String, OffsetDateTime>> MAP_TYPE_REF = new TypeReference<Map<String, OffsetDateTime>>() { };

    private static final ZoneId Z1 = ZoneId.of("America/Chicago");

    private static final ZoneId Z2 = ZoneId.of("America/Anchorage");

    private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");

    final static class Wrapper {
        @JsonFormat(
                pattern="yyyy_MM_dd'T'HH:mm:ssZ",
                shape=JsonFormat.Shape.STRING)
        public OffsetDateTime value;

        public Wrapper() { }
        public Wrapper(OffsetDateTime v) { value = v; }
    }

    static class WrapperWithReadTimestampsAsNanosDisabled {
        @JsonFormat(
            without=Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
        )
        public OffsetDateTime value;

        public WrapperWithReadTimestampsAsNanosDisabled() { }
        public WrapperWithReadTimestampsAsNanosDisabled(OffsetDateTime v) { value = v; }
    }

    static class WrapperWithReadTimestampsAsNanosEnabled {
        @JsonFormat(
            with=Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
        )
        public OffsetDateTime value;

        public WrapperWithReadTimestampsAsNanosEnabled() { }
        public WrapperWithReadTimestampsAsNanosEnabled(OffsetDateTime v) { value = v; }
    }

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testDeserializationAsFloat01WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER.readValue("0.000000000", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectMapper m = newMapper()
            .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue("0.000000000", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat02WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        OffsetDateTime value = MAPPER.readValue("123456789.183917322", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);

        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(TimeZone.getDefault())
                .readValue("123456789.183917322");

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        OffsetDateTime value = MAPPER.readValue(
                DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), OffsetDateTime.class
                );

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        ObjectMapper m = newMapper()
                .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue(
                DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), OffsetDateTime.class
        );

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        OffsetDateTime value = m.readValue("0", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
                .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue("0", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        OffsetDateTime value = m.readValue("0", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue("0", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);

        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        OffsetDateTime value = m.readValue("123456789", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
                .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue("123456789", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        OffsetDateTime value = m.readValue("123456789422", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue("123456789422", OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        OffsetDateTime value = m.readValue(Long.toString(date.toEpochSecond()), OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
                .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue(Long.toString(date.toEpochSecond()), OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000L), ChronoUnit.NANOS);

        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        OffsetDateTime value =
                m.readValue(Long.toString(date.toInstant().toEpochMilli()), OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000L), ChronoUnit.NANOS);

        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value =
                m.readValue(Long.toString(date.toInstant().toEpochMilli()), OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt04NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z1);
        ObjectMapper m = newMapper();
        WrapperWithReadTimestampsAsNanosEnabled actual = m.readValue(
            a2q("{'value':123456789}"),
            WrapperWithReadTimestampsAsNanosEnabled.class);

        assertNotNull("The actual should not be null.", actual);
        assertNotNull("The actual value should not be null.", actual.value);
        assertIsEqual(date, actual.value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, actual.value.getOffset());
    }

    @Test
    public void testDeserializationAsInt04NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z1);
        ObjectMapper m = newMapper()
            .setTimeZone(TimeZone.getDefault());
        WrapperWithReadTimestampsAsNanosEnabled actual = m.readValue(
            a2q("{'value':123456789}"),
            WrapperWithReadTimestampsAsNanosEnabled.class);

        assertNotNull("The actual should not be null.", actual);
        assertNotNull("The actual value should not be null.", actual.value);
        assertIsEqual(date, actual.value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), actual.value.getOffset());
    }

    @Test
    public void testDeserializationAsInt04MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z1);
        ObjectMapper m = newMapper();
        WrapperWithReadTimestampsAsNanosDisabled actual = m.readValue(
            a2q("{'value':123456789422}"),
            WrapperWithReadTimestampsAsNanosDisabled.class);

        assertNotNull("The actual should not be null.", actual);
        assertNotNull("The actual value should not be null.", actual.value);
        assertIsEqual(date, actual.value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, actual.value.getOffset());
    }

    @Test
    public void testDeserializationAsInt04MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z1);
        ObjectMapper m = newMapper()
            .setTimeZone(TimeZone.getDefault());
        WrapperWithReadTimestampsAsNanosDisabled actual = m.readValue(
            a2q("{'value':123456789422}"),
            WrapperWithReadTimestampsAsNanosDisabled.class);

        assertNotNull("The actual should not be null.", actual);
        assertNotNull("The actual value should not be null.", actual.value);
        assertIsEqual(date, actual.value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), actual.value.getOffset());
    }

    @Test
    public void testDeserializationAsString01WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsString01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .with(TimeZone.getDefault());
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsString01WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .with(TimeZone.getDefault());
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z1), value.getOffset());
    }

    @Test
    public void testDeserializationAsString01WithTimeZoneColonless() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        String sDate = offsetWithoutColon(FORMATTER.format(date));
        OffsetDateTime value = r.readValue('"' + sDate + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z1), value.getOffset());
    }

    @Test
    public void testDeserializationAsString02WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsString02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .with(TimeZone.getDefault());
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsString02WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .setTimeZone(TimeZone.getDefault());
        OffsetDateTime value = m.readValue('"' + FORMATTER.format(date) + '"', OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z2), value.getOffset());
    }

    @Test
    public void testDeserializationAsString02WithTimeZoneColonless() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        String sDate = offsetWithoutColon(FORMATTER.format(date));

        OffsetDateTime value = m.readValue('"' + sDate + '"', OffsetDateTime.class);

        assertNotNull("The value should not be null.", value);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z2), value.getOffset());
    }

    @Test
    public void testDeserializationAsString03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsString03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .with(TimeZone.getDefault());
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsString03WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .with(TimeZone.getDefault());
        OffsetDateTime value = r.readValue('"' + FORMATTER.format(date) + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z3), value.getOffset());
    }


    @Test
    public void testDeserializationAsString03WithTimeZoneColonless() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        String sDate = offsetWithoutColon(FORMATTER.format(date));

        OffsetDateTime value = r.readValue('"' + sDate + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z3), value.getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo01WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        ObjectMapper m = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789.183917322]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        ObjectMapper m = newMapper()
            .setTimeZone(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789.183917322]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo02WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
            .setTimeZone(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789422]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .setTimeZone(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789422]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo04WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true)
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true)
            .setTimeZone(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        ObjectMapper m = newMapper()
            .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
            .setTimeZone(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
                );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        OffsetDateTime cast = (OffsetDateTime) value;
        assertIsEqual(date, cast);
        assertEquals("The time zone is not correct.", getOffset(cast, Z3), cast.getOffset());
    }

    @Test
    public void testCustomPatternWithAnnotations() throws Exception
    {
        OffsetDateTime inputValue = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), UTC);
        final Wrapper input = new Wrapper(inputValue);
        final ObjectMapper m = newMapper();
        String json = m.writeValueAsString(input);
        assertEquals(a2q("{'value':'1970_01_01T00:00:00+0000'}"), json);

        Wrapper result = m.readValue(json, Wrapper.class);
        assertEquals(input.value, result.value);
    }

    // [datatype-jsr310#79]
    @Test
    public void testRoundTripOfOffsetDateTimeAndJavaUtilDate() throws Exception
    {
        ObjectMapper mapper = newMapper();
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        Instant givenInstant = LocalDate.of(2016, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        String json = mapper.writeValueAsString(java.util.Date.from(givenInstant));

        OffsetDateTime actual = mapper.readValue(json, OffsetDateTime.class); // this fails

        assertEquals(givenInstant.atOffset(ZoneOffset.UTC), actual);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "OffsetDateTime";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, OffsetDateTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        OffsetDateTime actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        Map<String, OffsetDateTime> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        OffsetDateTime actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting", null, actualDateFromEmptyStr);
    }

    @Test ( expected =  MismatchedInputException.class)
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "OffsetDateTime";
        final ObjectMapper mapper = mapperBuilder().build();
        mapper.configOverride(OffsetDateTime.class)
                .setFormat(JsonFormat.Value.forLeniency(false));

        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, OffsetDateTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        objectReader.readValue(valueFromEmptyStr);
    }

    // [module-java8#166]
    @Test
    public void testDeserializationNoAdjustIfMIN() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.MIN;
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true)
                .setTimeZone(TimeZone.getTimeZone(Z1))
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
        );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        OffsetDateTime actualValue = (OffsetDateTime) value;
        assertIsEqual(date, actualValue);
        assertEquals(date.getOffset(),actualValue.getOffset());
    }

    @Test
    public void testDeserializationNoAdjustIfMAX() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.MAX;
        ObjectMapper m = newMapper()
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true)
                .setTimeZone(TimeZone.getTimeZone(Z1))
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
        );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        OffsetDateTime actualValue = (OffsetDateTime) value;
        assertIsEqual(date, actualValue);
        assertEquals(date.getOffset(),actualValue.getOffset());
    }

    // [jackson-modules-java8#308] Can't deserialize OffsetDateTime.MIN: Invalid value for EpochDay
    @Test
    public void testOffsetDateTimeMinOrMax()
            throws Exception
    {
        _testOffsetDateTimeMinOrMax(OffsetDateTime.MIN);
        _testOffsetDateTimeMinOrMax(OffsetDateTime.MAX);
    }

    private void _testOffsetDateTimeMinOrMax(OffsetDateTime offsetDateTime)
        throws Exception
    {
        ObjectMapper mapper = newMapper();
        String ser = mapper.writeValueAsString(offsetDateTime);
        OffsetDateTime result = mapper.readValue(ser, OffsetDateTime.class);
        assertIsEqual(offsetDateTime, result);
    }

    private static void assertIsEqual(OffsetDateTime expected, OffsetDateTime actual)
    {
        assertTrue("The value is not correct. Expected timezone-adjusted <" + expected + ">, actual <" + actual + ">.",
                expected.isEqual(actual));
    }

    private static ZoneOffset getDefaultOffset(OffsetDateTime date)
    {
        return ZoneId.systemDefault().getRules().getOffset(date.toLocalDateTime());
    }

    private static ZoneOffset getOffset(OffsetDateTime date, ZoneId zone)
    {
        return zone.getRules().getOffset(date.toLocalDateTime());
    }

    private static String offsetWithoutColon(String string){
        return new StringBuilder(string).deleteCharAt(string.lastIndexOf(":")).toString();
    }
}
