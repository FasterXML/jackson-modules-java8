package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherassertThat;
import static org.hamcrest.CoreMatchers.containsString;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WriteNanosecondsTest extends ModuleTestBase {
    public static final ZoneId UTC = ZoneId.of("UTC");
    private static ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerializeDurationWithAndWithoutNanoseconds() throws Exception {
        DummyClass<Duration> value = new DummyClass<>(Duration.ZERO);

        String json = MAPPER.writeValueAsString(value);

        assertThat(json, containsString("\"nanoseconds\":0.0"));
        assertThat(json, containsString("\"notNanoseconds\":0"));
    }

    @Test
    public void testSerializeInstantWithAndWithoutNanoseconds() throws Exception {
        DummyClass<Instant> input = new DummyClass<>(Instant.EPOCH);

        String json = MAPPER.writeValueAsString(input);

        assertThat(json, containsString("\"nanoseconds\":0.0"));
        assertThat(json, containsString("\"notNanoseconds\":0"));
    }

    @Test
    public void testSerializeLocalDateTimeWithAndWithoutNanoseconds() throws Exception {
        DummyClass<LocalDateTime> input = new DummyClass<>(
                // Nanos will only be written if it's non-zero
                LocalDateTime.of(1970, 1, 1, 0, 0, 0, 1)
        );

        String json = MAPPER.writeValueAsString(input);

        assertThat(json, containsString("\"nanoseconds\":[1970,1,1,0,0,0,1]"));
        assertThat(json, containsString("\"notNanoseconds\":[1970,1,1,0,0,0,0]"));
    }

    @Test
    public void testSerializeLocalTimeWithAndWithoutNanoseconds() throws Exception {
        DummyClass<LocalTime> input = new DummyClass<>(
                // Nanos will only be written if it's non-zero
                LocalTime.of(0, 0, 0, 1)
        );

        String json = MAPPER.writeValueAsString(input);

        assertThat(json, containsString("\"nanoseconds\":[0,0,0,1]"));
        assertThat(json, containsString("\"notNanoseconds\":[0,0,0,0]"));
    }

    @Test
    public void testSerializeOffsetDateTimeWithAndWithoutNanoseconds() throws Exception {
        DummyClass<OffsetDateTime> input = new DummyClass<>(OffsetDateTime.ofInstant(Instant.EPOCH, UTC));

        String json = MAPPER.writeValueAsString(input);

        assertThat(json, containsString("\"nanoseconds\":0.0"));
        assertThat(json, containsString("\"notNanoseconds\":0"));
    }

    @Test
    public void testSerializeOffsetTimeWithAndWithoutNanoseconds() throws Exception {
        DummyClass<OffsetTime> input = new DummyClass<>(
                // Nanos will only be written if it's non-zero
                OffsetTime.of(0,0,0, 1 , ZoneOffset.UTC)
        );

        String json = MAPPER.writeValueAsString(input);

        assertThat(json, containsString("\"nanoseconds\":[0,0,0,1,\"Z\"]"));
        assertThat(json, containsString("\"notNanoseconds\":[0,0,0,0,\"Z\"]"));
    }

    @Test
    public void testSerializeZonedDateTimeWithAndWithoutNanoseconds() throws Exception {
        DummyClass<ZonedDateTime> input = new DummyClass<>(ZonedDateTime.ofInstant(Instant.EPOCH, UTC));

        String json = MAPPER.writeValueAsString(input);

        assertThat(json, containsString("\"nanoseconds\":0.0"));
        assertThat(json, containsString("\"notNanoseconds\":0"));
    }

    private static class DummyClass<T> {
        @JsonFormat(with = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        private final T nanoseconds;

        @JsonFormat(without = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
        private final T notNanoseconds;

        DummyClass(T t) {
            this.nanoseconds = t;
            this.notNanoseconds = t;
        }
    }
}
