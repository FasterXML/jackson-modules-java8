package tools.jackson.datatype.jsr310.deser.key;

import org.junit.Test;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import java.time.ZonedDateTime;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.*;

// for [modules-java8#306]
public class ZonedDateTimeKeyDeserializerTest
    extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final TypeReference<Map<ZonedDateTime, String>> MAP_TYPE_REF
        = new TypeReference<Map<ZonedDateTime, String>>() {};

    @Test
    public void Instant_style_can_be_deserialized() throws Exception {
        Map<ZonedDateTime, String> map = MAPPER.readValue(getMap("2015-07-24T12:23:34.184Z"),
                MAP_TYPE_REF);
        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T12:23:34.184Z", entry.getKey().toString());
    }

    @Test
    public void ZonedDateTime_with_zone_name_can_be_deserialized() throws Exception {
        Map<ZonedDateTime, String> map = MAPPER.readValue(getMap("2015-07-24T12:23:34.184Z[UTC]"),
                MAP_TYPE_REF);
        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T12:23:34.184Z[UTC]", entry.getKey().toString());
    }

    // 29-Oct-2024, tatu: Following two tests are for Java 8+ only vs Java 9 or later
    
    @Test
    public void ZonedDateTime_with_place_name_can_be_deserialized() throws Exception {
        assumeFalse(System.getProperty("java.version").startsWith("1.8"));

        Map<ZonedDateTime, String> map = MAPPER.readValue(getMap("2015-07-24T12:23:34.184Z[Europe/London]"),
                MAP_TYPE_REF);
        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T13:23:34.184+01:00[Europe/London]", entry.getKey().toString());
    }

    @Test
    public void ZonedDateTime_with_place_name_can_be_deserialized_Java_8() throws Exception {
        // Java 8 parses this format incorrectly due to https://bugs.openjdk.org/browse/JDK-8066982
        assumeTrue(System.getProperty("java.version").startsWith("1.8"));

        Map<ZonedDateTime, String> map = MAPPER.readValue(getMap("2015-07-24T12:23:34.184Z[Europe/London]"),
                MAP_TYPE_REF);
        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T12:23:34.184+01:00[Europe/London]", entry.getKey().toString());
    }

    @Test
    public void ZonedDateTime_with_offset_can_be_deserialized() throws Exception {
        Map<ZonedDateTime, String> map = MAPPER.readValue(getMap("2015-07-24T12:23:34.184+02:00"),
                MAP_TYPE_REF);
        Map.Entry<ZonedDateTime, String> entry = map.entrySet().iterator().next();
        assertEquals("2015-07-24T12:23:34.184+02:00", entry.getKey().toString());
    }

    private static String getMap(String input) {
        return "{\"" + input + "\": \"This is a string\"}";
    }
}
