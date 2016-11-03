package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.OptionalDouble;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

final class OptionalDoubleDeserializer extends StdDeserializer<OptionalDouble>
{
    private static final long serialVersionUID = 1L;

    static final OptionalDoubleDeserializer INSTANCE = new OptionalDoubleDeserializer();

    public OptionalDoubleDeserializer() {
        super(OptionalDouble.class);
    }

    @Override
    public OptionalDouble getNullValue(DeserializationContext ctxt) {
        return OptionalDouble.empty();
    }

    @Override
    public OptionalDouble deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return OptionalDouble.of(jp.getValueAsDouble());
    }
}
