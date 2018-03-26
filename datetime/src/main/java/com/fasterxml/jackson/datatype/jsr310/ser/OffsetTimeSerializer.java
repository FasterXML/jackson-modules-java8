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
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

/**
 * Serializer for Java 8 temporal {@link OffsetTime}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class OffsetTimeSerializer extends JSR310FormattedSerializerBase<OffsetTime>
{
    private static final long serialVersionUID = 1L;

    public static final OffsetTimeSerializer INSTANCE = new OffsetTimeSerializer();

    protected OffsetTimeSerializer() {
        super(OffsetTime.class);
    }

    protected OffsetTimeSerializer(OffsetTimeSerializer base,
            Boolean useTimestamp, DateTimeFormatter dtf) {
        this(base, useTimestamp, null, dtf);
    }

    protected OffsetTimeSerializer(OffsetTimeSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter dtf) {
        super(base, useTimestamp, useNanoseconds, dtf, null);
    }

    @Override
    protected OffsetTimeSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new OffsetTimeSerializer(this, useTimestamp, dtf);
    }

    @Override
    public void serialize(OffsetTime time, JsonGenerator g, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            g.writeStartArray();
            _serializeAsArrayContents(time, g, provider);
            g.writeEndArray();
        } else {
            String str = (_formatter == null) ? time.toString() : time.format(_formatter);
            g.writeString(str);
        }
    }

    @Override
    public void serializeWithType(OffsetTime value, JsonGenerator g, SerializerProvider provider,
            TypeSerializer typeSer) throws IOException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, serializationShape(provider)));
        // need to write out to avoid double-writing array markers
        if (typeIdDef.valueShape == JsonToken.START_ARRAY) {
            _serializeAsArrayContents(value, g, provider);
        } else {
            String str = (_formatter == null) ? value.toString() : value.format(_formatter);
            g.writeString(str);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    private final void _serializeAsArrayContents(OffsetTime value, JsonGenerator g,
            SerializerProvider provider) throws IOException
    {
        g.writeNumber(value.getHour());
        g.writeNumber(value.getMinute());
        final int secs = value.getSecond();
        final int nanos = value.getNano();
        if ((secs > 0) || (nanos > 0)) {
            g.writeNumber(secs);
            if (nanos > 0) {
                if(useNanoseconds(provider)) {
                    g.writeNumber(nanos);
                } else {
                    g.writeNumber(value.get(ChronoField.MILLI_OF_SECOND));
                }
            }
        }
        g.writeString(value.getOffset().toString());
    }
    
    @Override // since 2.9
    protected JsonToken serializationShape(SerializerProvider provider) {
        return useTimestamp(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new OffsetTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter);
    }
}
