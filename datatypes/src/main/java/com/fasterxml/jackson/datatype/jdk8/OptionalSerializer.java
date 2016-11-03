package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class OptionalSerializer
    extends StdSerializer<Optional<?>>
    implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    /**
     * Declared type parameter for Optional.
     */
    protected final JavaType _referredType;

    protected final BeanProperty _property;

    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _valueSerializer;

    /**
     * To support unwrapped values of dynamic types, will need this:
     */
    protected final NameTransformer _unwrapper;

    /**
     * Further guidance on serialization-inclusion (or not), regarding
     * contained value (if any).
     *
     * @since 2.7
     */
    protected final JsonInclude.Include _contentInclusion;

    /**
     * If element type can not be statically determined, mapping from
     * runtime type to serializer is handled using this object
     *
     * @since 2.6
     */
    protected transient PropertySerializerMap _dynamicSerializers;

    /*
    /**********************************************************
    /* Constructors, factory methods
    /**********************************************************
     */

    @Deprecated // since 2.7
    public OptionalSerializer(JavaType type) {
        this((ReferenceType) type, null, null);
    }

    @SuppressWarnings("unchecked")
    protected OptionalSerializer(ReferenceType optionalType,
            TypeSerializer vts, JsonSerializer<?> valueSer)
    {
        super(optionalType);
        _referredType = optionalType.getReferencedType();
        _property = null;
        _valueTypeSerializer = vts;
        _valueSerializer = (JsonSerializer<Object>) valueSer;
        _unwrapper = null;
        _contentInclusion = null;
        _dynamicSerializers = PropertySerializerMap.emptyForProperties();
    }

    @SuppressWarnings("unchecked")
    protected OptionalSerializer(OptionalSerializer base, BeanProperty property,
            TypeSerializer vts, JsonSerializer<?> valueSer,
            NameTransformer unwrapper, JsonInclude.Include contentIncl)
    {
        super(base);
        _referredType = base._referredType;
        _dynamicSerializers = base._dynamicSerializers;
        _property = property;
        _valueTypeSerializer = vts;
        _valueSerializer = (JsonSerializer<Object>) valueSer;
        _unwrapper = unwrapper;
        if ((contentIncl == JsonInclude.Include.USE_DEFAULTS)
                || (contentIncl == JsonInclude.Include.ALWAYS)) {
            _contentInclusion = null;
        } else {
            _contentInclusion = contentIncl;
        }
    }

    @Override
    public JsonSerializer<Optional<?>> unwrappingSerializer(NameTransformer transformer) {
        JsonSerializer<Object> ser = _valueSerializer;
        if (ser != null) {
            ser = ser.unwrappingSerializer(transformer);
        }
        NameTransformer unwrapper = (_unwrapper == null) ? transformer
                : NameTransformer.chainedTransformer(transformer, _unwrapper);
        return withResolved(_property, _valueTypeSerializer, ser, unwrapper, _contentInclusion);
    }

    protected OptionalSerializer withResolved(BeanProperty prop,
            TypeSerializer vts, JsonSerializer<?> valueSer,
            NameTransformer unwrapper, JsonInclude.Include contentIncl)
    {
        if ((_property == prop) && (contentIncl == _contentInclusion)
                && (_valueTypeSerializer == vts) && (_valueSerializer == valueSer)
                && (_unwrapper == unwrapper)) {
            return this;
        }
        return new OptionalSerializer(this, prop, vts, valueSer, unwrapper, contentIncl);
    }

    /*
    /**********************************************************
    /* Contextualization (support for property annotations)
    /**********************************************************
     */

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider,
            BeanProperty property) throws JsonMappingException
    {
        TypeSerializer vts = _valueTypeSerializer;
        if (vts != null) {
            vts = vts.forProperty(property);
        }
        JsonSerializer<?> ser = findContentSerializer(provider, property);
        if (ser == null) {
            ser = _valueSerializer;
            if (ser == null) {
                // A few conditions needed to be able to fetch serializer here:
                if (_useStatic(provider, property, _referredType)) {
                    ser = _findSerializer(provider, _referredType, property);
                }
            } else {
                ser = provider.handlePrimaryContextualization(ser, property);
            }
        }
        // Also: may want to have more refined exclusion based on referenced value
        JsonInclude.Include contentIncl = _contentInclusion;
        if (property != null) {
            JsonInclude.Value incl = property.findPropertyInclusion(provider.getConfig(),
                    Optional.class);
            JsonInclude.Include newIncl = incl.getContentInclusion();
            if ((newIncl != contentIncl) && (newIncl != JsonInclude.Include.USE_DEFAULTS)) {
                contentIncl = newIncl;
            }
        }
        return withResolved(property, vts, ser, _unwrapper, contentIncl);
    }

    protected boolean _useStatic(SerializerProvider provider, BeanProperty property,
            JavaType referredType)
    {
        // First: no serializer for `Object.class`, must be dynamic
        if (referredType.isJavaLangObject()) {
            return false;
        }
        // but if type is final, might as well fetch
        if (referredType.isFinal()) { // or should we allow annotation override? (only if requested...)
            return true;
        }
        // also: if indicated by typing, should be considered static
        if (referredType.useStaticType()) {
            return true;
        }
        // if neither, maybe explicit annotation?
        AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        if ((intr != null) && (property != null)) {
            Annotated ann = property.getMember();
            if (ann != null) {
                JsonSerialize.Typing t = intr.findSerializationTyping(property.getMember());
                if (t == JsonSerialize.Typing.STATIC) {
                    return true;
                }
                if (t == JsonSerialize.Typing.DYNAMIC) {
                    return false;
                }
            }
        }
        // and finally, may be forced by global static typing (unlikely...)
        return provider.isEnabled(MapperFeature.USE_STATIC_TYPING);
    }

    /*
    /**********************************************************
    /* API overrides
    /**********************************************************
     */

    @Override
    public boolean isEmpty(SerializerProvider provider, Optional<?> value)
    {
        if ((value == null) || !value.isPresent()) {
            return true;
        }
        if (_contentInclusion == null) {
            return false;
        }
        Object contents = value.get();
        JsonSerializer<Object> ser = _valueSerializer;
        if (ser == null) {
            try {
                ser = _findCachedSerializer(provider, contents.getClass());
            } catch (JsonMappingException e) { // nasty but necessary
                throw new RuntimeJsonMappingException(e);
            }
        }
        return ser.isEmpty(provider, contents);
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return (_unwrapper != null);
    }

    /*
    /**********************************************************
    /* Serialization methods
    /**********************************************************
     */

    @Override
    public void serialize(Optional<?> opt, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        if (!opt.isPresent()) {
            // [datatype-jdk8#20]: can not write `null` if unwrapped
            if (_unwrapper == null) {
                provider.defaultSerializeNull(gen);
            }
            return;
        }
        Object value = opt.get();
        JsonSerializer<Object> ser = _valueSerializer;
        if (ser == null) {
            ser = _findCachedSerializer(provider, value.getClass());
        }
        if (_valueTypeSerializer != null) {
            ser.serializeWithType(value, gen, provider, _valueTypeSerializer);
        } else {
            ser.serialize(value, gen, provider);
        }
    }

    @Override
    public void serializeWithType(Optional<?> opt,
            JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        if (!opt.isPresent()) {
            // [datatype-jdk8#20]: can not write `null` if unwrapped
            if (_unwrapper == null) {
                provider.defaultSerializeNull(gen);
            }
            return;
        }
        // Otherwise apply type-prefix/suffix, then std serialize:
        typeSer.writeTypePrefixForScalar(opt, gen, Optional.class);
        serialize(opt, gen, provider);
        typeSer.writeTypeSuffixForScalar(opt, gen);
    }

    /*
    /**********************************************************
    /* Introspection support
    /**********************************************************
     */

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        JsonSerializer<?> ser = _valueSerializer;
        if (ser == null) {
            ser = _findSerializer(visitor.getProvider(), _referredType, _property);
            if (_unwrapper != null) {
                ser = ser.unwrappingSerializer(_unwrapper);
            }
        }
        ser.acceptJsonFormatVisitor(visitor, _referredType);
    }

    /*
    /**********************************************************
    /* Misc other
    /**********************************************************
     */

    /**
     * Helper method that encapsulates logic of retrieving and caching required
     * serializer.
     */
    private final JsonSerializer<Object> _findCachedSerializer(SerializerProvider provider,
            Class<?> type) throws JsonMappingException
    {
        JsonSerializer<Object> ser = _dynamicSerializers.serializerFor(type);
        if (ser == null) {
            ser = _findSerializer(provider, type, _property);
            if (_unwrapper != null) {
                ser = ser.unwrappingSerializer(_unwrapper);
            }
            _dynamicSerializers = _dynamicSerializers.newWith(type, ser);
        }
        return ser;
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider,
            Class<?> type, BeanProperty prop) throws JsonMappingException
    {
        return provider.findTypedValueSerializer(type, true, prop);
    }

    private final JsonSerializer<Object> _findSerializer(SerializerProvider provider,
        JavaType type, BeanProperty prop) throws JsonMappingException
    {
        return provider.findTypedValueSerializer(type, true, prop);
    }

    // !!! 22-Mar-2016, tatu: Method added in jackson-databind 2.7.4; may be used
    //    when we go 2.8
    protected JsonSerializer<?> findContentSerializer(SerializerProvider serializers,
            BeanProperty property)
        throws JsonMappingException
    {
        if (property != null) {
            // First: if we have a property, may have property-annotation overrides
            AnnotatedMember m = property.getMember();
            final AnnotationIntrospector intr = serializers.getAnnotationIntrospector();
            if (m != null) {
                Object serDef = intr.findContentSerializer(m);
                if (serDef != null) {
                    return serializers.serializerInstance(m, serDef);
                }
            }
        }
        return null;
    }
}
