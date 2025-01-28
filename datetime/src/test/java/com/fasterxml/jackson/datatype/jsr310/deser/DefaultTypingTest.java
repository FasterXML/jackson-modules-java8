package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultTypingTest extends ModuleTestBase
{
    private final ObjectMapper TYPING_MAPPER = newMapper();
    {
        TYPING_MAPPER.activateDefaultTyping(new NoCheckSubTypeValidator());
    }

    // for [datatype-jsr310#24]: will NOT add Type Id since actual type is concrete,
    // not abstract (although... how come deserializer does not except Type Id?)
    @Test
    public void testZoneIdAsIs() throws Exception
    {
        ZoneId exp = ZoneId.of("America/Chicago");
        String json = TYPING_MAPPER.writeValueAsString(exp);
        ZoneId act = TYPING_MAPPER.readValue(json, ZoneId.class);
        assertEquals(exp, act);
    }

    // This one WILL add type info, since `ZoneId` is abstract type:
    @Test
    public void testZoneWithForcedBaseType() throws Exception
    {
        ZoneId exp = ZoneId.of("America/Chicago");
        String json = TYPING_MAPPER.writerFor(ZoneId.class).writeValueAsString(exp);
        ZoneId act = TYPING_MAPPER.readValue(json, ZoneId.class);
        assertEquals(exp, act);
    }
}
