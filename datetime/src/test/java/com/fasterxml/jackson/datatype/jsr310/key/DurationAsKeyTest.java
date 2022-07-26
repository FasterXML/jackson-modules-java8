package com.fasterxml.jackson.datatype.jsr310.key;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

public class DurationAsKeyTest extends ModuleTestBase
{
    private static final Duration DURATION = Duration.ofMinutes(13).plusSeconds(37).plusNanos(120 * 1000 * 1000L);
    private static final String DURATION_STRING = "PT13M37.12S";

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(new TypeReference<Map<Duration, String>>() { });

    @Test
    public void testSerialization() throws Exception {
        assertEquals("Value is not correct", mapAsString(DURATION_STRING, "test"),
                MAPPER.writeValueAsString(Collections.singletonMap(DURATION, "test")));
    }

    @Test
    public void testDeserialization() throws Exception {
        assertEquals("Value is not correct", Collections.singletonMap(DURATION, "test"),
                READER.readValue(mapAsString(DURATION_STRING, "test")));
    }
}
