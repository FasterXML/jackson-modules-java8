package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.std.ReferenceTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

final class OptionalDeserializer
    extends ReferenceTypeDeserializer<Optional<?>>
{
    private static final long serialVersionUID = 1L;

    protected final boolean _cfgReadAbsentAsNull;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    /**
     * @since 2.14
     */
    public OptionalDeserializer(JavaType fullType, ValueInstantiator inst,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser,
            boolean cfgReadAbsentAsNull)
    {
        super(fullType, inst, typeDeser, deser);
        _cfgReadAbsentAsNull = cfgReadAbsentAsNull;
    }

    /**
     * @since 2.9
     * @deprecated Since 2.14
     */
    @Deprecated // @since 2.14
    public OptionalDeserializer(JavaType fullType, ValueInstantiator inst,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser)
    {
        this(fullType, inst, typeDeser, deser,
                Jdk8Module.DEFAULT_READ_ABSENT_AS_NULL);
    }

    /*
    /**********************************************************
    /* Abstract method implementations
    /**********************************************************
     */

    @Override
    public OptionalDeserializer withResolved(TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new OptionalDeserializer(_fullType, _valueInstantiator,
                typeDeser, valueDeser, _cfgReadAbsentAsNull);
    }

    @Override
    public Optional<?> getNullValue(DeserializationContext ctxt) throws JsonMappingException {
        // 07-May-2019, tatu: [databind#2303], needed for nested ReferenceTypes
        return Optional.ofNullable(_valueDeserializer.getNullValue(ctxt));
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        // 07-May-2019, tatu: I _think_ this needs to align with "null value" and
        //    not necessarily with empty value of contents? (used to just do "absent"
        //    so either way this seems to me like an improvement)
        return getNullValue(ctxt);
    }

    /**
     * As of Jackson 2.14 we will either return either same as
     * {@link #getNullValue} or {@code null}: see
     * {@like Jdk8Module#configureReadAbsentLikeNull(boolean)} for
     * details.
     */
    @Override // @since 2.14
    public Object getAbsentValue(DeserializationContext ctxt) throws JsonMappingException {
        // Note: actual `null` vs "null value" (which is coerced as "empty")
        if (_cfgReadAbsentAsNull) {
            return null;
        }
        return getNullValue(ctxt);
    }

    @Override
    public Optional<?> referenceValue(Object contents) {
        return Optional.ofNullable(contents);
    }

    @Override
    public Object getReferenced(Optional<?> reference) {
        // 23-Apr-2021, tatu: [modules-java8#214] Need to support empty
        //    for merging too
        return reference.orElse(null);
    }

    @Override // since 2.9
    public Optional<?> updateReference(Optional<?> reference, Object contents) {
        return Optional.ofNullable(contents);
    }

    // Default ought to be fine:
//    public Boolean supportsUpdate(DeserializationConfig config) { }

}
