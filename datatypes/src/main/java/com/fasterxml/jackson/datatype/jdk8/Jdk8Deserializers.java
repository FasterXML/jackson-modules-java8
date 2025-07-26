package com.fasterxml.jackson.datatype.jdk8;

import java.util.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.ReferenceType;

public class Jdk8Deserializers
    extends Deserializers.Base
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * @since 2.14
     */
    protected final boolean _cfgReadAbsentAsNull;

    public Jdk8Deserializers() {
        // for backwards compatibility
        this(Jdk8Module.DEFAULT_READ_ABSENT_AS_NULL);
    }

    public Jdk8Deserializers(boolean cfgReadAbsentAsNull) {
        _cfgReadAbsentAsNull = cfgReadAbsentAsNull;
    }

    @Override // since 2.7
    public JsonDeserializer<?> findReferenceDeserializer(ReferenceType refType,
            DeserializationConfig config, BeanDescription beanDesc,
            TypeDeserializer contentTypeDeserializer, JsonDeserializer<?> contentDeserializer)
    {
        if (refType.hasRawClass(Optional.class)) {
            return new OptionalDeserializer(refType, null,
                    contentTypeDeserializer,contentDeserializer,
                    _cfgReadAbsentAsNull);
        }
        // 21-Oct-2015, tatu: Should probably consider possibility of custom deserializer being
        //    added to property; if so, `contentDeserializer` would not be null.
        //    Room for future improvement
        
        if (refType.hasRawClass(OptionalInt.class)) {
            return OptionalIntDeserializer.INSTANCE;
        }
        if (refType.hasRawClass(OptionalLong.class)) {
            return OptionalLongDeserializer.INSTANCE;
        }
        if (refType.hasRawClass(OptionalDouble.class)) {
            return OptionalDoubleDeserializer.INSTANCE;
        }
        return null;
    }

    // @since 2.18.5 wrt [modules-java8#372]
    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type,
            DeserializationConfig config, BeanDescription beanDesc)
        throws JsonMappingException
    {
        // 25-Jul-2025, tatu: [modules-java8#372] - work-around for misconfigured
        //   `TypeFactory` that does not use `Jdk8TypeModifier`
        if (type.hasRawClass(Optional.class)) {
            // Not the cleanest but has to do: create properly resolved type;
            // pass `null` for `contentTypeDeserializer` and `contentDeserializer`
            // (to be resolver contextually)
            JavaType refType = config.constructType(Optional.class);
            return new OptionalDeserializer(refType, null, null, null,
                    _cfgReadAbsentAsNull);
        }
        return null;
    }
}
