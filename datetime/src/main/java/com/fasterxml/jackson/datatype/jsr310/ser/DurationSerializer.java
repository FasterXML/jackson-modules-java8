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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for Java 8 temporal {@link Duration}s.
 * <p>
 * NOTE: since 2.10, {@link SerializationFeature#WRITE_DURATIONS_AS_TIMESTAMPS}
 * determines global default used for determining if serialization should use
 * numeric (timestamps) or textual representation. Before this,
 * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} was used.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class DurationSerializer extends JSR310FormattedSerializerBase<Duration> {
    private static final long serialVersionUID = 1L;

    protected String _formatPattern;

    public static final DurationSerializer INSTANCE = new DurationSerializer();

    private DurationSerializer() {
        super(Duration.class);
    }

    public DurationSerializer(String formatPattern) {
        this();
        _formatPattern = formatPattern;
    }

    protected DurationSerializer(DurationSerializer base, Boolean useTimestamp) {
        super(base, null, useTimestamp, null, null);
    }

    protected DurationSerializer(DurationSerializer base, Boolean useTimestamp,
                                 Boolean useNanoseconds) {
        super(base, null, useTimestamp, useNanoseconds, null);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
                                              BeanProperty property) throws JsonMappingException {
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

            String formatPattern = null;
            if (format.hasPattern()) {
                formatPattern = format.getPattern();
            }

            Boolean writeNanoseconds = format.getFeature(JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);

            DurationSerializer ser = new DurationSerializer();
            if ((shape != _shape) || (useTimestamp != _useTimestamp)) {
                ser = ser.withFormat(null, useTimestamp, shape);
            }
            if (writeNanoseconds != null) {
                ser = ser.withFeatures(null, writeNanoseconds);
            }
            if (formatPattern != null) {
                ser._formatPattern = formatPattern;
            }
            //DateTimeFormatter and ZoneId not acceptable for Duration class
            return ser;
        }
        return this;
    }

    @Override
    protected DurationSerializer withFormat(DateTimeFormatter dtf,
                                            Boolean useTimestamp, JsonFormat.Shape shape) {
        return new DurationSerializer(this, useTimestamp);
    }

    // @since 2.10
    @Override
    protected SerializationFeature getTimestampsFeature() {
        return SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;
    }

    @Override
    public void serialize(Duration duration, JsonGenerator generator,
                          SerializerProvider provider) throws IOException {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(
                        duration.getSeconds(), duration.getNano()
                ));
            } else {
                generator.writeNumber(duration.toMillis());
            }
        } else {
            if (_formatPattern == null) {
                generator.writeString(duration.toString());
            } else {
                generator.writeString(DurationFormatUtils.formatDuration(
                        duration.toMillis(), _formatPattern, true));
            }
        }
    }

    @Override
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws
            JsonMappingException {
        JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
        if (v2 != null) {
            v2.numberType(JsonParser.NumberType.LONG);
            SerializerProvider provider = visitor.getProvider();
            if ((provider != null) && useNanoseconds(provider)) {
                // big number, no more specific qualifier to use...
            } else { // otherwise good old Unix timestamp, in milliseconds
                v2.format(JsonValueFormat.UTC_MILLISEC);
            }
        }
    }

    @Override
    protected JsonToken serializationShape(SerializerProvider provider) {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                return JsonToken.VALUE_NUMBER_FLOAT;
            }
            return JsonToken.VALUE_NUMBER_INT;
        }
        return JsonToken.VALUE_STRING;
    }

    @Override
    protected DurationSerializer withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new DurationSerializer(this, _useTimestamp, writeNanoseconds);
    }
}
