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
    private static final long serialVersionUID = 1L;

    private final boolean _oneBaseMonths;

    public JavaTimeSerializerModifier(boolean oneBaseMonths) {
        _oneBaseMonths = oneBaseMonths;
    }

    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (_oneBaseMonths && valueType.hasRawClass(Month.class)) {
            return new OneBasedMonthSerializer(serializer);
        }
        return serializer;
    }
}
