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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializer for Java 8 temporal {@link LocalDateTime}s.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
public class LocalDateTimeDeserializer
    extends JSR310DateTimeDeserializerBase<LocalDateTime>
{
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

    private LocalDateTimeDeserializer() {
        this(DEFAULT_FORMATTER);
    }

    public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
    }

    @Override
    protected JsonDeserializer<LocalDateTime> withDateFormat(DateTimeFormatter formatter) {
        return new LocalDateTimeDeserializer(formatter);
    }

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        if (parser.hasTokenId(JsonTokenId.ID_STRING)) {
            String string = parser.getText().trim();
            if (string.length() == 0) {
                return null;
            }

            try {
	            if (_formatter == DEFAULT_FORMATTER) {
	                // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
	                if (string.length() > 10 && string.charAt(10) == 'T') {
	                   if (string.endsWith("Z")) {
	                       return LocalDateTime.ofInstant(Instant.parse(string), ZoneOffset.UTC);
	                   } else {
	                       return LocalDateTime.parse(string, DEFAULT_FORMATTER);
	                   }
	                }
	            }

                return LocalDateTime.parse(string, _formatter);
            } catch (DateTimeException e) {
                _rethrowDateTimeException(parser, context, e, string);
            }
        }
        if (parser.isExpectedStartArrayToken()) {
            if (parser.nextToken() == JsonToken.END_ARRAY) {
                return null;
            }
            int year = parser.getIntValue();
            int month = parser.nextIntValue(-1);
            int day = parser.nextIntValue(-1);
            int hour = parser.nextIntValue(-1);
            int minute = parser.nextIntValue(-1);

            if (parser.nextToken() != JsonToken.END_ARRAY) {
            	int second = parser.getIntValue();

            	if (parser.nextToken() != JsonToken.END_ARRAY) {
            		int partialSecond = parser.getIntValue();
            		if(partialSecond < 1_000 &&
            				!context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS))
            			partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds

            		if(parser.nextToken() != JsonToken.END_ARRAY) {
            			throw context.wrongTokenException(parser, JsonToken.END_ARRAY, "Expected array to end.");
            		}
            		return LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
            	}
            	return LocalDateTime.of(year, month, day, hour, minute, second);
            }
            return LocalDateTime.of(year, month, day, hour, minute);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime) parser.getEmbeddedObject();
        }
        throw context.wrongTokenException(parser, JsonToken.VALUE_STRING, "Expected array or string.");
    }
}
