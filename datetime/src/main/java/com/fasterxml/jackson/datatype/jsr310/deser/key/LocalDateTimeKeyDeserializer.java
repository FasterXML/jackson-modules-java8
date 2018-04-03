package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.DeserializationContext;

public class LocalDateTimeKeyDeserializer extends Jsr310KeyDeserializer {

    public static final LocalDateTimeKeyDeserializer INSTANCE = new LocalDateTimeKeyDeserializer();

    private LocalDateTimeKeyDeserializer() {
        // singleton
    }

    @Override
    protected LocalDateTime deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return LocalDateTime.parse(key, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, LocalDateTime.class, e, key);
        }
    }

}
