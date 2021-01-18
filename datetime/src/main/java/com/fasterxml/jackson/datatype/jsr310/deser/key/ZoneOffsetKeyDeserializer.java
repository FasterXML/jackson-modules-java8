package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.time.DateTimeException;
import java.time.ZoneOffset;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;

public class ZoneOffsetKeyDeserializer extends Jsr310KeyDeserializer {

    public static final ZoneOffsetKeyDeserializer INSTANCE = new ZoneOffsetKeyDeserializer();

    private ZoneOffsetKeyDeserializer() {
        // singleton
    }

    @Override
    protected ZoneOffset deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        try {
            return ZoneOffset.of(key);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, ZoneOffset.class, e, key);
        }
    }
}
