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
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

/**
 * Serializer for Java 8 temporal {@link YearMonth}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class YearMonthSerializer extends JSR310FormattedSerializerBase<YearMonth>
{
    private static final long serialVersionUID = 1L;

    public static final YearMonthSerializer INSTANCE = new YearMonthSerializer();

    protected YearMonthSerializer() { // was private before 2.12
        this(null);
    }

    public YearMonthSerializer(DateTimeFormatter formatter) {
        super(YearMonth.class, formatter);
    }

    private YearMonthSerializer(YearMonthSerializer base, Boolean useTimestamp,
            DateTimeFormatter formatter) {
        super(base, useTimestamp, formatter, null);
    }

    @Override
    protected YearMonthSerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter,
            JsonFormat.Shape shape) {
        return new YearMonthSerializer(this, useTimestamp, formatter);
    }

    @Override
    public void serialize(YearMonth value, JsonGenerator g, SerializerProvider provider) throws IOException
    {
        if (useTimestamp(provider)) {
            g.writeStartArray();
            _serializeAsArrayContents(value, g, provider);
            g.writeEndArray();
            return;
        }
        g.writeString((_formatter == null) ? value.toString() : value.format(_formatter));
    }

    @Override
    public void serializeWithType(YearMonth value, JsonGenerator g,
            SerializerProvider provider, TypeSerializer typeSer) throws IOException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g,
                typeSer.typeId(value, serializationShape(provider)));
        // need to write out to avoid double-writing array markers
        if (typeIdDef.valueShape == JsonToken.START_ARRAY) {
            _serializeAsArrayContents(value, g, provider);
        } else {
            g.writeString((_formatter == null) ? value.toString() : value.format(_formatter));
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }
    
    protected void _serializeAsArrayContents(YearMonth value, JsonGenerator g,
            SerializerProvider provider) throws IOException
    {
        g.writeNumber(value.getYear());
        g.writeNumber(value.getMonthValue());
    }

    @Override
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        SerializerProvider provider = visitor.getProvider();
        boolean useTimestamp = (provider != null) && useTimestamp(provider);
        if (useTimestamp) {
            super._acceptTimestampVisitor(visitor, typeHint);
        } else {
            JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
            if (v2 != null) {
                v2.format(JsonValueFormat.DATE_TIME);
            }
        }
    }

    @Override // since 2.9
    protected JsonToken serializationShape(SerializerProvider provider) {
        return useTimestamp(provider) ? JsonToken.START_ARRAY : JsonToken.VALUE_STRING;
    }
}
