package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimeZone;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(Parameterized.class)
public class TestZonedDateTimeSerializationWithCustomFormatter {
    private final DateTimeFormatter formatter;

    public TestZonedDateTimeSerializationWithCustomFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Test
    public void testSerialization() throws Exception {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        assertThat(serializeWith(zonedDateTime, formatter),
                containsString(zonedDateTime.format(formatter.withZone(ZoneOffset.UTC))));
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

    @Parameters
    public static Collection<Object[]> customFormatters() {
        Collection<Object[]> formatters = new ArrayList<>();
        formatters.add(new Object[]{DateTimeFormatter.ISO_ZONED_DATE_TIME});
        formatters.add(new Object[]{DateTimeFormatter.ISO_OFFSET_DATE_TIME});
        formatters.add(new Object[]{DateTimeFormatter.ISO_LOCAL_DATE_TIME});
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")});
        return formatters;
    }
}
