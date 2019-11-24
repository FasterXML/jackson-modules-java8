package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.ZoneId;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

public class DefaultTypingTest extends ModuleTestBase
{
    static class NoCheckSubTypeValidator
        extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    private final ObjectMapper TYPING_MAPPER = newMapperBuilder()
            .activateDefaultTyping(new NoCheckSubTypeValidator())
            .build();

    // for [datatype-jsr310#24]
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
