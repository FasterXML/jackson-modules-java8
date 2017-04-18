package com.fasterxml.jackson.datatype.jdk8;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.stream.IntStream;

/**
 * {@link IntStream} serializer
 * <p>
 * Unfortunately there to common ancestor between number base stream, so we need to define each in a specific class
 * </p>
 */
public class StreamIntSerializer extends StdSerializer<IntStream> {

    /**
     * Singleton instance
     */
    public static final StreamIntSerializer INSTANCE = new StreamIntSerializer();

    /**
     * Constructor
     */
    private StreamIntSerializer() {
        super(IntStream.class);
    }

    @Override
    public void serialize(IntStream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        try {
            stream.forEachOrdered(value -> {
                try {
                    jgen.writeNumber(value);
                } catch (IOException e) {
                    throw new WrappedIOException(e);
                }
            });
            stream.close();
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
        jgen.writeEndArray();
    }
}
