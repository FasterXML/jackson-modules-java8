package com.fasterxml.jackson.datatype.jsr310.failing;

import java.time.Instant;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.Assert.assertEquals;

// [modules-java8#307]: Loss of precision via JsonNode for BigDecimal-valued
// things (like Instant)
public class InstanceViaBigDecimal307Test extends ModuleTestBase
{
    static class Wrapper307 {
        public Instant value;

        public Wrapper307(Instant v) { value = v; }
        protected Wrapper307() { }
    }

    private final Instant ISSUED_AT = Instant.ofEpochSecond(1234567890).plusNanos(123456789);

    private ObjectMapper MAPPER = newMapper();

    @Test
    public void instantViaReadValue() throws Exception {
         String serialized = MAPPER.writeValueAsString(new Wrapper307(ISSUED_AT));
         Wrapper307 deserialized = MAPPER.readValue(serialized, Wrapper307.class);
         assertEquals(ISSUED_AT, deserialized.value);
    }

    @Test
    public void instantViaReadTree() throws Exception {
        String serialized = MAPPER.writeValueAsString(new Wrapper307(ISSUED_AT));
        JsonNode tree = MAPPER.readTree(serialized);
        Wrapper307 deserialized = MAPPER.treeToValue(tree, Wrapper307.class);
        assertEquals(ISSUED_AT, deserialized.value);
    }
}
