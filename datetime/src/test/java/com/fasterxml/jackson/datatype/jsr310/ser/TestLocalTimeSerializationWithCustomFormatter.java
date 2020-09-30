package com.fasterxml.jackson.datatype.jsr310.ser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestLocalTimeSerializationWithCustomFormatter {
    private final DateTimeFormatter formatter;

    public TestLocalTimeSerializationWithCustomFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Test
    public void testSerialization() throws Exception {
        LocalTime dateTime = LocalTime.now();
        assertThat(serializeWith(dateTime, formatter), containsString(dateTime.format(formatter)));
    }

    private String serializeWith(LocalTime dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new LocalTimeSerializer(f)))
                .build();
        return mapper.writeValueAsString(dateTime);
    }

    @Test
    public void testDeserialization() throws Exception {
        LocalTime dateTime = LocalTime.now();
        assertThat(deserializeWith(dateTime.format(formatter), formatter), equalTo(dateTime));
    }

    private LocalTime deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(LocalTime.class, new LocalTimeDeserializer(f)))
                .build();
        return mapper.readValue("\"" + json + "\"", LocalTime.class);
    }

    @Parameters
    public static Collection<Object[]> customFormatters() {
        Collection<Object[]> formatters = new ArrayList<>();
        formatters.add(new Object[]{DateTimeFormatter.ISO_LOCAL_TIME});
        formatters.add(new Object[]{DateTimeFormatter.ISO_TIME});
        return formatters;
    }
}
