package com.fasterxml.jackson.datatype.jsr310.deser.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ZonedDateTimeKeyDeserializerTest {

    private static ObjectMapper objectMapper;
    private final TypeReference<Map<ZonedDateTime, String>> MAP_TYPE_REF = new TypeReference<Map<ZonedDateTime, String>>() {
		};

    @BeforeClass
    public static void beforeClass() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void Instant_style_can_be_deserialized() throws JsonProcessingException {
        String input = "2015-07-24T12:23:34.184Z";

        Map<ZonedDateTime, String> map = objectMapper.readValue(getMap(input), MAP_TYPE_REF);

        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T12:23:34.184Z", entry.getKey().toString());
    }

    @Test
    public void ZonedDateTime_with_zone_name_can_be_deserialized() throws JsonProcessingException {
        String input = "2015-07-24T12:23:34.184Z[UTC]";

        Map<ZonedDateTime, String> map = objectMapper.readValue(getMap(input), MAP_TYPE_REF);

        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T12:23:34.184Z[UTC]", entry.getKey().toString());
    }

    @Test
    public void ZonedDateTime_with_place_name_can_be_deserialized() throws JsonProcessingException {
        String input = "2015-07-24T12:23:34.184Z[Europe/London]";

        Map<ZonedDateTime, String> map = objectMapper.readValue(getMap(input), MAP_TYPE_REF);

        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T13:23:34.184+01:00[Europe/London]", entry.getKey().toString());
    }

    @Test
    public void ZonedDateTime_with_offset_can_be_deserialized() throws JsonProcessingException {
        String input = "2015-07-24T12:23:34.184+02:00";

        Map<ZonedDateTime, String> map = objectMapper.readValue(getMap(input), MAP_TYPE_REF);

        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T12:23:34.184+02:00", entry.getKey().toString());
    }

    private static String getMap(String input) {
        return "{\"" + input + "\": \"This is a string\"}";
    }
}
