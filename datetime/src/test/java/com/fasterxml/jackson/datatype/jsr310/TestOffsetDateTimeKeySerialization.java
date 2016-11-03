package com.fasterxml.jackson.datatype.jsr310;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestOffsetDateTimeKeySerialization {

    private static final TypeReference<Map<OffsetDateTime, String>> TYPE_REF = new TypeReference<Map<OffsetDateTime, String>>() {
    };
    private static final OffsetDateTime DATE_TIME_0 = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneOffset.UTC);
    private static final String DATE_TIME_0_STRING = "1970-01-01T00:00Z";
    private static final OffsetDateTime DATE_TIME_1 = OffsetDateTime.of(2015, 3, 14, 9, 26, 53, 590 * 1000 * 1000, ZoneOffset.UTC);
    private static final String DATE_TIME_1_STRING = "2015-03-14T09:26:53.590Z";
    private static final OffsetDateTime DATE_TIME_2 = OffsetDateTime.of(2015, 3, 14, 9, 26, 53, 590 * 1000 * 1000, ZoneOffset.ofHours(6));
    private static final String DATE_TIME_2_STRING = "2015-03-14T09:26:53.590+06:00";

    private ObjectMapper om;
    private Map<OffsetDateTime, String> map;

    @Before
    public void setUp() {
        this.om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        map = new HashMap<>();
    }

    /*
     * ObjectMapper configuration is not respected at deserialization and serialization at the moment.
     */

    @Test
    public void testSerialization0() throws Exception {
        map.put(DATE_TIME_0, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(DATE_TIME_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(DATE_TIME_1, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(DATE_TIME_1_STRING, "test"), value);
    }

    @Test
    public void testSerialization2() throws Exception {
        map.put(DATE_TIME_2, "test");

        String value = om.writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(DATE_TIME_2_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<OffsetDateTime, String> value = om.readValue(map(DATE_TIME_0_STRING, "test"), TYPE_REF);

        map.put(DATE_TIME_0, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<OffsetDateTime, String> value = om.readValue(map(DATE_TIME_1_STRING, "test"), TYPE_REF);

        map.put(DATE_TIME_1, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization2() throws Exception {
        Map<OffsetDateTime, String> value = om.readValue(map(DATE_TIME_2_STRING, "test"), TYPE_REF);

        map.put(DATE_TIME_2, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
