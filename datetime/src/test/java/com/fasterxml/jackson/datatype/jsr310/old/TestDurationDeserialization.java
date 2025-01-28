package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import static org.junit.jupiter.api.Assertions.*;

public class TestDurationDeserialization extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(Duration.class);

    @Test
    public void testDeserializationAsFloat01() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60.0");

        assertNotNull(value);
        Duration exp = Duration.ofSeconds(60L, 0);
        assertEquals(exp,  value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsFloat02() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60.0");

        assertNotNull(value);
        Duration exp = Duration.ofSeconds(60L, 0);
        assertEquals(exp, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsFloat03() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498.000008374");
        assertNotNull(value);
        assertEquals(Duration.ofSeconds(13498L, 8374), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsFloat04() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498.000008374");
        assertNotNull(value);
        assertEquals(Duration.ofSeconds(13498L, 8374), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsInt01() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60");
        assertNotNull(value);
        assertEquals(Duration.ofSeconds(60L, 0), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsInt02() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60000");

        assertNotNull(value);
        assertEquals(Duration.ofSeconds(60L, 0), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsInt03() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498");

        assertNotNull(value);
        assertEquals(Duration.ofSeconds(13498L, 0), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsInt04() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498000");

        assertNotNull(value);
        assertEquals(Duration.ofSeconds(13498L, 0), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        Duration exp = Duration.ofSeconds(60L, 0);
        Duration value = READER.readValue('"' + exp.toString() + '"');

        assertNotNull(value);
        assertEquals(exp, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        Duration exp = Duration.ofSeconds(13498L, 8374);
        Duration value = READER.readValue('"' + exp.toString() + '"');

        assertNotNull(value);
        assertEquals(exp, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        Duration value = READER.readValue("\"   \"");
        assertNull(value, "The value should be null.");
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);

        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498.000008374]", TemporalAmount.class);

        assertNotNull(value);
        assertInstanceOf(Duration.class, value, "The value should be a Duration.");
        assertEquals(duration, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498]", TemporalAmount.class);

        assertNotNull(value);
        assertInstanceOf(Duration.class, value, "The value should be a Duration.");
        assertEquals(Duration.ofSeconds(13498L), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498837]", TemporalAmount.class);

        assertNotNull(value);
        assertInstanceOf(Duration.class, value, "The value should be a Duration.");
        assertEquals(Duration.ofSeconds(13498L, 837000000), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo04() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);

        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + '"' + duration.toString() + "\"]", TemporalAmount.class);

        assertNotNull(value);
        assertInstanceOf(Duration.class, value, "The value should be a Duration.");
        assertEquals(duration, value, "The value is not correct.");
    }
}
