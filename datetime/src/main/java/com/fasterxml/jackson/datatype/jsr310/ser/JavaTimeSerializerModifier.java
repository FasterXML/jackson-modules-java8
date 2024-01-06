package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.Month;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

/**
 * @since 2.17
 */
public class JavaTimeSerializerModifier extends BeanSerializerModifier {
    private final boolean _oneBaseMonths;

    public JavaTimeSerializerModifier(boolean oneBaseMonths) {
        _oneBaseMonths = oneBaseMonths;
    }

    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (valueType.hasRawClass(Month.class)) {
            return new MonthSerializer((JsonSerializer<Enum>) serializer, _oneBaseMonths);
        }
        return serializer;
    }

}