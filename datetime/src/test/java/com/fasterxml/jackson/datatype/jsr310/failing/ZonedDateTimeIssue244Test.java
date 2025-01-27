package com.fasterxml.jackson.datatype.jsr310.failing;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test case for https://github.com/FasterXML/jackson-modules-java8/issues/244
 */
public class ZonedDateTimeIssue244Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    public void zoneIdUTC() throws Exception
    {
        assertSerializeAndDeserialize(ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Test
    public void zoneOffsetUTC() throws Exception
    {
        assertSerializeAndDeserialize(ZonedDateTime.now(ZoneOffset.UTC)); // fails!
    }

    @Test
    public void zoneOffsetNonUTC() throws Exception
    {
        assertSerializeAndDeserialize(ZonedDateTime.now(ZoneOffset.ofHours(-7))); // fails!
    }

    private void assertSerializeAndDeserialize(final ZonedDateTime date) throws Exception
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
