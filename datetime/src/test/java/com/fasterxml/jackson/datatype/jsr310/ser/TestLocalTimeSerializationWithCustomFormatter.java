package com.fasterxml.jackson.datatype.jsr310.ser;


import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalTimeSerializationWithCustomFormatter {

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        LocalTime dateTime = LocalTime.now();
        assertTrue(serializeWith(dateTime, formatter).contains(dateTime.format(formatter)));
    }

    private String serializeWith(LocalTime dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(new LocalTimeSerializer(f)));
        return mapper.writeValueAsString(dateTime);
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        LocalTime dateTime = LocalTime.now();
        assertEquals(dateTime, deserializeWith(dateTime.format(formatter), formatter));
    }

    private LocalTime deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(LocalTime.class, new LocalTimeDeserializer(f)));
        return mapper.readValue("\"" + json + "\"", LocalTime.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
                DateTimeFormatter.ISO_LOCAL_TIME,
                DateTimeFormatter.ISO_TIME
        );
    }
}