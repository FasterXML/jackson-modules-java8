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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;
import java.time.format.DateTimeFormatter;

/**
 * Deserializer for Java 8 temporal {@link Year}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class YearDeserializer extends JSR310DateTimeDeserializerBase<Year>
{
    private static final long serialVersionUID = 1L;

    public static final YearDeserializer INSTANCE = new YearDeserializer();

    private YearDeserializer()
    {
        this(null);
    }

    public YearDeserializer(DateTimeFormatter formatter) {
        super(Year.class, formatter);
    }

    @Override
    protected JsonDeserializer<Year> withDateFormat(DateTimeFormatter dtf) {
        return new YearDeserializer(dtf);
    }

    @Override
    public Year deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        JsonToken t = parser.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String string = parser.getValueAsString().trim();
            try {
                if (_formatter == null) {
                    return Year.parse(string);
                }
                return Year.parse(string, _formatter);
            } catch (DateTimeException e) {
                return _handleDateTimeException(context, e, string);
            }
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return Year.of(parser.getIntValue());
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return (Year) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.START_ARRAY)){
            return _deserializeFromArray(parser, context);
        }
        return _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT);
    }
}
