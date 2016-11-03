package com.fasterxml.jackson.datatype.jsr310;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import static org.hamcrest.core.StringContains.containsString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestLocalDateSerializationWithCustomFormatter {
    private final DateTimeFormatter formatter;

    public TestLocalDateSerializationWithCustomFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Test
    public void testSerialization() throws Exception {
        LocalDate date = LocalDate.now();
        assertThat(serializeWith(date, formatter), containsString(date.format(formatter)));
    }

    private String serializeWith(LocalDate date, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addSerializer(new LocalDateSerializer(f)));
        return mapper.writeValueAsString(date);
    }

    @Test
    public void testDeserialization() throws Exception {
        LocalDate date = LocalDate.now();
        assertThat(deserializeWith(date.format(formatter), formatter), equalTo(date));
    }

    private LocalDate deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = new ObjectMapper().registerModule(new SimpleModule()
            .addDeserializer(LocalDate.class, new LocalDateDeserializer(f)));
        return mapper.readValue("\"" + json + "\"", LocalDate.class);
    }

    @Parameters
    public static Collection<Object[]> customFormatters() {
        Collection<Object[]> formatters = new ArrayList<>();
        formatters.add(new Object[]{DateTimeFormatter.BASIC_ISO_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_LOCAL_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_ORDINAL_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_WEEK_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("MM/dd/yyyy")});
        return formatters;
    }
}
