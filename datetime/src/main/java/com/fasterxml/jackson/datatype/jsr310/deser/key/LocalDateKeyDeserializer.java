package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.DeserializationContext;

public class LocalDateKeyDeserializer extends Jsr310KeyDeserializer {

    public static final LocalDateKeyDeserializer INSTANCE = new LocalDateKeyDeserializer();

    private LocalDateKeyDeserializer() {
        // singleton
    }

    @Override
    protected LocalDate deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return LocalDate.parse(key, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, LocalDate.class, e, key);
        }
    }
}
