package com.fasterxml.jackson.datatype.jsr310.ser;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class WriteZoneIdTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    static class DummyClassWithDate {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "dd-MM-yyyy hh:mm:ss Z",
                with = JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID)
        public ZonedDateTime date;

        DummyClassWithDate() { }

        public DummyClassWithDate(ZonedDateTime date) {
            this.date = date;
        }
    }

    @Test
    public void testSerialization01() throws Exception
    {
        ZoneId id = ZoneId.of("America/Chicago");
        String value = MAPPER.writeValueAsString(id);
        assertEquals("The value is not correct.", "\"America/Chicago\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        ZoneId id = ZoneId.of("America/Anchorage");
        String value = MAPPER.writeValueAsString(id);
        assertEquals("The value is not correct.", "\"America/Anchorage\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ZoneId id = ZoneId.of("America/Denver");
        ObjectMapper mapper = JsonMapper.builder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .addModule(new JavaTimeModule())
                .build();
        String value = mapper.writeValueAsString(id);
        assertEquals("The value is not correct.", "[\"java.time.ZoneRegion\",\"America/Denver\"]", value);
    }

    @Test
    public void testJacksonAnnotatedPOJOWithDateWithTimezoneToJson() throws Exception
    {
        String ZONE_ID_STR = "Asia/Krasnoyarsk";
        final ZoneId ZONE_ID = ZoneId.of(ZONE_ID_STR);

        DummyClassWithDate input = new DummyClassWithDate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), ZONE_ID));

        // 30-Jun-2016, tatu: Exact time seems to vary a bit based on DST, so let's actually
        //    just verify appending of timezone id itself:
        String json = MAPPER.writeValueAsString(input);
        if (!json.contains("\"1970-01-01T")) {
            Assert.fail("Should contain time prefix, did not: "+json);
        }
        String match = String.format("[%s]", ZONE_ID_STR);
        if (!json.contains(match)) {
            Assert.fail("Should contain zone id "+match+", does not: "+json);
        }
    }

    @Test
    public void testMapSerialization() throws Exception {
        final ZonedDateTime datetime = ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Warsaw]");
        final HashMap<ZonedDateTime, String> map = new HashMap<>();
        map.put(datetime, "");

        ObjectMapper mapper = newMapperBuilder()
            .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID).build();
        String json = mapper.writeValueAsString(map);
        Assert.assertEquals("{\"2007-12-03T10:15:30+01:00[Europe/Warsaw]\":\"\"}", json);
    }
}
