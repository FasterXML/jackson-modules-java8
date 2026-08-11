package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for [modules-java8#76]: {@link JavaTimeFeature#ALWAYS_WRITE_SUBSECOND_DIGITS}.
 */
public class AlwaysWriteSubSecondDigits76Test extends ModuleTestBase
{
    static class Wrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public OffsetDateTime value;

        Wrapper(OffsetDateTime v) { value = v; }
    }

    static class ShapeOnlyWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Instant value;

        ShapeOnlyWrapper(Instant v) { value = v; }
    }

    // NOTE: cannot use `ModuleTestBase.mapperBuilder()` here, since it already registers a
    // plain `JavaTimeModule` and duplicate registrations of the same module are ignored
    private static JsonMapper.Builder builderWithFeature() {
        return JsonMapper.builder()
                .defaultLocale(Locale.ENGLISH)
                .addModule(new JavaTimeModule()
                        .enable(JavaTimeFeature.ALWAYS_WRITE_SUBSECOND_DIGITS));
    }

    private final ObjectMapper MAPPER = builderWithFeature()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private final ObjectMapper DEFAULT_MAPPER = mapperBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    public void testInstantZeroSubSecond() throws Exception
    {
        Instant value = Instant.parse("2017-09-14T04:28:48Z");
        // Default: sub-second field omitted entirely
        assertEquals(q("2017-09-14T04:28:48Z"), DEFAULT_MAPPER.writeValueAsString(value));
        // Enabled: zero-padded to millisecond precision
        assertEquals(q("2017-09-14T04:28:48.000Z"), MAPPER.writeValueAsString(value));
    }

    @Test
    public void testInstantHigherPrecisionNotTruncated() throws Exception
    {
        assertEquals(q("2017-09-14T04:28:48.100Z"),
                MAPPER.writeValueAsString(Instant.parse("2017-09-14T04:28:48.100Z")));
        assertEquals(q("2017-09-14T04:28:48.123456Z"),
                MAPPER.writeValueAsString(Instant.parse("2017-09-14T04:28:48.123456Z")));
        assertEquals(q("2017-09-14T04:28:48.123456789Z"),
                MAPPER.writeValueAsString(Instant.parse("2017-09-14T04:28:48.123456789Z")));
    }

    @Test
    public void testOffsetDateTime() throws Exception
    {
        OffsetDateTime value = OffsetDateTime.parse("2017-09-14T04:28:48+02:00");
        assertEquals(q("2017-09-14T04:28:48+02:00"), DEFAULT_MAPPER.writeValueAsString(value));
        assertEquals(q("2017-09-14T04:28:48.000+02:00"), MAPPER.writeValueAsString(value));

        // Note: the JDK ISO formatter renders 100 msec as ".1"; with the feature on,
        // width is stable at (at least) 3 digits
        OffsetDateTime millis = OffsetDateTime.parse("2017-09-14T04:28:48.100+02:00");
        assertEquals(q("2017-09-14T04:28:48.1+02:00"), DEFAULT_MAPPER.writeValueAsString(millis));
        assertEquals(q("2017-09-14T04:28:48.100+02:00"), MAPPER.writeValueAsString(millis));
    }

    @Test
    public void testZonedDateTime() throws Exception
    {
        ZonedDateTime value = ZonedDateTime.parse("2017-09-14T04:28:48+02:00[Europe/Budapest]");
        assertEquals(q("2017-09-14T04:28:48.000+02:00"), MAPPER.writeValueAsString(value));
    }

    @Test
    public void testZonedDateTimeWithZoneId() throws Exception
    {
        ObjectMapper mapper = builderWithFeature()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
                .build();
        ZonedDateTime value = ZonedDateTime.parse("2017-09-14T04:28:48+02:00[Europe/Budapest]");
        assertEquals(q("2017-09-14T04:28:48.000+02:00[Europe/Budapest]"),
                mapper.writeValueAsString(value));
    }

    @Test
    public void testLocalDateTime() throws Exception
    {
        LocalDateTime value = LocalDateTime.parse("2017-09-14T04:28:48");
        assertEquals(q("2017-09-14T04:28:48"), DEFAULT_MAPPER.writeValueAsString(value));
        assertEquals(q("2017-09-14T04:28:48.000"), MAPPER.writeValueAsString(value));

        // Seconds keep being written even when zero (as with the JDK ISO formatter)
        LocalDateTime noSeconds = LocalDateTime.parse("2017-09-14T04:28");
        assertEquals(q("2017-09-14T04:28:00"), DEFAULT_MAPPER.writeValueAsString(noSeconds));
        assertEquals(q("2017-09-14T04:28:00.000"), MAPPER.writeValueAsString(noSeconds));
    }

    // Feature must not leak into numeric timestamp serialization
    @Test
    public void testTimestampsUnaffected() throws Exception
    {
        ObjectMapper mapper = builderWithFeature()
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertEquals("1505363328.000000000",
                mapper.writeValueAsString(Instant.parse("2017-09-14T04:28:48Z")));
    }

    // ... nor override an explicit `@JsonFormat` pattern
    @Test
    public void testExplicitPatternWins() throws Exception
    {
        assertEquals(a2q("{'value':'2017-09-14T04:28:48'}"),
                MAPPER.writeValueAsString(new Wrapper(OffsetDateTime.parse("2017-09-14T04:28:48Z"))));
    }

    // Values written with the feature on must still be readable
    @Test
    public void testRoundTrip() throws Exception
    {
        for (String raw : new String[] {
                "2017-09-14T04:28:48Z", "2017-09-14T04:28:48.123456789Z",
                "1970-01-01T00:00:00Z", "+10000-09-14T04:28:48Z", "-0100-09-14T04:28:48Z" }) {
            Instant value = Instant.parse(raw);
            String json = MAPPER.writeValueAsString(value);
            assertEquals(value, MAPPER.readValue(json, Instant.class),
                    "Round-trip failed for " + raw + " (serialized as " + json + ")");
        }
    }

    // The full `Instant` range must keep working: it is wider than that of
    // `LocalDateTime`, so the feature must not route `Instant` through a
    // zone-bound formatter
    @Test
    public void testInstantMinMax() throws Exception
    {
        // no sub-second part -> padded
        assertEquals(q("-1000000000-01-01T00:00:00Z"),
                DEFAULT_MAPPER.writeValueAsString(Instant.MIN));
        assertEquals(q("-1000000000-01-01T00:00:00.000Z"),
                MAPPER.writeValueAsString(Instant.MIN));

        // already at nanosecond precision -> unchanged
        assertEquals(q("+1000000000-12-31T23:59:59.999999999Z"),
                DEFAULT_MAPPER.writeValueAsString(Instant.MAX));
        assertEquals(q("+1000000000-12-31T23:59:59.999999999Z"),
                MAPPER.writeValueAsString(Instant.MAX));

        // and just inside the `LocalDateTime` boundary, either side
        assertEquals(q("-999999999-01-01T00:00:00.000Z"),
                MAPPER.writeValueAsString(LocalDateTime.MIN.toInstant(ZoneOffset.UTC)));
        assertEquals(q("-1000000000-12-31T23:59:59.000Z"),
                MAPPER.writeValueAsString(LocalDateTime.MIN.toInstant(ZoneOffset.UTC).minusSeconds(1)));
    }

    // `Instant.toString()` writes 0, 3, 6 or 9 sub-second digits; only the
    // zero case may be rewritten, the rest must be passed through untouched
    @Test
    public void testInstantSubSecondWidthsPreserved() throws Exception
    {
        assertEquals(q("1970-01-01T00:00:00.000000001Z"),
                MAPPER.writeValueAsString(Instant.ofEpochSecond(0, 1)));
        assertEquals(q("1970-01-01T00:00:00.000001Z"),
                MAPPER.writeValueAsString(Instant.ofEpochSecond(0, 1000)));
        assertEquals(q("1970-01-01T00:00:00.001Z"),
                MAPPER.writeValueAsString(Instant.ofEpochSecond(0, 1000000)));
    }

    // `@JsonFormat(shape=STRING)` without a pattern must not lose the padding
    @Test
    public void testShapeStringWithoutPattern() throws Exception
    {
        assertEquals(a2q("{'value':'2017-09-14T04:28:48.000Z'}"),
                MAPPER.writeValueAsString(new ShapeOnlyWrapper(Instant.parse("2017-09-14T04:28:48Z"))));
    }
}
