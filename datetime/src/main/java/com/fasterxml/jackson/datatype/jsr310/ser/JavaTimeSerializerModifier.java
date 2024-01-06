package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.Month;
import java.util.function.Supplier;

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
    private final Supplier<Boolean> _serializeEnumsByIndex;

    public JavaTimeSerializerModifier(boolean oneBaseMonths, Supplier<Boolean> serializeEnumsByIndex) {
        _oneBaseMonths = oneBaseMonths;
        _serializeEnumsByIndex = serializeEnumsByIndex;
    }

    @Override
    public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config, JavaType valueType, BeanDescription beanDesc, JsonSerializer<?> serializer) {
        if (_oneBaseMonths && valueType.hasRawClass(Month.class)) {
            return new OneBasedMonthSerializer((JsonSerializer<Enum>) serializer, _serializeEnumsByIndex);
        }
        return serializer;
    }

}