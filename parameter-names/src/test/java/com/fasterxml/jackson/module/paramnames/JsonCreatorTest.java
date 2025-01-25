package com.fasterxml.jackson.module.paramnames;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static org.assertj.core.api.BDDAssertions.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonCreatorTest extends ModuleTestBase
{
    static class ClassWithJsonCreatorOnStaticMethod {
        final String first;
        final String second;

        ClassWithJsonCreatorOnStaticMethod(String first, String second) {
             this.first = first;
             this.second = second;
        }

        @JsonCreator
        static ClassWithJsonCreatorOnStaticMethod factory(String first, String second) {
             return new ClassWithJsonCreatorOnStaticMethod(first, second);
        }
    }

    // [modules-base#178]
    static class Bean178
    {
        int _a, _b;

        public Bean178(@JsonDeserialize() int a, int b) {
            _a = a;
            _b = b;
        }
    }

    // [modules-base#301]
    static class Bean301
    {
        int _a, _b, _c;

        public Bean301(@JsonProperty(required=true) int a,
                @JsonProperty(value="", required=false) int b,
                int c) {
            _a = a;
            _b = b;
            _c = c;
        }
    }

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testJsonCreatorOnStaticMethod() throws Exception
    {
		// when
		String json = a2q("{'first':'1st','second':'2nd'}");
		ClassWithJsonCreatorOnStaticMethod actual = MAPPER.readValue(json, ClassWithJsonCreatorOnStaticMethod.class);

		then(actual).isEqualToComparingFieldByField(new ClassWithJsonCreatorOnStaticMethod("1st", "2nd"));
    }

    // [modules-base#178]
    @Test
    public void testJsonCreatorWithOtherAnnotations() throws Exception
    {
        Bean178 bean = MAPPER.readValue(a2q("{'a':1,'b':2}"), Bean178.class);
        assertNotNull(bean);
    }

    // [modules-base#301]
    @Test
    public void testCreatorNameMasking310() throws Exception
    {
        Bean301 bean = MAPPER.readValue(a2q("{'a':1,'b':2, 'c':3}"), Bean301.class);
        assertNotNull(bean);
        assertEquals(1, bean._a);
        assertEquals(2, bean._b);
        assertEquals(3, bean._c);
    }
}
