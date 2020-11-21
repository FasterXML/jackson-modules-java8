package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalDouble;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.type.LogicalType;

class OptionalDoubleDeserializer extends BaseScalarOptionalDeserializer<OptionalDouble>
{
    private static final long serialVersionUID = 1L;

    static final OptionalDoubleDeserializer INSTANCE = new OptionalDoubleDeserializer();

    public OptionalDoubleDeserializer() {
        super(OptionalDouble.class, OptionalDouble.empty());
    }

    @Override
    public LogicalType logicalType() { return LogicalType.Float; }
    
    @Override
    public OptionalDouble deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // minor optimization, first, for common case
        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT)) {
            return OptionalDouble.of(p.getDoubleValue());
        }
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_STRING:
            String text = p.getText();
            // 19-Nov-2020, ckozak: see jackson-databind#2942: Special case, floating point special
            //     values as String (e.g. "NaN", "Infinity", "-Infinity" need to be considered
            //     "native" representation as JSON does not allow as numbers, and hence not bound
            //     by coercion rules
            Double specialValue = _checkDoubleSpecialValue(text);
            if (specialValue != null) {
                return OptionalDouble.of(specialValue);
            }
            CoercionAction act = _checkFromStringCoercion(ctxt, text);
            // null and empty both same
            if ((act == CoercionAction.AsNull) || (act == CoercionAction.AsEmpty)) {
                return _empty;
            }
            // 21-Jun-2020, tatu: Should this also accept "textual null" similar
            //   to regular Doubles?
            text = text.trim();
            return OptionalDouble.of(_parseDoublePrimitive(ctxt, text));
        case JsonTokenId.ID_NUMBER_INT: // coercion here should be fine
            return OptionalDouble.of(p.getDoubleValue());
        case JsonTokenId.ID_NULL:
            return getNullValue(ctxt);
        case JsonTokenId.ID_START_ARRAY:
            return _deserializeFromArray(p, ctxt);
        }
        return (OptionalDouble) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }
}
