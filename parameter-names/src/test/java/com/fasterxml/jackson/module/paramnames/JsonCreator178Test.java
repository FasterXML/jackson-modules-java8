package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.junit.*;

import static org.junit.Assert.assertNotNull;

public class JsonCreator178Test extends ModuleTestBase
{
    // [modules-base#178]
    static class Bean178
    {
        int _a, _b;

        public Bean178(@JsonDeserialize() int a, int b) {
            _a = a;
            _b = b;
        }
    }

    private final ObjectMapper MAPPER = newMapper();

    // [modules-base#178]
    @Test
    public void testJsonCreatorWithOtherAnnotations() throws Exception
    {
        Bean178 bean = MAPPER.readValue(a2q("{'a':1,'b':2}"), Bean178.class);
        assertNotNull(bean);
    }
}
