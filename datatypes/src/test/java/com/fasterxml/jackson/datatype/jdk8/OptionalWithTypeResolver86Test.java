package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

// [modules-java8#86] Cannot read `Optional`s written with `StdTypeResolverBuilder`
public class OptionalWithTypeResolver86Test
        extends ModuleTestBase
{

    public static class Foo<T> {
        public Optional<T> value;
    }

    public static class Pojo86 {
        public String name;

        // with static method
        public static Pojo86 valueOf(String name) {
            Pojo86 pojo = new Pojo86();
            pojo.name = name;
            return pojo;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pojo86 pojo86 = (Pojo86) o;
            return Objects.equals(name, pojo86.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    @Test
    public void test() throws Exception {
        _testOptionalWith(Optional.of("MyName"), String.class, "MyName");
        _testOptionalWith(Optional.of(42), Integer.class, 42);
        _testOptionalWith(Optional.of(Pojo86.valueOf("PojoName")), Pojo86.class, Pojo86.valueOf("PojoName"));
    }

    private <T> void _testOptionalWith(Optional<T> value, Class<T> type, T expectedValue)
            throws Exception
    {
        ObjectMapper mapper = configureObjectMapper();

        // Serialize
        Foo<T> foo = new Foo<>();
        foo.value = value;
        String json = mapper.writeValueAsString(foo);
        String expectedJSON = a2q(String.format(
                "{'%s':{'value':{'%s':%s}}}",
                Foo.class.getName(),
                Optional.class.getName(),
                mapper.writeValueAsString(expectedValue)
        ));
        assertEquals(expectedJSON, json);

        // Deserialize
        Foo<T> bean = mapper.readValue(json, mapper.getTypeFactory().constructParametricType(Foo.class, type));
        assertEquals(value, bean.value);
    }

    private ObjectMapper configureObjectMapper() {
        ObjectMapper mapper = mapperWithModule();
        mapper.setDefaultTyping(
                new StdTypeResolverBuilder()
                        .init(JsonTypeInfo.Id.CLASS, null)
                        .inclusion(JsonTypeInfo.As.WRAPPER_OBJECT)
        );
        return mapper;
    }

}
