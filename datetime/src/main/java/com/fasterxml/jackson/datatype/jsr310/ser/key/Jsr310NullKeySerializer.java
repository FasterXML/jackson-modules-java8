package com.fasterxml.jackson.datatype.jsr310.ser.key;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * This class is to be used in case {@code null} keys are needed to be serialized in a {@link Map} with Java 8 temporal keys. By default the
 * {@code null} key is not supported by jackson, the serializer needs to be registered manually.
 *
 * @author Zoltan Kiss
 * @since 2.6
 */
@Deprecated // since 2.10 -- not sure why module should provide general purpose null serializer
//   (maybe add in databind)
public class Jsr310NullKeySerializer extends JsonSerializer<Object> {

    public static final String NULL_KEY = "";

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException
    {
        if (value != null) {
            throw JsonMappingException.from(gen,
                    "Jsr310NullKeySerializer is only for serializing null values.");
        }
        gen.writeFieldName(NULL_KEY);
    }

}
