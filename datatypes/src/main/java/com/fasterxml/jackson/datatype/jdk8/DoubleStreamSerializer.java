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
public class DoubleStreamSerializer extends StdSerializer<DoubleStream>
{
    private static final long serialVersionUID = 1L;

    /**
     * Singleton instance
     */
    public static final DoubleStreamSerializer INSTANCE = new DoubleStreamSerializer();

    /**
     * Constructor
     */
    private DoubleStreamSerializer() {
        super(DoubleStream.class);
    }

    @Override
    public void serialize(DoubleStream stream, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        
        try(DoubleStream ds = stream) {
            jgen.writeStartArray();
            
            ds.forEachOrdered(value -> {
                try {
                    jgen.writeNumber(value);
                } catch (IOException e) {
                    throw new WrappedIOException(e);
                }
            });
            
            jgen.writeEndArray();
        } catch (WrappedIOException e) {
            throw e.getCause();
        } 
    }
}