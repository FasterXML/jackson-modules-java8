package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalDateTimeSerializationWithCustomFormatter {

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        assertTrue(serializeWith(dateTime, formatter).contains(dateTime.format(formatter)));
    }

    private String serializeWith(LocalDateTime dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule().addSerializer(new LocalDateTimeSerializer(f)));
        return mapper.writeValueAsString(dateTime);
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        LocalDateTime dateTime = LocalDateTime.now();
        assertEquals(dateTime, deserializeWith(dateTime.format(formatter), formatter));
    }

    private LocalDateTime deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule().addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(f)));
        return mapper.readValue("\"" + json + "\"", LocalDateTime.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
                DateTimeFormatter.ISO_DATE_TIME,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );
    }
}
