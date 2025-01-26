package com.fasterxml.jackson.datatype.jsr310.ser;


import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer;

import static org.junit.jupiter.api.Assertions.*;

public class TestYearSerializationWithCustomFormatter {

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        Year year = Year.now();
        String expected = "\"" + year.format(formatter) + "\"";
        assertEquals(expected, serializeWith(year, formatter));
    }

    private String serializeWith(Year dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(new YearSerializer(f)));
        return mapper.writeValueAsString(dateTime);
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        Year year = Year.now();
        assertEquals(year, deserializeWith(year.format(formatter), formatter));
    }

    private Year deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(Year.class, new YearDeserializer(f)));
        return mapper.readValue("\"" + json + "\"", Year.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
                DateTimeFormatter.ofPattern("yyyy"),
                DateTimeFormatter.ofPattern("yy")
        );
    }
}
