package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;

import static org.junit.jupiter.api.Assertions.*;

public class TestYearMonthSerializationWithCustomFormatter {

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testSerialization(DateTimeFormatter formatter) throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertTrue(serializeWith(dateTime, formatter).contains(dateTime.format(formatter)));
    }

    private String serializeWith(YearMonth dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(new YearMonthSerializer(f)));
        return mapper.writeValueAsString(dateTime);
    }

    @ParameterizedTest
    @MethodSource("customFormatters")
    void testDeserialization(DateTimeFormatter formatter) throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertEquals(dateTime, deserializeWith(dateTime.format(formatter), formatter));
    }

    private YearMonth deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(YearMonth.class, new YearMonthDeserializer(f)));
        return mapper.readValue("\"" + json + "\"", YearMonth.class);
    }

    static Stream<DateTimeFormatter> customFormatters() {
        return Stream.of(
                DateTimeFormatter.ofPattern("uuuu-MM"),
                DateTimeFormatter.ofPattern("uu-M")
        );
    }
}