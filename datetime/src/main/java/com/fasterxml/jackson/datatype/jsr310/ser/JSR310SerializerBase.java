package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * Base class that indicates that all JSR310 datatypes are serialized as scalar JSON types.
 *
 * @author Nick Williams
 */
abstract class JSR310SerializerBase<T> extends StdSerializer<T>
{
    protected JSR310SerializerBase(Class<?> supportedType) {
        super(supportedType);
    }

    @Override
    public void serializeWithType(T value, JsonGenerator g, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, serializationShape(ctxt)));
        serialize(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    /**
     * Overridable helper method used from {@link #serializeWithType}, to indicate
     * shape of value during serialization; needed to know how type id is to be
     * serialized.
     */
    protected abstract JsonToken serializationShape(SerializerProvider provider);
}
