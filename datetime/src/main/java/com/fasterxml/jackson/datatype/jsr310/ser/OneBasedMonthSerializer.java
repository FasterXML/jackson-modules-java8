package com.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.Month;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @since 2.17
 */
public class OneBasedMonthSerializer extends JsonSerializer<Enum<Month>> {
    private final Supplier<Boolean> _serializeEnumsByIndex;
    private final JsonSerializer<Object> _defaultSerializer;

    @SuppressWarnings("unchecked")
    public OneBasedMonthSerializer(JsonSerializer<?> defaultSerializer, Supplier<Boolean> serializeEnumsByIndex) {
        _defaultSerializer = (JsonSerializer<Object>) defaultSerializer;
        _serializeEnumsByIndex = serializeEnumsByIndex;
    }

    @Override
    public void serialize(Enum<Month> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (_serializeEnumsByIndex.get()) {
            gen.writeNumber(value.ordinal() + 1);
            return;
        }
        _defaultSerializer.serialize(value, gen, serializers);
    }
}
