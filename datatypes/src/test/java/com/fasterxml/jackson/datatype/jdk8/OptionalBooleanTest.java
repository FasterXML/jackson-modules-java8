package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalBooleanTest extends ModuleTestBase
{
    static class BooleanBean {
        public Optional<Boolean> value;

        public BooleanBean() { }
        public BooleanBean(Boolean b) {
            value = Optional.ofNullable(b);
        }
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    // for [datatype-jdk8#23]
    @Test
    public void testBoolean() throws Exception
    {
        // First, serialization
        String json = MAPPER.writeValueAsString(new BooleanBean(true));
        assertEquals(a2q("{'value':true}"), json);
        json = MAPPER.writeValueAsString(new BooleanBean());
        assertEquals(a2q("{'value':null}"), json);
        json = MAPPER.writeValueAsString(new BooleanBean(null));
        assertEquals(a2q("{'value':null}"), json);

        // then deser
        BooleanBean b = MAPPER.readValue(a2q("{'value':null}"), BooleanBean.class);
        assertNotNull(b.value);
        assertFalse(b.value.isPresent());

        b = MAPPER.readValue(a2q("{'value':false}"), BooleanBean.class);
        assertNotNull(b.value);
        assertTrue(b.value.isPresent());
        assertFalse(b.value.get().booleanValue());

        b = MAPPER.readValue(a2q("{'value':true}"), BooleanBean.class);
        assertNotNull(b.value);
        assertTrue(b.value.isPresent());
        assertTrue(b.value.get().booleanValue());

        // and looks like a special, somewhat non-conforming case is what a user had
        // issues with
        b = MAPPER.readValue(a2q("{'value':''}"), BooleanBean.class);
        assertNotNull(b.value);
        assertFalse(b.value.isPresent());
    }
}
