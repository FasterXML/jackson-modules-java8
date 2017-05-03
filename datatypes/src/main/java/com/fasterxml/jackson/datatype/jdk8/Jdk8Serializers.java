package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

class Jdk8Serializers extends Serializers.Base
{
    @Override
    public JsonSerializer<?> findReferenceSerializer(SerializationConfig config,
            ReferenceType refType, BeanDescription beanDesc,
            TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer)
    {
        final Class<?> raw = refType.getRawClass();
        if (Optional.class.isAssignableFrom(raw)) {
            return new OptionalSerializer(refType, contentTypeSerializer, contentValueSerializer);
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
        return findReferenceSerializer(config, refType, beanDesc, contentTypeSerializer, contentValueSerializer);
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
    {
        Class<?> raw = type.getRawClass();
        if (LongStream.class.isAssignableFrom(raw)) {
            return StreamLongSerializer.INSTANCE;
        }
        if (IntStream.class.isAssignableFrom(raw)) {
            return StreamIntSerializer.INSTANCE;
        }
        if (DoubleStream.class.isAssignableFrom(raw)) {
            return StreamDoubleSerializer.INSTANCE;
        }
        if (Stream.class.isAssignableFrom(raw)) {
            JavaType[] params = config.getTypeFactory().findTypeParameters(type, Stream.class);
            JavaType vt = (params == null || params.length != 1) ? TypeFactory.unknownType() : params[0];
            return new StreamSerializer(config.getTypeFactory().constructParametrizedType(Stream.class, Stream.class, vt), vt);
        }
        return super.findSerializer(config, type, beanDesc);
    }
}
