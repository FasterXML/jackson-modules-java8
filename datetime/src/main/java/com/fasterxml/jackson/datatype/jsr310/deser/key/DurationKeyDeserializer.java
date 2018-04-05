package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Duration;

import com.fasterxml.jackson.databind.DeserializationContext;

public class DurationKeyDeserializer extends Jsr310KeyDeserializer {

    public static final DurationKeyDeserializer INSTANCE = new DurationKeyDeserializer();

    private DurationKeyDeserializer() {
        // singleton
    }

    @Override
    protected Duration deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return Duration.parse(key);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, Duration.class, e, key);
        }
    }
}
