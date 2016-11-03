package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Base class that indicates that all JSR310 datatypes are serialized as scalar JSON types.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
abstract class JSR310SerializerBase<T> extends StdSerializer<T>
{
    private static final long serialVersionUID = 1L;

    protected JSR310SerializerBase(Class<?> supportedType) {
        super(supportedType, false);
    }

    @Override
    public void serializeWithType(T value, JsonGenerator generator, SerializerProvider provider,
                                  TypeSerializer serializer) throws IOException
    {
        serializer.writeTypePrefixForScalar(value, generator);
        this.serialize(value, generator, provider);
        serializer.writeTypeSuffixForScalar(value, generator);
    }
}
