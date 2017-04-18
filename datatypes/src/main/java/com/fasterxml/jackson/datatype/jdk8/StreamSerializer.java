package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.stream.Stream;

/**
 * Common typed stream serializer
 *
 * @param <T> type of the stream elements
 */
public class StreamSerializer<T> extends StdSerializer<Stream<T>> implements ContextualSerializer {
    /**
     * Stream elements type (matching T)
     */
    private final JavaType elemType;
    /**
     * element specific serializer, if any
     */
    private transient final JsonSerializer<T> elemSerializer;

    /**
     * Constructor
     *
     * @param streamType Stream type
     * @param elemType   Stream elements type (matching T)
     */
    public StreamSerializer(JavaType streamType, JavaType elemType) {
        this(streamType, elemType, null);
    }

    /**
     * Constructor with custom serializer
     *
     * @param streamType     Stream type
     * @param elemType       Stream elements type (matching T)
     * @param elemSerializer Custom serializer to use for element type
     */
    public StreamSerializer(JavaType streamType, JavaType elemType, JsonSerializer<T> elemSerializer) {
        super(streamType);
        this.elemType = elemType;
        this.elemSerializer = elemSerializer;
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        if (!elemType.hasRawClass(Object.class)
                && (provider.isEnabled(MapperFeature.USE_STATIC_TYPING) || elemType.isFinal())) {
            return new StreamSerializer(
                    provider.getTypeFactory().constructParametrizedType(Stream.class, Stream.class, elemType),
                    elemType,
                    provider.findPrimaryPropertySerializer(elemType, property));
        }
        return this;
    }

    @Override
    public void serialize(Stream<T> stream, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartArray();
        try {
            stream.forEachOrdered(elem -> {
                try {
                    if (elemSerializer == null) {
                        provider.defaultSerializeValue(elem, jgen);
                    } else {
                        elemSerializer.serialize(elem, jgen, provider);
                    }
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
