package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class WriteZoneIdTest extends ModuleTestBase
{
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
    public void testJacksonAnnotatedPOJOWithDateWithTimezoneToJson() throws Exception
    {
        ObjectMapper mapper = newMapper();
        String ZONE_ID_STR = "Asia/Krasnoyarsk";
        final ZoneId ZONE_ID = ZoneId.of(ZONE_ID_STR);

        DummyClassWithDate input = new DummyClassWithDate(ZonedDateTime.ofInstant(Instant.ofEpochSecond(0L), ZONE_ID));

        // 30-Jun-2016, tatu: Exact time seems to vary a bit based on DST, so let's actually
        //    just verify appending of timezone id itself:
        String json = mapper.writeValueAsString(input);
        if (!json.contains("\"1970-01-01T")) {
            Assert.fail("Should contain time prefix, did not: "+json);
        }
        String match = String.format("[%s]", ZONE_ID_STR);
        if (!json.contains(match)) {
            Assert.fail("Should contain zone id "+match+", does not: "+json);
        }
    }
}
