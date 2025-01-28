package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalDateSerializationWithCustomFormatter {

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        LocalDate date = LocalDate.now();
        assertTrue(serializeWith(date, formatter).contains(date.format(formatter)),
            "Serialized value should contain the formatted date");
    }

    private String serializeWith(LocalDate date, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(new LocalDateSerializer(f)));
        return mapper.writeValueAsString(date);
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        LocalDate date = LocalDate.now();
        assertEquals(date, deserializeWith(date.format(formatter), formatter),
            "Deserialized value should match the original date");
    }

    private LocalDate deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(LocalDate.class, new LocalDateDeserializer(f)));
        return mapper.readValue("\"" + json + "\"", LocalDate.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
            DateTimeFormatter.BASIC_ISO_DATE,
            DateTimeFormatter.ISO_DATE,
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_ORDINAL_DATE,
            DateTimeFormatter.ISO_WEEK_DATE,
            DateTimeFormatter.ofPattern("MM/dd/yyyy")
        );
    }
}