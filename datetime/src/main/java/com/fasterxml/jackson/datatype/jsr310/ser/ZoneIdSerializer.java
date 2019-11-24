package com.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;

public class ZoneIdSerializer extends ToStringSerializerBase
{
    public ZoneIdSerializer() { super(ZoneId.class); }

    @Override
    public void serializeWithType(Object value, JsonGenerator g,
            SerializerProvider ctxt, TypeSerializer typeSer) throws IOException
    {
        // Better ensure we don't use specific sub-classes:
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, ZoneId.class, JsonToken.VALUE_STRING));
        serialize(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    @Override
    public String valueToString(Object value) {
        return value.toString();
    }
}
