package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestZonedDateTimeKeySerialization extends ModuleTestBase {

    private static final TypeReference<Map<ZonedDateTime, String>> TYPE_REF = new TypeReference<Map<ZonedDateTime, String>>() {
    };
    private static final ZonedDateTime DATE_TIME_0 = ZonedDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneOffset.UTC);
    private static final String DATE_TIME_0_STRING = "1970-01-01T00:00:00Z";

    private static final ZonedDateTime DATE_TIME_1 = ZonedDateTime.of(
            2015, 3, 14, 9, 26, 53, 590 * 1000 * 1000, ZoneOffset.UTC);
    private static final String DATE_TIME_1_STRING = "2015-03-14T09:26:53.59Z";

    private static final ZonedDateTime DATE_TIME_2 = ZonedDateTime.of(
            2015, 3, 14, 9, 26, 53, 590 * 1000 * 1000, ZoneId.of("Europe/Budapest"));
    /**
     * Value of {@link #DATE_TIME_2} after it's been serialized and read back. Serialization throws away time zone information, it only
     * keeps offset data.
     */
    private static final ZonedDateTime DATE_TIME_2_OFFSET = DATE_TIME_2.withZoneSameInstant(ZoneOffset.ofHours(1));
    private static final String DATE_TIME_2_STRING = "2015-03-14T09:26:53.59+01:00";;

    private ObjectMapper om;
    private Map<ZonedDateTime, String> map;

    @Before
    public void setUp() {
        this.om = newMapper();
        map = new HashMap<>();
    }

    /*
     * ObjectMapper configuration is not respected at deserialization and serialization at the moment.
     */

    @Test
    public void testSerialization0() throws Exception {
        map.put(DATE_TIME_0, "test");

        String value = om.writerFor(TYPE_REF).writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(DATE_TIME_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        map.put(DATE_TIME_1, "test");

        String value = om.writerFor(TYPE_REF).writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(DATE_TIME_1_STRING, "test"), value);
    }

    @Test
    public void testSerialization2() throws Exception {
        map.put(DATE_TIME_2, "test");

        String value = om.writerFor(TYPE_REF).writeValueAsString(map);

        Assert.assertEquals("Value is incorrect", map(DATE_TIME_2_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<ZonedDateTime, String> value = om.readValue(map(DATE_TIME_0_STRING, "test"), TYPE_REF);

        map.put(DATE_TIME_0, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<ZonedDateTime, String> value = om.readValue(map(DATE_TIME_1_STRING, "test"), TYPE_REF);

        map.put(DATE_TIME_1, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization2() throws Exception {
        Map<ZonedDateTime, String> value = om.readValue(map(DATE_TIME_2_STRING, "test"), TYPE_REF);

        map.put(DATE_TIME_2_OFFSET, "test");
        Assert.assertEquals("Value is incorrect", map, value);
    }

    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }

}
