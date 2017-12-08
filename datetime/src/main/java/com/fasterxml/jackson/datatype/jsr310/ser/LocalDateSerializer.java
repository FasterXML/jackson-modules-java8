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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * Serializer for Java 8 temporal {@link LocalDate}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class LocalDateSerializer extends JSR310FormattedSerializerBase<LocalDate>
{
    private static final long serialVersionUID = 1L;

    public static final LocalDateSerializer INSTANCE = new LocalDateSerializer();

    protected LocalDateSerializer() {
        super(LocalDate.class);
    }

    protected LocalDateSerializer(LocalDateSerializer base,
                                  Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        super(base, useTimestamp, dtf, shape);
    }

    public LocalDateSerializer(DateTimeFormatter formatter) {
        super(LocalDate.class, formatter);
    }

    @Override
    protected LocalDateSerializer withFormat(Boolean useTimestamp, DateTimeFormatter dtf, JsonFormat.Shape shape) {
        return new LocalDateSerializer(this, useTimestamp, dtf, shape);
    }

    @Override
    public void serialize(LocalDate date, JsonGenerator g, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            if (_shape == JsonFormat.Shape.NUMBER_INT) {
                g.writeNumber(date.toEpochDay());
            } else {
                g.writeStartArray();
                _serializeAsArrayContents(date, g, provider);
                g.writeEndArray();
            }
        } else {
            g.writeString((_formatter == null) ? date.toString() : date.format(_formatter));
        }
    }

    @Override
    public void serializeWithType(LocalDate value, JsonGenerator g,
            SerializerProvider provider, TypeSerializer typeSer) throws IOException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, serializationShape(provider)));
        // need to write out to avoid double-writing array markers
        switch (typeIdDef.valueShape) {
        case START_ARRAY:
            _serializeAsArrayContents(value, g, provider);
            break;
        case VALUE_NUMBER_INT:
            g.writeNumber(value.toEpochDay());
            break;
        default:
            g.writeString((_formatter == null) ? value.toString() : value.format(_formatter));
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    protected void _serializeAsArrayContents(LocalDate value, JsonGenerator g,
            SerializerProvider provider) throws IOException
    {
        g.writeNumber(value.getYear());
        g.writeNumber(value.getMonthValue());
        g.writeNumber(value.getDayOfMonth());
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        SerializerProvider provider = visitor.getProvider();
        boolean useTimestamp = (provider != null) && useTimestamp(provider);
        if (useTimestamp) {
            _acceptTimestampVisitor(visitor, typeHint);
        } else {
            JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
            if (v2 != null) {
                v2.format(JsonValueFormat.DATE);
            }
        }
    }

    @Override // since 2.9
    protected JsonToken serializationShape(SerializerProvider provider) {
        if (useTimestamp(provider)) {
            if (_shape == JsonFormat.Shape.NUMBER_INT) {
                return JsonToken.VALUE_NUMBER_INT;
            }
            return JsonToken.START_ARRAY;
        }
        return JsonToken.VALUE_STRING;
    }
}
