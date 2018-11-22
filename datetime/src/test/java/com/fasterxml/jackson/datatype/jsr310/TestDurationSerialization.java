package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestDurationSerialization extends ModuleTestBase
{
    private ObjectWriter WRITER;

    final static class HMSWrapper {
        @JsonFormat(pattern="HH:mm:ss",
                shape=JsonFormat.Shape.STRING)
        public Duration duration;

        public HMSWrapper() { }
        public HMSWrapper(Duration d) { duration = d; }
    }

    final static class DaysDurationWrapper {
        @JsonFormat(pattern="dd' Day(s)'",
                shape=JsonFormat.Shape.STRING)
        public Duration duration;

        public DaysDurationWrapper(Duration d) { duration = d; }
    }

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

        assertNotNull("The value should not be null.", value);
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

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "13498.000008374", value);
    }

    @Test
    public void testSerializationAsTimestampMilliseconds01() throws Exception
    {
        Duration duration = Duration.ofSeconds(60L, 0);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "60000", value);
    }

    @Test
    public void testSerializationAsTimestampMilliseconds02() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = WRITER
                .with(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(duration);

        assertNotNull("The value should not be null.", value);
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

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "13498837", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        Duration duration = Duration.ofSeconds(60L, 0);
        String value = WRITER
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(duration);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + duration.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = WRITER
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(duration);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + duration.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
                        SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .build();
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = mapper.writeValueAsString(duration);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "[\"" + Duration.class.getName() + "\",13498.000008374]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .build();
        Duration duration = Duration.ofSeconds(13498L, 837481723);
        String value = mapper.writeValueAsString(duration);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "[\"" + Duration.class.getName() + "\",13498837]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .build();
        Duration duration = Duration.ofSeconds(13498L, 8374);
        String value = mapper.writeValueAsString(duration);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "[\"" + Duration.class.getName() + "\",\"" + duration.toString() + "\"]", value);
    }

    @Test
    public void testSerializationWithHMSFormat() throws Exception
    {
        Duration duration = Duration.ofSeconds(3676L, 0);
        final HMSWrapper hmsWrapper = new HMSWrapper(duration);
        String value = WRITER.writeValueAsString(hmsWrapper);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "{\"duration\":\"01:01:16\"}", value);
    }

    @Test
    public void testSerializationWithDaysFormat() throws Exception
    {
        Duration duration = Duration.ofDays(367L);
        final DaysDurationWrapper daysWrapper = new DaysDurationWrapper(duration);
        String value = WRITER.writeValueAsString(daysWrapper);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "{\"duration\":\"367 Day(s)\"}", value);
    }
}
