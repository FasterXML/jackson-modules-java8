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

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser.NumberType;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonNumberFormatVisitor;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

/**
 * Base class for serializers used for {@link java.time.Instant} and
 * other {@link Temporal} subtypes.
 */
@SuppressWarnings("serial")
public abstract class InstantSerializerBase<T extends Temporal>
    extends JSR310FormattedSerializerBase<T>
{
    private final DateTimeFormatter defaultFormat;

    private final ToLongFunction<T> getEpochMillis;

    private final ToLongFunction<T> getEpochSeconds;

    private final ToIntFunction<T> getNanoseconds;

    protected InstantSerializerBase(Class<T> supportedType, ToLongFunction<T> getEpochMillis,
            ToLongFunction<T> getEpochSeconds, ToIntFunction<T> getNanoseconds,
            DateTimeFormatter formatter)
    {
        // Bit complicated, just because we actually want to "hide" default formatter,
        // so that it won't accidentally force use of textual presentation
        super(supportedType, null);
        defaultFormat = formatter;
        this.getEpochMillis = getEpochMillis;
        this.getEpochSeconds = getEpochSeconds;
        this.getNanoseconds = getNanoseconds;
    }

    protected InstantSerializerBase(InstantSerializerBase<T> base,
            Boolean useTimestamp, DateTimeFormatter dtf)
    {
        this(base, useTimestamp, null, dtf);
    }

    protected InstantSerializerBase(InstantSerializerBase<T> base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf)
    {
        super(base, useTimestamp, useNanoseconds, dtf, null);
        defaultFormat = base.defaultFormat;
        getEpochMillis = base.getEpochMillis;
        getEpochSeconds = base.getEpochSeconds;
        getNanoseconds = base.getNanoseconds;
    }

    @Override
    protected abstract JSR310FormattedSerializerBase<?> withFormat(
        Boolean useTimestamp,
        DateTimeFormatter dtf, JsonFormat.Shape shape);

    @Override
    public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(
                        getEpochSeconds.applyAsLong(value), getNanoseconds.applyAsInt(value)
                ));
                return;
            }
            generator.writeNumber(getEpochMillis.applyAsLong(value));
            return;
        }

        generator.writeString(formatValue(value, provider));
    }

    // Overridden to ensure that our timestamp handling is as expected
    @Override
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        if (useNanoseconds(visitor.getProvider())) {
            JsonNumberFormatVisitor v2 = visitor.expectNumberFormat(typeHint);
            if (v2 != null) {
                v2.numberType(NumberType.BIG_DECIMAL);
            }
        } else {
            JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
            if (v2 != null) {
                v2.numberType(NumberType.LONG);
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

    // @since 2.12
    protected String formatValue(T value, SerializerProvider provider)
    {
        DateTimeFormatter formatter = (_formatter != null) ? _formatter : defaultFormat;
        if (formatter != null) {
            if (formatter.getZone() == null) { // timezone set if annotated on property
                // If the user specified to use the context TimeZone explicitly, and the formatter provided doesn't contain a TZ
                // Then we use the TZ specified in the objectMapper
                if (provider.getConfig().hasExplicitTimeZone() && provider.isEnabled(WRITE_DATES_WITH_CONTEXT_TIME_ZONE)) {
                    formatter = formatter.withZone(provider.getTimeZone().toZoneId());
                }
            }
            return formatter.format(value);
        }

        return value.toString();
    }
}
