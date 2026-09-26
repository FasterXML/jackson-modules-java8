package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

// [modules-java8#389]
public class JSR310EmbeddedObjectDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testZoneIdRejectsEmbeddedBytes() {
        ObjectNode node = MAPPER.createObjectNode();
        node.putPOJO("zone", new byte[] { (byte) 0xDE, (byte) 0xAD });

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                MAPPER.convertValue(node, new TypeReference<Map<String, ZoneId>>() { }));
        assertInstanceOf(MismatchedInputException.class, e.getCause());
    }

    @Test
    public void testZoneIdAcceptsEmbeddedZoneId() {
        ObjectNode node = MAPPER.createObjectNode();
        node.putPOJO("zone", ZoneId.of("UTC"));

        Map<String, ZoneId> result = MAPPER.convertValue(node,
                new TypeReference<Map<String, ZoneId>>() { });
        assertEquals(ZoneId.of("UTC"), result.get("zone"));
    }

    @Test
    public void testPeriodRejectsEmbeddedBytes() {
        ObjectNode node = MAPPER.createObjectNode();
        node.putPOJO("period", new byte[] { 1, 2 });

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                MAPPER.convertValue(node, new TypeReference<Map<String, Period>>() { }));
        assertInstanceOf(MismatchedInputException.class, e.getCause());
    }

    @Test
    public void testZoneOffsetRejectsEmbeddedBytes() {
        ObjectNode node = MAPPER.createObjectNode();
        node.putPOJO("offset", new byte[] { 1, 2 });

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () ->
                MAPPER.convertValue(node, new TypeReference<Map<String, ZoneOffset>>() { }));
        assertInstanceOf(MismatchedInputException.class, e.getCause());
    }
}
