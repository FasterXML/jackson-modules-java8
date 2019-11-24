package com.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;

// @since 2.10.2
public class ZoneIdSerializer extends ToStringSerializerBase
{
    private static final long serialVersionUID = 1L;

    public ZoneIdSerializer() { super(ZoneId.class); }

    @Override
    public void serializeWithType(Object value, JsonGenerator g,
            SerializerProvider provider, TypeSerializer typeSer) throws IOException
    {
        // Better ensure we don't use specific sub-classes:
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, ZoneId.class, JsonToken.VALUE_STRING));
        serialize(value, g, provider);
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    @Override
    public String valueToString(Object value) {
        return value.toString();
    }
}
