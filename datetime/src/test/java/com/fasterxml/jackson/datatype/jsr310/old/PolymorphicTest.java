package com.fasterxml.jackson.datatype.jsr310.old;

import java.time.ZoneId;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PolymorphicTest extends ModuleTestBase
{
    private final ObjectMapper TYPING_MAPPER = newMapper();
    {
        TYPING_MAPPER.enableDefaultTyping();
    }

    // for [datatype-jsr310#24]
    @Test
    public void testZoneIdAsIs() throws Exception
    {
        ZoneId exp = ZoneId.of("America/Chicago");
        String json = TYPING_MAPPER.writeValueAsString(exp);
        assertEquals(exp, TYPING_MAPPER.readValue(json, ZoneId.class));
    }

    @Test
    public void testZoneWithForcedBaseType() throws Exception
    {
        ZoneId exp = ZoneId.of("America/Chicago");
        String json = TYPING_MAPPER.writerFor(ZoneId.class).writeValueAsString(exp);
        assertEquals(exp, TYPING_MAPPER.readValue(json, ZoneId.class));
    }
}
