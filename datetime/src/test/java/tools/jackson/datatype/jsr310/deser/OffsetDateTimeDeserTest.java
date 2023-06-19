package tools.jackson.datatype.jsr310.deser;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.jsr310.MockObjectConfiguration;
import tools.jackson.datatype.jsr310.ModuleTestBase;
import tools.jackson.datatype.jsr310.util.DecimalUtils;

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

    private ObjectMapper MAPPER = newMapper();
    private ObjectMapper MAPPER_DEFAULT_TZ = newMapper(TimeZone.getDefault());

    @Test
    public void testDeserializationAsFloat01WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER.readValue("0.000000000", OffsetDateTime.class);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readValue("0.000000000", OffsetDateTime.class);
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat02WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        OffsetDateTime value = MAPPER.readValue("123456789.183917322", OffsetDateTime.class);
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
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        OffsetDateTime value = MAPPER
                .readerFor(OffsetDateTime.class)
                .readValue(DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()));
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsFloat03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .readValue(DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()));
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("0");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .with(TimeZone.getDefault())
                .readValue("0");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("0");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt01MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("0");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("123456789");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("123456789");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("123456789422");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt02MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("123456789422");
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(Long.toString(date.toEpochSecond()));
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03NanosecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano(), ChronoUnit.NANOS);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(Long.toString(date.toEpochSecond()));
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000L), ChronoUnit.NANOS);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(Long.toString(date.toInstant().toEpochMilli()));
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsInt03MillisecondsWithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        date = date.minus(date.getNano() - (date.get(ChronoField.MILLI_OF_SECOND) * 1_000_000L), ChronoUnit.NANOS);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(Long.toString(date.toInstant().toEpochMilli()));
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
        ObjectMapper m = mapperBuilder()
                .defaultTimeZone(TimeZone.getDefault())
                .build();
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
        ObjectMapper m = mapperBuilder()
                .defaultTimeZone(TimeZone.getDefault())
                .build();
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
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsString01WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsString01WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
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
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsString02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsString02WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z2), value.getOffset());
    }

    @Test
    public void testDeserializationAsString02WithTimeZoneColonless() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        ObjectReader r = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        String sDate = offsetWithoutColon(FORMATTER.format(date));

        OffsetDateTime value = r.readValue('"' + sDate + '"');

        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getOffset(value, Z2), value.getOffset());
    }

    @Test
    public void testDeserializationAsString03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        OffsetDateTime value = MAPPER.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, value.getOffset());
    }

    @Test
    public void testDeserializationAsString03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
        assertIsEqual(date, value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), value.getOffset());
    }

    @Test
    public void testDeserializationAsString03WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        OffsetDateTime value = MAPPER_DEFAULT_TZ.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue('"' + FORMATTER.format(date) + '"');
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
        ObjectMapper m = newMapperBuilder()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
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
        ObjectMapper m = newMapperBuilder(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
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
        ObjectMapper m = newMapperBuilder()
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        Temporal value = m.readerFor(Temporal.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789]");
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo02WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 0), Z2);
        ObjectMapper m = newMapperBuilder(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        Temporal value = m.readerFor(Temporal.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789]");
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo03WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        ObjectMapper m = newMapperBuilder()
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        Temporal value = m.readerFor(Temporal.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789422]");
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo03WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 422000000), Z2);
        ObjectMapper m = newMapperBuilder(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        Temporal value = m.readerFor(Temporal.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",123456789422]");
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo04WithoutTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectMapper m = newMapperBuilder()
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        Temporal value = m.readerFor(Temporal.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]");
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", ZoneOffset.UTC, ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        ObjectMapper m = newMapperBuilder(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        Temporal value = m.readerFor(Temporal.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]");
        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        assertIsEqual(date, (OffsetDateTime) value);
        assertEquals("The time zone is not correct.", getDefaultOffset(date), ((OffsetDateTime) value).getOffset());
    }

    @Test
    public void testDeserializationWithTypeInfo04WithTimeZoneTurnedOff() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);

        ObjectMapper m = newMapperBuilder(TimeZone.getDefault())
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        Temporal value = m.readerFor(Temporal.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]");

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
        String json = MAPPER.writeValueAsString(input);
        assertEquals(a2q("{'value':'1970_01_01T00:00:00+0000'}"), json);

        Wrapper result = MAPPER.readValue(json, Wrapper.class);
        assertEquals(input.value, result.value);
    }

    // [datatype-jsr310#79]
    @Test
    public void testRoundTripOfOffsetDateTimeAndJavaUtilDate() throws Exception
    {
        Instant givenInstant = LocalDate.of(2016, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();

        String json = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(java.util.Date.from(givenInstant));
        OffsetDateTime actual = MAPPER.readerFor(OffsetDateTime.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(json);
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
        final ObjectMapper mapper = mapperBuilder()
                .withConfigOverride(OffsetDateTime.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
            .build();

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
        ObjectMapper m = mapperBuilder()
                .enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .defaultTimeZone(TimeZone.getTimeZone(Z1))
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
        );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        OffsetDateTime actualValue = (OffsetDateTime) value;
        assertIsEqual(date, actualValue);
        assertEquals(date.getOffset(),actualValue.getOffset());
    }

    @Test
    public void testDeserializationNoAdjustIfMAX() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.MAX;
        ObjectMapper m = mapperBuilder()
                .enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .defaultTimeZone(TimeZone.getTimeZone(Z1))
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        Temporal value = m.readValue(
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
        );

        assertTrue("The value should be an OffsetDateTime.", value instanceof OffsetDateTime);
        OffsetDateTime actualValue = (OffsetDateTime) value;
        assertIsEqual(date, actualValue);
        assertEquals(date.getOffset(),actualValue.getOffset());
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
