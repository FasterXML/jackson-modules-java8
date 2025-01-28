package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationDeser337Test extends ModuleTestBase
{
    @Test
    public void test_default() throws Exception {
        ObjectMapper mapper = newMapper();

        Duration duration = Duration.parse("PT-43.636S");

        String ser = mapper.writeValueAsString(duration);

        assertEquals("-43.636000000", ser);

        Duration deser = mapper.readValue(ser, Duration.class);

        assertEquals(duration, deser);
        assertEquals(deser.toString(), "PT-43.636S");
    }    

    // Handling with WRITE_DURATIONS_AS_TIMESTAMPS enabled can't round-trip a value
    @Test
    public void test_with() throws Exception {
        ObjectMapper mapper = mapperBuilder()
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, true)
                .build();

        Duration duration = Duration.parse("PT-43.636S");

        String ser = mapper.writeValueAsString(duration);

        assertEquals("-43.636000000", ser);

        Duration deser = mapper.readValue(ser, Duration.class);

        assertEquals(duration, deser);
        assertEquals(deser.toString(), "PT-43.636S");
    }

    // Handling with WRITE_DURATIONS_AS_TIMESTAMPS disabled works
    @Test
    public void test_without() throws Exception {
        ObjectMapper mapper = mapperBuilder()
                .configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
                .build();

        Duration duration = Duration.parse("PT-43.636S");

        String ser = mapper.writeValueAsString(duration);
        assertEquals(q("PT-43.636S"), ser);

        Duration deser = mapper.readValue(ser, Duration.class);
        assertEquals(duration, deser);
    }
}
