package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * Base class that indicates that all JSR310 datatypes are serialized as scalar JSON types.
 *
 * @author Nick Williams
 */
abstract class JSR310SerializerBase<T> extends StdSerializer<T>
{
    private static final long serialVersionUID = 1L;

    protected JSR310SerializerBase(Class<?> supportedType) {
        super(supportedType, false);
    }

    @Override
    public void serializeWithType(T value, JsonGenerator g, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, serializationShape(provider)));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    /**
     * Overridable helper method used from {@link #serializeWithType}, to indicate
     * shape of value during serialization; needed to know how type id is to be
     * serialized.
     *
     * @since 2.9
     */
    protected abstract JsonToken serializationShape(SerializerProvider provider);
}
