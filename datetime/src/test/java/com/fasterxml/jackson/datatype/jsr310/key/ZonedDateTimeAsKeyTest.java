package com.fasterxml.jackson.datatype.jsr310.key;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ZonedDateTimeAsKeyTest extends ModuleTestBase
{
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

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization0() throws Exception {
        String value = MAPPER.writerFor(TYPE_REF).writeValueAsString(asMap(DATE_TIME_0, "test"));
        Assert.assertEquals("Value is incorrect", mapAsString(DATE_TIME_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        String value = MAPPER.writerFor(TYPE_REF).writeValueAsString(asMap(DATE_TIME_1, "test"));
        Assert.assertEquals("Value is incorrect", mapAsString(DATE_TIME_1_STRING, "test"), value);
    }

    @Test
    public void testSerialization2() throws Exception {
        String value = MAPPER.writerFor(TYPE_REF).writeValueAsString(asMap(DATE_TIME_2, "test"));
        Assert.assertEquals("Value is incorrect", mapAsString(DATE_TIME_2_STRING, "test"), value);
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
        Assert.assertEquals("Value is incorrect", asMap(DATE_TIME_2_OFFSET, "test"),
                READER.readValue(mapAsString(DATE_TIME_2_STRING, "test")));
    }

    @Test
    public void testSerializationToInstantWithNanos() throws Exception {
        String value = mapperBuilder().enable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS).build()
            .writerFor(TYPE_REF).writeValueAsString(asMap(DATE_TIME_1, "test"));
        Assert.assertEquals("Value is incorrect",
            mapAsString(String.valueOf(DATE_TIME_1.toEpochSecond()) + '.' + DATE_TIME_1.getNano(), "test"), value);
    }

    @Test
    public void testSerializationToInstantWithoutNanos() throws Exception {
        String value = mapperBuilder().enable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS).build()
            .writerFor(TYPE_REF).writeValueAsString(asMap(DATE_TIME_1, "test"));
        Assert.assertEquals("Value is incorrect",
            mapAsString(String.valueOf(DATE_TIME_1.toInstant().toEpochMilli()), "test"), value);
    }
}
