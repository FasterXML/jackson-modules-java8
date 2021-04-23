package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OptionalWithEmptyTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    static class BooleanBean {
        public Optional<Boolean> value;

        public BooleanBean() { }
        public BooleanBean(Boolean b) {
            value = Optional.ofNullable(b);
        }
    }

    public void testOptionalFromEmpty() throws Exception {
        Optional<?> value = MAPPER.readValue(q(""), new TypeReference<Optional<Integer>>() {});
        assertEquals(false, value.isPresent());
    }

    // for [datatype-jdk8#23]
    public void testBooleanWithEmpty() throws Exception
    {
        // and looks like a special, somewhat non-conforming case is what a user had
        // issues with
        BooleanBean b = MAPPER.readValue(a2q("{'value':''}"), BooleanBean.class);
        assertNotNull(b.value);

        assertEquals(false, b.value.isPresent());
    }

}
