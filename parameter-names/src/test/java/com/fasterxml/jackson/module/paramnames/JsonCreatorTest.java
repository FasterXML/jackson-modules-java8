package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static org.junit.Assert.assertNotNull;

import org.junit.*;

import static org.assertj.core.api.BDDAssertions.*;

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
}
