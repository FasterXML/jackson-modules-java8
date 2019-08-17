package com.fasterxml.jackson.datatype.jsr310.deser.key;

import java.io.IOException;
import java.time.DateTimeException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

abstract class Jsr310KeyDeserializer extends KeyDeserializer
{

    @SuppressWarnings("deprecation")
    @Override
    public final Object deserializeKey(String key, DeserializationContext ctxt)
        throws IOException
    {
        // 17-Aug-2019, tatu: Jackson 2.x had special handling for "null" key marker, which
        //    is why we have this unnecessary dispatching, for now
        return deserialize(key, ctxt);
    }

    protected abstract Object deserialize(String key, DeserializationContext ctxt)
        throws IOException;

    @SuppressWarnings("unchecked")
    protected <T> T _handleDateTimeException(DeserializationContext ctxt,
              Class<?> type, DateTimeException e0, String value) throws IOException
    {
        try {
            return (T) ctxt.handleWeirdKey(type, value,
                    "Failed to deserialize %s: (%s) %s",
                    ClassUtil.nameOf(type),
                    e0.getClass().getName(),
                    e0.getMessage());

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
