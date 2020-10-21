/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.ser;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * Base class that provides an array schema instead of scalar schema if
 * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} is enabled.
 *
 * @author Nick Williams
 * @since 2.2
 */
abstract class JSR310FormattedSerializerBase<T>
    extends JSR310SerializerBase<T>
    implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    /**
     * Flag that indicates that serialization must be done as the
     * Java timestamp, regardless of other settings.
     */
    protected final Boolean _useTimestamp;

    /**
     * Flag that indicates that numeric timestamp values must be written using
     * nanosecond timestamps if the datatype supports such resolution,
     * regardless of other settings.
     */
    protected final Boolean _useNanoseconds;

    /**
     * Specific format to use, if not default format: non null value
     * also indicates that serialization is to be done as JSON String,
     * not numeric timestamp, unless {@code #_useTimestamp} is true.
     */
    protected final DateTimeFormatter _formatter;

    protected final JsonFormat.Shape _shape;

    /**
     * Lazily constructed {@code JavaType} representing type
     * {@code List<Integer>}.
     *
     * @since 2.10
     */
    protected transient volatile JavaType _integerListType;
    
    protected JSR310FormattedSerializerBase(Class<T> supportedType) {
        this(supportedType, null);
    }

    protected JSR310FormattedSerializerBase(Class<T> supportedType,
            DateTimeFormatter formatter) {
        super(supportedType);
        _useTimestamp = null;
        _useNanoseconds = null;
        _shape = null;
        _formatter = formatter;
    }
    
    protected JSR310FormattedSerializerBase(JSR310FormattedSerializerBase<?> base,
            Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape)
    {
        this(base, useTimestamp, null, dtf, shape);
    }

    protected JSR310FormattedSerializerBase(JSR310FormattedSerializerBase<?> base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf,
            JsonFormat.Shape shape)
    {
        super(base.handledType());
        _useTimestamp = useTimestamp;
        _useNanoseconds = useNanoseconds;
        _formatter = dtf;
        _shape = shape;
    }

    protected abstract JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp,
            DateTimeFormatter dtf, JsonFormat.Shape shape);

    /**
     * @since 2.8
     */
    @Deprecated // since 2.9.5
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId) {
        // 01-Jul-2016, tatu: Sub-classes need to override
        return this;
    }

    /**
     * @since 2.9.5
     */
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId,
            Boolean writeNanoseconds) {
        return this;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null) {
            Boolean useTimestamp = null;

           // Simple case first: serialize as numeric timestamp?
            JsonFormat.Shape shape = format.getShape();
            if (shape == JsonFormat.Shape.ARRAY || shape.isNumeric() ) {
                useTimestamp = Boolean.TRUE;
            } else {
                useTimestamp = (shape == JsonFormat.Shape.STRING) ? Boolean.FALSE : null;
            }
            DateTimeFormatter dtf = _formatter;

            // If not, do we have a pattern?
            if (format.hasPattern()) {
                dtf = _useDateTimeFormatter(prov, format);
            }
            JSR310FormattedSerializerBase<?> ser = this;
            if ((shape != _shape) || (useTimestamp != _useTimestamp) || (dtf != _formatter)) {
                ser = ser.withFormat(useTimestamp, dtf, shape);
            }
            Boolean writeZoneId = format.getFeature(JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID);
            Boolean writeNanoseconds = format.getFeature(JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
            if ((writeZoneId != null) || (writeNanoseconds != null)) {
                ser = ser.withFeatures(writeZoneId, writeNanoseconds);
            }
            return ser;
        }
        return this;
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint)
    {
        return createSchemaNode(
            provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) ? "array" : "string", true
        );
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        if (useTimestamp(visitor.getProvider())) {
            _acceptTimestampVisitor(visitor, typeHint);
        } else {
            JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
            if (v2 != null) {
                v2.format(JsonValueFormat.DATE_TIME);
            }
        }
    }

    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        // By default, most sub-types use JSON Array, so do this:
        // 28-May-2019, tatu: serialized as a List<Integer>, presumably
        JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(_integerListType(visitor.getProvider()));
        if (v2 != null) {
            v2.itemsFormat(JsonFormatTypes.INTEGER);
        }
    }

    protected JavaType _integerListType(SerializerProvider prov) {
        JavaType t = _integerListType;
        if (t == null) {
            t = prov.getTypeFactory()
                    .constructCollectionType(List.class, Integer.class);
            _integerListType = t;
        }
        return t;
    }

    /**
     * Overridable method that determines {@link SerializationFeature} that is used as
     * the global default in determining if date/time value serialized should use numeric
     * format ("timestamp") or not.
     *<p>
     * Note that this feature is just the baseline setting and may be overridden on per-type
     * or per-property basis.
     *
     * @since 2.10
     */
    protected SerializationFeature getTimestampsFeature() {
        return SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
    }

    protected boolean useTimestamp(SerializerProvider provider) {
        if (_useTimestamp != null) {
            return _useTimestamp.booleanValue();
        }
        if (_shape != null) {
            if (_shape == Shape.STRING) {
                return false;
            }
            if (_shape == Shape.NUMBER_INT) {
                return true;
            }
        }
        // assume that explicit formatter definition implies use of textual format
        return (_formatter == null) && (provider != null)
                && provider.isEnabled(getTimestampsFeature());
    }

    protected boolean _useTimestampExplicitOnly(SerializerProvider provider) {
        if (_useTimestamp != null) {
            return _useTimestamp.booleanValue();
        }
        return false;
    }

    protected boolean useNanoseconds(SerializerProvider provider) {
        if (_useNanoseconds != null) {
            return _useNanoseconds.booleanValue();
        }
        if (_shape != null) {
            if (_shape == Shape.NUMBER_INT) {
                return false;
            }
            if (_shape == Shape.NUMBER_FLOAT) {
                return true;
            }
        }
        return (provider != null)
                && provider.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    // modules-java8#189: to be overridden by other formatters using this as base class
    protected DateTimeFormatter _useDateTimeFormatter(SerializerProvider prov, JsonFormat.Value format) {
        DateTimeFormatter dtf;
        final String pattern = format.getPattern();
        final Locale locale = format.hasLocale() ? format.getLocale() : prov.getLocale();
        if (locale == null) {
            dtf = DateTimeFormatter.ofPattern(pattern);
        } else {
            dtf = DateTimeFormatter.ofPattern(pattern, locale);
        }
        //Issue #69: For instant serializers/deserializers we need to configure the formatter with
        //a time zone picked up from JsonFormat annotation, otherwise serialization might not work
        if (format.hasTimeZone()) {
            dtf = dtf.withZone(format.getTimeZone().toZoneId());
        }
        return dtf;
    }
}
