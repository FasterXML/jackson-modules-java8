package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalLong;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.type.LogicalType;

public class OptionalLongDeserializer extends BaseScalarOptionalDeserializer<OptionalLong>
{
    private static final long serialVersionUID = 1L;

    static final OptionalLongDeserializer INSTANCE = new OptionalLongDeserializer();

    public OptionalLongDeserializer() {
        super(OptionalLong.class, OptionalLong.empty());
    }

    @Override
    public LogicalType logicalType() { return LogicalType.Integer; }

    @Override
    public OptionalLong deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        // minor optimization, first, for common case
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return OptionalLong.of(p.getLongValue());
        }
        CoercionAction act;
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            String text = p.getText();
            act = _checkFromStringCoercion(ctxt, text);
            // null and empty both same
            if ((act == CoercionAction.AsNull) || (act == CoercionAction.AsEmpty)) {
                return _empty;
            }
            text = text.trim();
            // 21-Jun-2020, tatu: Should this also accept "textual null" similar
            //   to regular Integers?
            return OptionalLong.of(_parseLongPrimitive(ctxt, text));
        case JsonTokenId.ID_NUMBER_FLOAT: // coercing may work too
            act = _checkFloatToIntCoercion(p, ctxt, _valueClass);
            if ((act == CoercionAction.AsNull) || (act == CoercionAction.AsEmpty)) {
                return _empty;
            }
            return OptionalLong.of(p.getValueAsLong());
        case JsonTokenId.ID_NULL:
            return _empty;
        case JsonTokenId.ID_START_ARRAY:
            return _deserializeFromArray(p, ctxt);
        }
        return (OptionalLong) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }
}
