package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

import static org.junit.Assert.*;

public class DurationSerTest extends ModuleTestBase
{
    private ObjectWriter WRITER;

    @Before
    public void setUp()
    {
        WRITER = newMapper().writer();
    }

    @Test
    public void testSerializationAsTimestampNanoseconds01() throws Exception
    {
        Duration duration = Duration.ofSeconds(60L, 0);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);
        assertEquals("The value is not correct.", "60"+NO_NANOSECS_SUFFIX, value);
    }

    @Test
    public void testSerializationAsTimestampNanoseconds02() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);
        assertEquals("The value is not correct.", "13498.000008374", value);
    }

    // [modules-java8#165]
    @Test
    public void testSerializationAsTimestampNanoseconds03() throws Exception
    {
        ObjectWriter w = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

        // 20-Oct-2020, tatu: Very weird, but "use nanoseconds" actually results
        //   in unit being seconds, with fractions (with nanosec precision)
        String value = w.writeValueAsString(Duration.ofMillis(1L));
        assertEquals("The value is not correct.", "0.001000000", value);

        value = w.writeValueAsString(Duration.ofMillis(-1L));
        assertEquals("The value is not correct.", "-0.001000000", value);
    }
    
    @Test
    public void testSerializationAsTimestampMilliseconds01() throws Exception
    {
        final ObjectWriter w = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        String value = w.writeValueAsString(Duration.ofSeconds(45L, 0));
        assertEquals("The value is not correct.", "45000", value);

        // and with negative value too
        value = w.writeValueAsString(Duration.ofSeconds(-32L, 0));
        assertEquals("The value is not correct.", "-32000", value);
    }

    @Test
    public void testSerializationAsTimestampMilliseconds02() throws Exception
    {
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(Duration.ofSeconds(13498L, 8374));
        assertEquals("The value is not correct.", "13498000", value);
    }

    @Test
    public void testSerializationAsTimestampMilliseconds03() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 837481723);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);
        assertEquals("The value is not correct.", "13498837", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        Duration duration = Duration.ofSeconds(60L, 0);
        String value = WRITER
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(duration);
        assertEquals("The value is not correct.", '"' + duration.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = WRITER
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(duration);
        assertEquals("The value is not correct.", '"' + duration.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
                        SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.",
                "[\"" + Duration.class.getName() + "\",13498.000008374]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        Duration duration = Duration.ofSeconds(13498L, 837481723);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.",
                "[\"" + Duration.class.getName() + "\",13498837]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .build();
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.",
                "[\"" + Duration.class.getName() + "\",\"" + duration.toString() + "\"]", value);
    }

    /*
    /**********************************************************
    /* Tests for custom patterns (modules-java8#189)
    /**********************************************************
     */

    @Test
    public void shouldSerializeInNanos_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("NANOS");

        Duration duration = Duration.ofHours(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "3600000000000", value);
    }

    @Test
    public void shouldSerializeInMicros_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("MICROS");

        Duration duration = Duration.ofMillis(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1000", value);
    }

    @Test
    public void shouldSerializeInMicrosDiscardingFractions_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("MICROS");

        Duration duration = Duration.ofNanos(1500);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    @Test
    public void shouldSerializeInMillis_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("MILLIS");

        Duration duration = Duration.ofSeconds(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1000", value);
    }

    @Test
    public void shouldSerializeInMillisDiscardingFractions_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("MILLIS");

        Duration duration = Duration.ofNanos(1500000);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    @Test
    public void shouldSerializeInSeconds_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("SECONDS");

        Duration duration = Duration.ofMinutes(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "60", value);
    }

    @Test
    public void shouldSerializeInSecondsDiscardingFractions_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("SECONDS");

        Duration duration = Duration.ofMillis(1500);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    @Test
    public void shouldSerializeInMinutes_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("MINUTES");

        Duration duration = Duration.ofHours(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "60", value);
    }

    @Test
    public void shouldSerializeInMinutesDiscardingFractions_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("MINUTES");

        Duration duration = Duration.ofSeconds(90);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    @Test
    public void shouldSerializeInHours_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("HOURS");

        Duration duration = Duration.ofDays(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "24", value);
    }

    @Test
    public void shouldSerializeInHoursDiscardingFractions_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("HOURS");

        Duration duration = Duration.ofMinutes(90);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    @Test
    public void shouldSerializeInHalfDays_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("HALF_DAYS");

        Duration duration = Duration.ofDays(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "2", value);
    }

    @Test
    public void shouldSerializeInHalfDaysDiscardingFractions_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("DAYS");

        Duration duration = Duration.ofHours(30);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    @Test
    public void shouldSerializeInDays_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("DAYS");

        Duration duration = Duration.ofDays(1);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    @Test
    public void shouldSerializeInDaysDiscardingFractions_whenSetAsPattern() throws Exception
    {
        ObjectMapper mapper = _mapperForPatternOverride("DAYS");

        Duration duration = Duration.ofHours(36);
        String value = mapper.writeValueAsString(duration);

        assertEquals("The value is not correct.", "1", value);
    }

    protected ObjectMapper _mapperForPatternOverride(String pattern) {
        ObjectMapper mapper = mapperBuilder()
                .withConfigOverride(Duration.class,
                        cfg -> cfg.setFormat(JsonFormat.Value.forPattern(pattern)))
                .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        return mapper;
    }
}
