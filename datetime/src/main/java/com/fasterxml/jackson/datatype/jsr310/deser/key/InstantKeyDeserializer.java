package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.DeserializationContext;

public class InstantKeyDeserializer extends Jsr310KeyDeserializer {

    public static final InstantKeyDeserializer INSTANCE = new InstantKeyDeserializer();

    private InstantKeyDeserializer() {
        // singleton
    }

    @Override
    protected Instant deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return DateTimeFormatter.ISO_INSTANT.parse(key, Instant::from);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, Instant.class, e, key);
        }
    }
}
