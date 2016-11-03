package com.fasterxml.jackson.datatype.jsr310.old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class TestDurationDeserialization extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(Duration.class);

    @Test
    public void testDeserializationAsFloat01() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60.0");

        assertNotNull("The value should not be null.", value);
        Duration exp = Duration.ofSeconds(60L, 0);
        assertEquals("The value is not correct.", exp,  value);
    }

    @Test
    public void testDeserializationAsFloat02() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60.0");

        assertNotNull("The value should not be null.", value);
        Duration exp = Duration.ofSeconds(60L, 0);
        assertEquals("The value is not correct.", exp, value);
    }

    @Test
    public void testDeserializationAsFloat03() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498.000008374");
        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 8374), value);
    }

    @Test
    public void testDeserializationAsFloat04() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498.000008374");
        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 8374), value);
    }

    @Test
    public void testDeserializationAsInt01() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60");
        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(60L, 0),  value);
    }

    @Test
    public void testDeserializationAsInt02() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60000");

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(60L, 0),  value);
    }

    @Test
    public void testDeserializationAsInt03() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498");

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 0),  value);
    }

    @Test
    public void testDeserializationAsInt04() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498000");

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 0),  value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        Duration exp = Duration.ofSeconds(60L, 0);
        Duration value = READER.readValue('"' + exp.toString() + '"');

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", exp,  value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        Duration exp = Duration.ofSeconds(13498L, 8374);
        Duration value = READER.readValue('"' + exp.toString() + '"');

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", exp,  value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        Duration value = READER.readValue("\"   \"");
        assertNull("The value should be null.", value);
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

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", duration, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498]", TemporalAmount.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L), value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498837]", TemporalAmount.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 837000000), value);
    }

    @Test
    public void testDeserializationWithTypeInfo04() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);

        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + '"' + duration.toString() + "\"]", TemporalAmount.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", duration, value);
    }
}
