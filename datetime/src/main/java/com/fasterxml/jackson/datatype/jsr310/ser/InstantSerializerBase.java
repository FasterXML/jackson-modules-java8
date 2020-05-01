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
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

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
 * Base class for serializers used for {@link java.time.Instant}.
 */
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
            DateTimeFormatter dtf,
            Boolean useTimestamp,  Boolean useNanoseconds)
    {
        super(base, dtf, useTimestamp, useNanoseconds, null);
        defaultFormat = base.defaultFormat;
        getEpochMillis = base.getEpochMillis;
        getEpochSeconds = base.getEpochSeconds;
        getNanoseconds = base.getNanoseconds;
    }

    @Override
    protected abstract JSR310FormattedSerializerBase<?> withFormat(DateTimeFormatter dtf,
        Boolean useTimestamp,
        JsonFormat.Shape shape);

    @Override
    public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
             if (withoutFraction(provider)) {
                generator.writeNumber(getEpochMillis.applyAsLong(value) / 1000);
                return;
            } else if (useNanoseconds(provider)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(
                        getEpochSeconds.applyAsLong(value), getNanoseconds.applyAsInt(value)
                ));
                return;
            }
        generator.writeNumber(getEpochMillis.applyAsLong(value));
            return;
        }
        String str;
        
        if (_formatter != null) {
            str = _formatter.format(value);;
        } else if (defaultFormat != null) {
            str = defaultFormat.format(value);;
        } else {
            str = value.toString();
        }
        generator.writeString(str);
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
}
