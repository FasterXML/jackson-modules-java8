package com.fasterxml.jackson.module.paramnames;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.BDDAssertions.then;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.databind.*;

// for [java8-modules#74]
public class CreatorWithNamingStrategy74Test
    extends ModuleTestBase
{
    static class ClassWithTwoProperties {
        public final int a;
        public final int b;

        public ClassWithTwoProperties(@JsonProperty("a") int a, @JsonProperty("b") int b) {
            this.a = a;
            this.b = b;
        }
    }

    @Test
    public void testPrivateConstructorWithAnnotations() throws Exception {
        verifyObjectDeserializationWithNamingStrategy(
                PropertyNamingStrategies.SNAKE_CASE,
                "{\"a\":1, \"b\": 2}",
                new ClassWithTwoProperties(1, 2));
    }

    private void verifyObjectDeserializationWithNamingStrategy(
            final PropertyNamingStrategy propertyNamingStrategy, final String json, Object expected)
            throws Exception {
        // given
        ObjectMapper objectMapper = newMapper()
                        .setPropertyNamingStrategy(propertyNamingStrategy)
                        .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
                        .setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)
                        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // when
        Object actual = objectMapper.readValue(json, expected.getClass());

        then(actual).isEqualToComparingFieldByField(expected);
    }
}
