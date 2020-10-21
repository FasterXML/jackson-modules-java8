package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
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
}
