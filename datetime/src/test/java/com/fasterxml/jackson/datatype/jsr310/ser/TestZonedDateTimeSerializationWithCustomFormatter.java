package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestZonedDateTimeSerializationWithCustomFormatter {

    @MethodSource("customFormatters")
    @ParameterizedTest
    public void testSerialization(DateTimeFormatter formatter) throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        assertTrue(serializeWith(zonedDateTime, formatter).contains(zonedDateTime.format(formatter.withZone(ZoneOffset.UTC))));
    }

    private String serializeWith(ZonedDateTime zonedDateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule().addSerializer(
                        new ZonedDateTimeSerializer(f)))
                .defaultTimeZone(TimeZone.getTimeZone("UTC"))
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        return mapper.writeValueAsString(zonedDateTime);
    }

    public static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        );
    }
}
