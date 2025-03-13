package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

// [modules-java8#279] OffsetDateTimeDeserializer fails to parse date-time string with 'Z' at the end
public class OffsetDateTimeDeser279Test
    extends ModuleTestBase
{
    static class Wrapper279 {
        OffsetDateTime date;

        public Wrapper279(OffsetDateTime d) { date = d; }
        protected Wrapper279() { }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        public OffsetDateTime getDate() {
            return date;
        }
        public void setDate(OffsetDateTime date) {
            this.date = date;
        }
    }

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testWrapperWithPattern279()
        throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(ZoneId.of("UTC"))
                .withYear(2025).withMonth(3).withDayOfMonth(13).withHour(13).withMinute(29).withSecond(57).withNano(0)
                .truncatedTo(ChronoUnit.SECONDS);
        Wrapper279 input = new Wrapper279(date);
        
        // serialization first
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"date\":\"2025-03-13T13:29:57Z\"}", json);

        // deserialization second
        Wrapper279 result = MAPPER.readValue(json, Wrapper279.class);
        assertEquals(input.date, result.date);
    }
}
