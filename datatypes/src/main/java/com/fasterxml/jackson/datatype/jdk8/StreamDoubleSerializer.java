package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.stream.DoubleStream;

/**
 * {@link DoubleStream} serializer
 * <p>
 * Unfortunately there to common ancestor between number base stream,
 * so we need to define each in a specific class
 * </p>
 */
public class StreamDoubleSerializer extends StdSerializer<DoubleStream> {

    /**
     * Singleton instance
     */
    public static final StreamDoubleSerializer INSTANCE = new StreamDoubleSerializer();

    /**
     * Constructor
     */
    private StreamDoubleSerializer() {
        super(DoubleStream.class);
    }

    @Override
    public void serialize(DoubleStream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException {
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