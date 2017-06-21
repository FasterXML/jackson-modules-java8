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
 */
public class StreamSerializer extends StdSerializer<Stream<?>> implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    /**
     * Stream elements type (matching T)
     */
    private final JavaType elemType;
    
    /**
     * element specific serializer, if any
     */
    private transient final JsonSerializer<Object> elemSerializer;

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
    public StreamSerializer(JavaType streamType, JavaType elemType, JsonSerializer<Object> elemSerializer) {
        super(streamType);
        this.elemType = elemType;
        this.elemSerializer = elemSerializer;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException
    {
        if (!elemType.hasRawClass(Object.class)
                && (provider.isEnabled(MapperFeature.USE_STATIC_TYPING) || elemType.isFinal())) {
            return new StreamSerializer(
                    provider.getTypeFactory().constructParametricType(Stream.class, elemType),
                    elemType,
                    provider.findPrimaryPropertySerializer(elemType, property));
        }
        return this;
    }

    @Override
    public void serialize(Stream<?> stream, JsonGenerator jgen, SerializerProvider provider) throws IOException
    {
        try(Stream<?> s = stream) {
            jgen.writeStartArray();
            
            s.forEachOrdered(elem -> {
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
            
            jgen.writeEndArray();
        } catch (WrappedIOException e) {
            throw e.getCause();
        }
    }
}
