package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class InstantDeserTest extends ModuleTestBase {
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(Instant.class);

    @Test
    public void testReadDateTimestampsAsNanosecondsEnabled() throws Throwable {
        long seconds = 946684800;
        long nanos = 900_000_000;
        Instant value = READER
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(String.valueOf(seconds) + String.valueOf(nanos));
        expect(Instant.ofEpochSecond(seconds, nanos), value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp, value);
    }
}
