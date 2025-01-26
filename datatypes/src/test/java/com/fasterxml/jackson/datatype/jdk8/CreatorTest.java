package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

public class CreatorTest extends ModuleTestBase
{
    static class CreatorWithOptionalStrings
    {
        Optional<String> a, b;

        // note: something weird with test setup, should not need annotations
        @JsonCreator
        public CreatorWithOptionalStrings(@JsonProperty("a") Optional<String> a,
                @JsonProperty("b") Optional<String> b)
        {
            this.a = a;
            this.b = b;
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    /**
     * Test to ensure that creator parameters use defaulting
     * (introduced in Jackson 2.6)
     */
    @Test
    public void testCreatorWithOptionalDefault() throws Exception
    {
        CreatorWithOptionalStrings bean = MAPPER.readValue(
                a2q("{'a':'foo'}"), CreatorWithOptionalStrings.class);
        assertNotNull(bean);
        assertNotNull(bean.a);
        assertNotNull(bean.b);
        assertTrue(bean.a.isPresent());
        assertFalse(bean.b.isPresent());
        assertEquals("foo", bean.a.get());
    }

    @Test
    public void testCreatorWithOptionalAbsentAsNull() throws Exception
    {
        Jdk8Module module = new Jdk8Module()
                .configureReadAbsentAsNull(true);
        final ObjectMapper mapper = JsonMapper.builder()
                .addModule(module)
                .build();
        CreatorWithOptionalStrings bean = mapper.readValue(
                a2q("{'a':'foo'}"), CreatorWithOptionalStrings.class);
        assertNotNull(bean);
        assertNotNull(bean.a);
        assertTrue(bean.a.isPresent());
        assertEquals("foo", bean.a.get());

        // This is the config change
        assertNull(bean.b);
    }
}
