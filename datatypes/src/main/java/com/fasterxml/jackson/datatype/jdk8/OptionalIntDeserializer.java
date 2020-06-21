package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalInt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.type.LogicalType;

public class OptionalIntDeserializer extends BaseScalarOptionalDeserializer<OptionalInt>
{
    private static final long serialVersionUID = 1L;

    static final OptionalIntDeserializer INSTANCE = new OptionalIntDeserializer();

    public OptionalIntDeserializer() {
        super(OptionalInt.class, OptionalInt.empty());
    }

    @Override
    public LogicalType logicalType() { return LogicalType.Integer; }

    @Override
    public OptionalInt deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // minor optimization, first, for common case
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return OptionalInt.of(p.getIntValue());
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
            return OptionalInt.of(_parseIntPrimitive(ctxt, text));
        case JsonTokenId.ID_NUMBER_FLOAT: // coercing may work too
            act = _checkFloatToIntCoercion(p, ctxt, _valueClass);
            if ((act == CoercionAction.AsNull) || (act == CoercionAction.AsEmpty)) {
                return _empty;
            }
            return OptionalInt.of(p.getValueAsInt());
        case JsonTokenId.ID_NULL:
            return _empty;
        case JsonTokenId.ID_START_ARRAY:
            return _deserializeFromArray(p, ctxt);
        default:
        }
        return (OptionalInt) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }
}
