package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Period;

import com.fasterxml.jackson.databind.DeserializationContext;

public class PeriodKeyDeserializer extends Jsr310KeyDeserializer {

    public static final PeriodKeyDeserializer INSTANCE = new PeriodKeyDeserializer();

    private PeriodKeyDeserializer() {
        // singletin
    }

    @Override
    protected Period deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return Period.parse(key);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, Period.class, e, key);
        }
    }
}
