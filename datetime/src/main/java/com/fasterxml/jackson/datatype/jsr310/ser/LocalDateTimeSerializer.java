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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * Serializer for Java 8 temporal {@link LocalDateTime}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class LocalDateTimeSerializer extends JSR310FormattedSerializerBase<LocalDateTime>
{
    private static final long serialVersionUID = 1L;

    public static final LocalDateTimeSerializer INSTANCE = new LocalDateTimeSerializer();
    
    protected LocalDateTimeSerializer() {
        this(null);
    }

    public LocalDateTimeSerializer(DateTimeFormatter f) {
        super(LocalDateTime.class, f);
    }

    private LocalDateTimeSerializer(LocalDateTimeSerializer base, Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter f) {
        super(base, useTimestamp, useNanoseconds, f, null);
    }

    @Override
    protected JSR310FormattedSerializerBase<LocalDateTime> withFormat(Boolean useTimestamp, DateTimeFormatter f, JsonFormat.Shape shape) {
        return new LocalDateTimeSerializer(this, useTimestamp, _useNanoseconds, f);
    }

    protected DateTimeFormatter _defaultFormatter() {
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        if (useTimestamp(provider)) {
            g.writeStartArray();
            _serializeAsArrayContents(value, g, provider);
            g.writeEndArray();
        } else {
            DateTimeFormatter dtf = _formatter;
            if (dtf == null) {
                dtf = _defaultFormatter();
            }
            g.writeString(value.format(dtf));
        }
    }

    @Override
    public void serializeWithType(LocalDateTime value, JsonGenerator g, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, serializationShape(provider)));
        // need to write out to avoid double-writing array markers
        if (typeIdDef.valueShape == JsonToken.START_ARRAY) {
            _serializeAsArrayContents(value, g, provider);
        } else {
            DateTimeFormatter dtf = _formatter;
            if (dtf == null) {
                dtf = _defaultFormatter();
            }
            g.writeString(value.format(dtf));
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    private final void _serializeAsArrayContents(LocalDateTime value, JsonGenerator g,
            SerializerProvider provider) throws IOException
    {
        g.writeNumber(value.getYear());
        g.writeNumber(value.getMonthValue());
        g.writeNumber(value.getDayOfMonth());
        g.writeNumber(value.getHour());
        g.writeNumber(value.getMinute());
        final int secs = value.getSecond();
        final int nanos = value.getNano();
        if ((secs > 0) || (nanos > 0)) {
            g.writeNumber(secs);
            if (nanos > 0) {
                if (useNanoseconds(provider)) {
                    g.writeNumber(nanos);
                } else {
                    g.writeNumber(value.get(ChronoField.MILLI_OF_SECOND));
                }
            }
        }
    }

    @Override // since 2.9
    protected JsonToken serializationShape(SerializerProvider provider) {
        return useTimestamp(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new LocalDateTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter);
    }
}
