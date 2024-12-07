package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class OptionalSerializer
    extends ReferenceTypeSerializer<Optional<?>> // since 2.9
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Constructors, factory methods
    /**********************************************************
     */

    protected OptionalSerializer(ReferenceType fullType, boolean staticTyping,
            TypeSerializer vts, JsonSerializer<Object> ser)
    {
        super(fullType, staticTyping, vts, ser);
    }

    protected OptionalSerializer(OptionalSerializer base, BeanProperty property,
            TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper,
            Object suppressableValue, boolean suppressNulls)
    {
        super(base, property, vts, valueSer, unwrapper,
                suppressableValue, suppressNulls);
    }

    @Override
    protected ReferenceTypeSerializer<Optional<?>> withResolved(BeanProperty prop,
            TypeSerializer vts, JsonSerializer<?> valueSer,
            NameTransformer unwrapper)
    {
        return new OptionalSerializer(this, prop, vts, valueSer, unwrapper,
                _suppressableValue, _suppressNulls);
    }

    @Override
    public ReferenceTypeSerializer<Optional<?>> withContentInclusion(Object suppressableValue,
            boolean suppressNulls)
    {
        return new OptionalSerializer(this, _property, _valueTypeSerializer,
                _valueSerializer, _unwrapper,
                suppressableValue, suppressNulls);
    }

    /*
    /**********************************************************
    /* Abstract method impls
    /**********************************************************
     */

    @Override
    protected boolean _isValuePresent(Optional<?> value) {
        return value.isPresent();
    }

    @Override
    protected Object _getReferenced(Optional<?> value) {
        return value.get();
    }

    @Override
    protected Object _getReferencedIfPresent(Optional<?> value) {
        return value.isPresent() ? value.get() : null;
    }

    /*
    /**********************************************************
    /* Serialization
    /**********************************************************
     */

    /**
     * [modules-java8#86] Cannot read {@link java.util.Optional}'s written with
     * {@link com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder}.
     * This override implementation was removed in 2.9, but restored and
     * modified in 2.19 due to [modules-java8#86].
     *
     * @since 2.19
     */
    @Override
    public void serializeWithType(Optional<?> ref, JsonGenerator g,
                                  SerializerProvider provider, TypeSerializer typeSer)
            throws IOException
    {
        if (!ref.isPresent()) {
            // [datatype-jdk8#20]: can not write `null` if unwrapped
            if (_unwrapper == null) {
                provider.defaultSerializeNull(g);
            }
            return;
        }
        WritableTypeId typeId = typeSer.writeTypePrefix(g,
                typeSer.typeId(ref, JsonToken.VALUE_STRING));
        serialize(ref, g, provider);
        typeSer.writeTypeSuffix(g, typeId);
    }

}
