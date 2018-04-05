package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer;

abstract class Jsr310KeyDeserializer extends KeyDeserializer {

    @Override
    public final Object deserializeKey(String key, DeserializationContext ctxt)
        throws IOException
    {
        if (Jsr310NullKeySerializer.NULL_KEY.equals(key)) {
            // potential null key in HashMap
            return null;
        }
        return deserialize(key, ctxt);
    }

    protected abstract Object deserialize(String key, DeserializationContext ctxt)
        throws IOException;

    @SuppressWarnings("unchecked")
    protected <T> T _handleDateTimeException(DeserializationContext ctxt,
              Class<?> type, DateTimeException e0, String value) throws IOException
    {
        try {
            return (T) ctxt.handleWeirdStringValue(type, value,
                    "Failed to deserialize %s: (%s) %s",
                    type.getName(), e0.getClass().getName(), e0.getMessage());

        } catch (JsonMappingException e) {
            e.initCause(e0);
            throw e;
        } catch (IOException e) {
            if (null == e.getCause()) {
                e.initCause(e0);
            }
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }
}
