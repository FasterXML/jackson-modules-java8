package com.fasterxml.jackson.datatype.jsr310.deser;

import static org.junit.Assert.assertEquals;

import java.time.ZoneId;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

public class ZoneIdDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testDeserialization01() throws Exception
    {
        ZoneId value = MAPPER.readValue("\"America/Chicago\"", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Chicago"), value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        ZoneId value = MAPPER.readValue("\"America/Anchorage\"", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Anchorage"), value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        ObjectMapper mapper = ObjectMapper.builder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .addModule(new JavaTimeModule())
                .build();
        ZoneId value = mapper.readValue("[\"" + ZoneId.class.getName() + "\",\"America/Denver\"]", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Denver"), value);
    }
}
