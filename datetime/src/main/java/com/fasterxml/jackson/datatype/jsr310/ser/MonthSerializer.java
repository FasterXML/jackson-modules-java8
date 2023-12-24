package com.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @since 2.17
 */
public class MonthSerializer extends JsonSerializer<Enum<Month>> {
    private final boolean _enableOneBaseMonths;
    private final JsonSerializer<Enum> _defaultSerializer;

    public MonthSerializer(JsonSerializer<Enum> defaultSerializer, boolean oneBaseMonths) {
        _defaultSerializer = defaultSerializer;
        _enableOneBaseMonths = oneBaseMonths;
    }

    @Override
    public void serialize(Enum<Month> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if(_enableOneBaseMonths
            /*
                && we write the enum ordinal (int) value,
                via WRITE_ENUMS_USING_INDEX or other similar construct
            */
        ) {
            gen.writeNumber(value.ordinal() + 1);
            return;
        }
        _defaultSerializer.serialize(value, gen, serializers);
    }

}