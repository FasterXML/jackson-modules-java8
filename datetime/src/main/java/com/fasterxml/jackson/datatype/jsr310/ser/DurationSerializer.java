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
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

/**
 * Serializer for Java 8 temporal {@link Duration}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class DurationSerializer extends JSR310FormattedSerializerBase<Duration>
{
    private static final long serialVersionUID = 1L;

    public static final DurationSerializer INSTANCE = new DurationSerializer();

    private DurationSerializer() {
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

    @Override
    protected DurationSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new DurationSerializer(this, useTimestamp, dtf);
    }
    
    @Override
    public void serialize(Duration duration, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            if (useNanoseconds(provider)) {
                generator.writeNumber(DecimalUtils.toBigDecimal(
                        duration.getSeconds(), duration.getNano()
                ));
            } else {
                generator.writeNumber(duration.toMillis());
            }
        } else {
            // Does not look like we can make any use of DateTimeFormatter here?
            generator.writeString(duration.toString());
        }
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
}
