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
import com.fasterxml.jackson.datatype.jsr310.util.DurationUnitConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for Java 8 temporal {@link Duration}s.
 *<p>
 * NOTE: since 2.10, {@link SerializationFeature#WRITE_DURATIONS_AS_TIMESTAMPS}
 * determines global default used for determining if serialization should use
 * numeric (timestamps) or textual representation. Before this,
 * {@link SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} was used.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class DurationSerializer extends JSR310FormattedSerializerBase<Duration>
{
    private static final long serialVersionUID = 1L;

    public static final DurationSerializer INSTANCE = new DurationSerializer();

    /**
     * When defined (not {@code null}) duration values will be converted into integers
     * with the unit configured for the converter.
     * Only available when {@link SerializationFeature#WRITE_DURATIONS_AS_TIMESTAMPS} is enabled
     * and {@link SerializationFeature#WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS} is not enabled
     * since the duration converters do not support fractions
     * @since 2.12
     */
    private DurationUnitConverter _durationUnitConverter;

    protected DurationSerializer() { // was private before 2.12
        super(Duration.class);
    }

    protected DurationSerializer(DurationSerializer base,
            Boolean useTimestamp, DateTimeFormatter dtf) {
        super(base, useTimestamp, dtf, null);
    }

    protected DurationSerializer(DurationSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf) {
        super(base, useTimestamp, useNanoseconds, dtf, null);
    }

    protected DurationSerializer(DurationSerializer base, DurationUnitConverter converter) {
        super(base, base._useTimestamp, base._useNanoseconds, base._formatter, base._shape);
        _durationUnitConverter = converter;
    }

    @Override
    protected DurationSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new DurationSerializer(this, useTimestamp, dtf);
    }

    protected DurationSerializer withConverter(DurationUnitConverter converter) {
        return new DurationSerializer(this, converter);
    }

    // @since 2.10
    @Override
    protected SerializationFeature getTimestampsFeature() {
        return SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        DurationSerializer ser = (DurationSerializer) super.createContextual(prov, property);
        JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        if (format != null && format.hasPattern()) {
            final String pattern = format.getPattern();
            DurationUnitConverter p = DurationUnitConverter.from(pattern);
            if (p == null) {
                prov.reportBadDefinition(handledType(),
                        String.format(
                                "Bad 'pattern' definition (\"%s\") for `Duration`: expected one of [%s]",
                                pattern, DurationUnitConverter.descForAllowed()));
            }

            ser = ser.withConverter(p);
        }
        return ser;
    }

    @Override
    public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                generator.writeNumber(_toNanos(duration));
            } else {
                if (_durationUnitConverter != null) {
                    generator.writeNumber(_durationUnitConverter.convert(duration));
                } else {
                    generator.writeNumber(duration.toMillis());
                }
            }
        } else {
            generator.writeString(duration.toString());
        }
    }

    // 20-Oct-2020, tatu: [modules-java8#165] Need to take care of
    //    negative values too, and without work-around values
    //    returned are wonky wrt conversions
    private BigDecimal _toNanos(Duration duration) {
        BigDecimal bd;
        if (duration.isNegative()) {
            duration = duration.abs();
            bd = DecimalUtils.toBigDecimal(duration.getSeconds(),
                    duration.getNano())
                .negate();
        } else {
            bd = DecimalUtils.toBigDecimal(duration.getSeconds(),
                    duration.getNano());
        }
        return bd;
    }

    @Override
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
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

    @Override // since 2.9
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
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new DurationSerializer(this, _useTimestamp, writeNanoseconds, _formatter);
    }

    @Override
    protected DateTimeFormatter _useDateTimeFormatter(SerializerProvider prov, JsonFormat.Value format) {
        return null;
    }
}
