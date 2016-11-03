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
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;

/**
 * Serializer for Java 8 temporal {@link MonthDay}s.
 *<p>
 * NOTE: unlike many other date/time type serializers, this serializer will only
 * use Array notation if explicitly instructed to do so with <code>JsonFormat</code>
 * (either directly or through per-type defaults) and NOT with global defaults.
 *
 * @since 2.7.1
 */
public class MonthDaySerializer extends JSR310FormattedSerializerBase<MonthDay>
{
    private static final long serialVersionUID = 1L;

    public static final MonthDaySerializer INSTANCE = new MonthDaySerializer();

    private MonthDaySerializer() {
        this(null);
    }

    public MonthDaySerializer(DateTimeFormatter formatter) {
        super(MonthDay.class, formatter);
    }

    private MonthDaySerializer(MonthDaySerializer base, Boolean useTimestamp, DateTimeFormatter formatter) {
        super(base, useTimestamp, formatter);
    }

    @Override
    protected MonthDaySerializer withFormat(Boolean useTimestamp, DateTimeFormatter formatter) {
        return new MonthDaySerializer(this, useTimestamp, formatter);
    }

    @Override
    public void serialize(MonthDay value, JsonGenerator generator, SerializerProvider provider) throws IOException
    {
        if (_useTimestampExplicitOnly(provider)) {
            generator.writeStartArray();
            generator.writeNumber(value.getMonthValue());
            generator.writeNumber(value.getDayOfMonth());
            generator.writeEndArray();
        } else {
            String str = (_formatter == null) ? value.toString() : value.format(_formatter);
            generator.writeString(str);
        }
    }

    @Override
    protected void _acceptTimestampVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        SerializerProvider provider = visitor.getProvider();
        boolean useTimestamp = (provider != null) && _useTimestampExplicitOnly(provider);
        if (useTimestamp) {
            _acceptTimestampVisitor(visitor, typeHint);
        } else {
            JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
            if (v2 != null) {
                v2.format(JsonValueFormat.DATE_TIME);
            }
        }
    }
}
