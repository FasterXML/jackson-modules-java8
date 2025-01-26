package com.fasterxml.jackson.module.paramnames;

import static org.assertj.core.api.BDDAssertions.then;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class NamingStrategy67Test extends ModuleTestBase
{
    static class ClassWithOneProperty {
        public final String firstProperty;

        @JsonCreator
//       @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public ClassWithOneProperty(String firstProperty) {
             this.firstProperty = "CTOR:"+firstProperty;
        }
   }
    static class ClassWithTwoProperties {
        public final int a;
        public final int b;

        ClassWithTwoProperties(@JsonProperty("a") int a, @JsonProperty("b") int b) {
            this.a = a+1;
            this.b = b+1;
        }
    }

    @Test
    public void testSnakeCaseNaming() throws Exception
    {
        ObjectMapper mapper = newMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                ;
        final String MSG = "1st";
        ClassWithOneProperty actual = mapper.readValue(
//                "\""+MSG+"\"",
                "{\"first_property\":\""+MSG+"\"}",
//                "{\"firstProperty\":\""+MSG+"\"}",
                ClassWithOneProperty.class);
        then(actual).isEqualToComparingFieldByField(new ClassWithOneProperty(MSG));
    }

    @Test
    public void testPrivateConstructorWithPropertyAnnotations() throws Exception
    {
        ObjectMapper mapper = newMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        ClassWithTwoProperties actual = mapper.readValue("{\"a\":1, \"b\": 2}",
                ClassWithTwoProperties.class);

        then(actual).isEqualToComparingFieldByField(new ClassWithTwoProperties(1, 2));
    }
}
