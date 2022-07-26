package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class OffsetDateTimeAsKeyTest extends ModuleTestBase
{
    private static final TypeReference<Map<OffsetDateTime, String>> TYPE_REF = new TypeReference<Map<OffsetDateTime, String>>() {
    };
    private static final OffsetDateTime DATE_TIME_0 = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneOffset.UTC);
    private static final String DATE_TIME_0_STRING = "1970-01-01T00:00Z";
    private static final OffsetDateTime DATE_TIME_1 = OffsetDateTime.of(2015, 3, 14, 9, 26, 53, 590 * 1000 * 1000, ZoneOffset.UTC);
    private static final String DATE_TIME_1_STRING = "2015-03-14T09:26:53.590Z";
    private static final OffsetDateTime DATE_TIME_2 = OffsetDateTime.of(2015, 3, 14, 9, 26, 53, 590 * 1000 * 1000, ZoneOffset.ofHours(6));
    private static final String DATE_TIME_2_STRING = "2015-03-14T09:26:53.590+06:00";

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization0() throws Exception {
        String value = MAPPER.writeValueAsString(asMap(DATE_TIME_0, "test"));
        Assert.assertEquals("Value is incorrect", mapAsString(DATE_TIME_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(DATE_TIME_1_STRING, "test"),
                MAPPER.writeValueAsString(asMap(DATE_TIME_1, "test")));
    }

    @Test
    public void testSerialization2() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(DATE_TIME_2_STRING, "test"),
                MAPPER.writeValueAsString(asMap(DATE_TIME_2, "test")));
    }

    @Test
    public void testDeserialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(DATE_TIME_0, "test"),
                READER.readValue(mapAsString(DATE_TIME_0_STRING, "test")));
    }

    @Test
    public void testDeserialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(DATE_TIME_1, "test"),
                READER.readValue(mapAsString(DATE_TIME_1_STRING, "test")));
    }

    @Test
    public void testDeserialization2() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(DATE_TIME_2, "test"),
                READER.readValue(mapAsString(DATE_TIME_2_STRING, "test")));
    }
}
