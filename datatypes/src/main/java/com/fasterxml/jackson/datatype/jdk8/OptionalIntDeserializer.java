package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalInt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class OptionalIntDeserializer extends StdScalarDeserializer<OptionalInt>
{
    private static final long serialVersionUID = 1L;

    static final OptionalIntDeserializer INSTANCE = new OptionalIntDeserializer();

    public OptionalIntDeserializer() {
        super(OptionalInt.class);
    }

    @Override
    public OptionalInt getNullValue(DeserializationContext ctxt) {
        return OptionalInt.empty();
    }

    @Override
    public OptionalInt deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return OptionalInt.of(p.getValueAsInt());
    }
}
