package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;

class Jdk8Serializers extends Serializers.Base
{
    @Override
    public JsonSerializer<?> findReferenceSerializer(SerializationConfig config, 
            ReferenceType refType, BeanDescription beanDesc,
            TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer)
    {
        final Class<?> raw = refType.getRawClass();
        if (Optional.class.isAssignableFrom(raw)) {
            boolean staticTyping = (contentTypeSerializer == null)
                    && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            return new OptionalSerializer(refType, staticTyping,
                    contentTypeSerializer, contentValueSerializer);
        }
        if (OptionalInt.class.isAssignableFrom(raw)) {
            return OptionalIntSerializer.INSTANCE;
        }
        if (OptionalLong.class.isAssignableFrom(raw)) {
            return OptionalLongSerializer.INSTANCE;
        }
        if (OptionalDouble.class.isAssignableFrom(raw)) {
            return OptionalDoubleSerializer.INSTANCE;
        }
        return null;
    }
}
