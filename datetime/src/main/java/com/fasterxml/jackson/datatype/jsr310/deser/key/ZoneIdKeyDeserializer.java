package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.ZoneId;

import com.fasterxml.jackson.databind.DeserializationContext;

public class ZoneIdKeyDeserializer extends Jsr310KeyDeserializer {

    public static final ZoneIdKeyDeserializer INSTANCE = new ZoneIdKeyDeserializer();

    private ZoneIdKeyDeserializer() {
        // singleton
    }

    @Override
    protected Object deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return ZoneId.of(key);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, ZoneId.class, e, key);
        }
    }
}
