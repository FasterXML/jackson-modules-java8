package com.fasterxml.jackson.datatype.jsr310.failing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;

/**
 * Test case for https://github.com/FasterXML/jackson-modules-java8/issues/244
 */
public class ZonedDateTimeIssue244Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    public void zoneIdUTC() throws JsonProcessingException
    {
        assertSerializeAndSerialize(ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Test
    public void zoneOffsetUTC() throws JsonProcessingException
    {
        assertSerializeAndSerialize(ZonedDateTime.now(ZoneOffset.UTC)); // fails!
    }

    @Test
    public void zoneOffsetNonUTC() throws JsonProcessingException
    {
        assertSerializeAndSerialize(ZonedDateTime.now(ZoneOffset.ofHours(-7))); // fails!
    }

    private void assertSerializeAndSerialize(final ZonedDateTime date) throws JsonProcessingException
    {
        final Example example1 = new Example(date);
        final String json = MAPPER.writeValueAsString(example1);
        final Example example2 = MAPPER.readValue(json, Example.class);

        assertEquals(example1.getDate(), example2.getDate());
    }

    static class Example
    {
        private ZonedDateTime date;

        public Example()
        {
        }

        public Example(final ZonedDateTime date)
        {
            this.date = date;
        }

        public ZonedDateTime getDate()
        {
            return date;
        }
    }
}
