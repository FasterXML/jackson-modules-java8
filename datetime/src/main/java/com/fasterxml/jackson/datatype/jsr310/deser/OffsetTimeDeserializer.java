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
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

/**
 * Deserializer for Java 8 temporal {@link OffsetTime}s.
 *
 * @author Nick Williams
 */
public class OffsetTimeDeserializer extends JSR310DateTimeDeserializerBase<OffsetTime>
{
    private static final long serialVersionUID = 1L;

    public static final OffsetTimeDeserializer INSTANCE = new OffsetTimeDeserializer();

    private OffsetTimeDeserializer() {
        this(DateTimeFormatter.ISO_OFFSET_TIME);
    }

    protected OffsetTimeDeserializer(DateTimeFormatter dtf) {
        super(OffsetTime.class, dtf);
    }

    @Override
    protected OffsetTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        return new OffsetTimeDeserializer(dtf);
    }

    // !!! TODO: lenient vs strict?
    @Override
    protected OffsetTimeDeserializer withLeniency(Boolean leniency) {
        return this;
    }

    @Override
    public OffsetTime deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }
            try {
                return OffsetTime.parse(string, _formatter);
            } catch (DateTimeException e) {
                return _handleDateTimeException(context, e, string);
            }
        }
        if (!parser.isExpectedStartArrayToken()) {
            if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
                return (OffsetTime) parser.getEmbeddedObject();
            }
            if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                _throwNoNumericTimestampNeedTimeZone(parser, context);
            }
            throw context.wrongTokenException(parser, handledType(), JsonToken.START_ARRAY,
                    "Expected array or string.");
        }
        JsonToken t = parser.nextToken();
        if (t != JsonToken.VALUE_NUMBER_INT) {
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                    && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final OffsetTime parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;            
            }
            context.reportInputMismatch(handledType(),
                    "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                    t);
        }
        int hour = parser.getIntValue();
        int minute = parser.nextIntValue(-1);
        if (minute == -1) {
            t = parser.getCurrentToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if (t != JsonToken.VALUE_NUMBER_INT) {
                _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "minutes");
            }
            minute = parser.getIntValue();
        }
        int partialSecond = 0;
        int second = 0;
        if (parser.nextToken() == JsonToken.VALUE_NUMBER_INT) {
            second = parser.getIntValue();
            if (parser.nextToken() == JsonToken.VALUE_NUMBER_INT) {
                partialSecond = parser.getIntValue();
                if (partialSecond < 1_000 &&
                        !context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                    partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds
                }
                parser.nextToken();
            }
        }
        if (parser.getCurrentToken() == JsonToken.VALUE_STRING) {
            OffsetTime result = OffsetTime.of(hour, minute, second, partialSecond, ZoneOffset.of(parser.getText()));
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                _reportWrongToken(context, JsonToken.END_ARRAY, "timezone");
            }
            return result;
        }
        throw context.wrongTokenException(parser, handledType(), JsonToken.VALUE_STRING,
                "Expected string for TimeZone after numeric values");
    }
}
