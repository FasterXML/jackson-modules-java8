package com.fasterxml.jackson.datatype.jsr310.old;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

import static org.junit.jupiter.api.Assertions.*;

public class TestDurationSerialization extends ModuleTestBase
{
    private ObjectWriter WRITER;

    @BeforeEach
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

        assertNotNull(value);
        assertEquals("60"+NO_NANOSECS_SUFFIX, value);
    }

    @Test
    public void testSerializationAsTimestampNanoseconds02() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);

        assertNotNull(value);
        assertEquals("13498.000008374", value);
    }

    @Test
    public void testSerializationAsTimestampMilliseconds01() throws Exception
    {
        Duration duration = Duration.ofSeconds(60L, 0);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);

        assertNotNull(value);
        assertEquals("60000", value);
    }

    @Test
    public void testSerializationAsTimestampMilliseconds02() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);

        assertNotNull(value);
        assertEquals("13498000", value);
    }

    @Test
    public void testSerializationAsTimestampMilliseconds03() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 837481723);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);

        assertNotNull(value);
        assertEquals("13498837", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        Duration duration = Duration.ofSeconds(60L, 0);
        String value = WRITER
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(duration);

        assertNotNull(value);
        assertEquals('"' + duration.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = WRITER
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(duration);

        assertNotNull(value);
        assertEquals('"' + duration.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ObjectMapper mapper = newMapper(); // need new to add mix-ins:
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = mapper.writeValueAsString(duration);

        assertNotNull(value);
        assertEquals("[\"" + Duration.class.getName() + "\",13498.000008374]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        ObjectMapper mapper = newMapper(); // need new to add mix-ins:
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        Duration duration = Duration.ofSeconds(13498L, 837481723);
        String value = mapper.writeValueAsString(duration);

        assertNotNull(value);
        assertEquals("[\"" + Duration.class.getName() + "\",13498837]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        ObjectMapper mapper = newMapper(); // need new to add mix-ins:
        mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = mapper.writeValueAsString(duration);

        assertNotNull(value);
        assertEquals("[\"" + Duration.class.getName() + "\",\"" + duration.toString() + "\"]", value);
    }
}
