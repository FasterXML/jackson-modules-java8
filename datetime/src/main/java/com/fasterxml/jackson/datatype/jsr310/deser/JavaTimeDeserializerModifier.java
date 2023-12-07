package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class JavaTimeDeserializerModifier extends BeanDeserializerModifier {
    private final boolean _oneBaseMonths;

    public JavaTimeDeserializerModifier() {
        this(false);
    }

    public JavaTimeDeserializerModifier(boolean oneBaseMonths) {
        _oneBaseMonths = oneBaseMonths;
    }

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> defaultDeserializer) {
        if (type.hasRawClass(Month.class)) {
            return new MonthDeserializer((JsonDeserializer<Enum>) defaultDeserializer, _oneBaseMonths);
        }
        return defaultDeserializer;
    }

}
