package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.DeserializationContext;

public class OffsetTimeKeyDeserializer extends Jsr310KeyDeserializer {

    public static final OffsetTimeKeyDeserializer INSTANCE = new OffsetTimeKeyDeserializer();

    private OffsetTimeKeyDeserializer() {
        // singleton
    }

    @Override
    protected OffsetTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return OffsetTime.parse(key, DateTimeFormatter.ISO_OFFSET_TIME);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, OffsetTime.class, e, key);
        }
    }

}
