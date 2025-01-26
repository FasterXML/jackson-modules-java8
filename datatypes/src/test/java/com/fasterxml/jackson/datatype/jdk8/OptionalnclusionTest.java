package com.fasterxml.jackson.datatype.jdk8;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalnclusionTest extends ModuleTestBase
{
    @JsonAutoDetect(fieldVisibility=Visibility.ANY)
    public static final class OptionalData {
        public Optional<String> myString = Optional.empty();
    }

    // for [datatype-jdk8#18]
    static class OptionalNonEmptyStringBean {
        @JsonInclude(value=Include.NON_EMPTY, content=Include.NON_EMPTY)
        public Optional<String> value;

        public OptionalNonEmptyStringBean() { }
        OptionalNonEmptyStringBean(String str) {
            value = Optional.ofNullable(str);
        }
    }

    public static final class OptionalGenericData<T> {
        public Optional<T> myData;
        public static <T> OptionalGenericData<T> construct(T data) {
            OptionalGenericData<T> ret = new OptionalGenericData<T>();
            ret.myData = Optional.of(data);
            return ret;
        }
    }

    static final class OptMapBean {
        public Map<String, Optional<?>> values;

        public OptMapBean(String key, Optional<?> v) {
            values = new LinkedHashMap<>();
            values.put(key, v);
        }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testSerOptNonEmpty() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_EMPTY).writeValueAsString(data);
        assertEquals("{}", value);
    }

    @Test
    public void testSerOptNonDefault() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_DEFAULT).writeValueAsString(data);
        assertEquals("{}", value);
    }

    @Test
    public void testSerOptNonAbsent() throws Exception
    {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_ABSENT).writeValueAsString(data);
        assertEquals("{}", value);
    }

    @Test
    public void testExcludeEmptyStringViaOptional() throws Exception
    {
        String json = MAPPER.writeValueAsString(new OptionalNonEmptyStringBean("x"));
        assertEquals("{\"value\":\"x\"}", json);
        json = MAPPER.writeValueAsString(new OptionalNonEmptyStringBean(null));
        assertEquals("{}", json);
        json = MAPPER.writeValueAsString(new OptionalNonEmptyStringBean(""));
        assertEquals("{}", json);
    }

    @Test
    public void testSerPropInclusionAlways() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.ALWAYS);
        ObjectMapper mapper = mapperWithModule().setDefaultPropertyInclusion(incl);
        assertEquals("{\"myData\":true}",
                mapper.writeValueAsString(OptionalGenericData.construct(Boolean.TRUE)));
    }

    @Test
    public void testSerPropInclusionNonNull() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_NULL);
        ObjectMapper mapper = mapperWithModule().setDefaultPropertyInclusion(incl);
        assertEquals("{\"myData\":true}",
                mapper.writeValueAsString(OptionalGenericData.construct(Boolean.TRUE)));
    }

    @Test
    public void testSerPropInclusionNonAbsent() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_ABSENT);
        ObjectMapper mapper = mapperWithModule().setDefaultPropertyInclusion(incl);
        assertEquals("{\"myData\":true}",
                mapper.writeValueAsString(OptionalGenericData.construct(Boolean.TRUE)));
    }

    @Test
    public void testSerPropInclusionNonEmpty() throws Exception
    {
        JsonInclude.Value incl =
                JsonInclude.Value.construct(JsonInclude.Include.NON_ABSENT, JsonInclude.Include.NON_EMPTY);
        ObjectMapper mapper = mapperWithModule().setDefaultPropertyInclusion(incl);
        assertEquals("{\"myData\":true}",
                mapper.writeValueAsString(OptionalGenericData.construct(Boolean.TRUE)));
    }

    @Test
    public void testMapElementInclusion() throws Exception
    {
        ObjectMapper mapper = mapperWithModule().setDefaultPropertyInclusion(
                JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_ABSENT));
        // first: Absent entry/-ies should NOT be included
        assertEquals("{\"values\":{}}",
                mapper.writeValueAsString(new OptMapBean("key", Optional.empty())));
        // but non-empty should
        assertEquals("{\"values\":{\"key\":\"value\"}}",
                mapper.writeValueAsString(new OptMapBean("key", Optional.of("value"))));
        // and actually even empty
        assertEquals("{\"values\":{\"key\":\"\"}}",
                mapper.writeValueAsString(new OptMapBean("key", Optional.of(""))));
    }
}
