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

package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Deserializer for Java 8 temporal {@link YearMonth}s.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
public class YearMonthDeserializer extends JSR310DateTimeDeserializerBase<YearMonth>
{
    private static final long serialVersionUID = 1L;

    public static final YearMonthDeserializer INSTANCE = new YearMonthDeserializer();

    private YearMonthDeserializer()
    {
        this(DateTimeFormatter.ofPattern("uuuu-MM"));
    }
    
    public YearMonthDeserializer(DateTimeFormatter formatter)
    {
        super(YearMonth.class, formatter);
    }

    /**
     * Since 2.11
     */
    protected YearMonthDeserializer(YearMonthDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    @Override
    protected YearMonthDeserializer withDateFormat(DateTimeFormatter dtf)  {
        return new YearMonthDeserializer(dtf);
    }

    @Override
    protected YearMonthDeserializer withLeniency(Boolean leniency) {
        return new YearMonthDeserializer(this, leniency);
    }

    @Override
    protected YearMonthDeserializer withShape(JsonFormat.Shape shape) { return this; }

    @Override
    public YearMonth deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                if (!isLenient()) {
                    return _failForNotLenient(parser, context, JsonToken.VALUE_STRING);
                }
                return null;
            }
            try {
                return YearMonth.parse(string, _formatter);
            } catch (DateTimeException e) {
                return _handleDateTimeException(context, e, string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                    && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final YearMonth parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;            
            }
            if (t != JsonToken.VALUE_NUMBER_INT) {
                _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "years");
            }
            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            if (month == -1) {
                if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "months");
                }
                month = parser.getIntValue();
            }
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY,
                        "Expected array to end");
            }
            return YearMonth.of(year, month);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (YearMonth) parser.getEmbeddedObject();
        }
        return _handleUnexpectedToken(context, parser,
                JsonToken.VALUE_STRING, JsonToken.START_ARRAY);
    }
}
