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
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Duration;

/**
 * Deserializer for Java 8 temporal {@link Duration}s.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
public class DurationDeserializer extends JSR310DeserializerBase<Duration>
{
    private static final long serialVersionUID = 1L;

    public static final DurationDeserializer INSTANCE = new DurationDeserializer();

    private static final BigDecimal DURATION_MAX = new BigDecimal(
            Long.MAX_VALUE + "." + java.time.Instant.MAX.getNano());
    private static final BigDecimal DURATION_MIN = new BigDecimal(
            Long.MIN_VALUE + "." + java.time.Instant.MIN.getNano());

    private DurationDeserializer()
    {
        super(Duration.class);
    }

    @Override
    public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        switch (parser.getCurrentTokenId())
        {
            case JsonTokenId.ID_NUMBER_FLOAT:
                BigDecimal value = parser.getDecimalValue();
                // If the decimal isn't within the bounds of a duration, bail out
                if(value.compareTo(DURATION_MAX) > 0 ||
                        value.compareTo(DURATION_MIN) < 0) {
                    throw new DateTimeException(
                            "Instant exceeds minimum or maximum Duration");
                }

                long seconds = value.longValue();
                int nanoseconds = DecimalUtils.extractNanosecondDecimal(value, seconds);
                return Duration.ofSeconds(seconds, nanoseconds);

            case JsonTokenId.ID_NUMBER_INT:
                if(context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
                    return Duration.ofSeconds(parser.getLongValue());
                }
                return Duration.ofMillis(parser.getLongValue());

            case JsonTokenId.ID_STRING:
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    return null;
                }
                try {
                    return Duration.parse(string);
                } catch (DateTimeException e) {
                    return _handleDateTimeException(context, e, string);
                }
            case JsonTokenId.ID_EMBEDDED_OBJECT:
                // 20-Apr-2016, tatu: Related to [databind#1208], can try supporting embedded
                //    values quite easily
                return (Duration) parser.getEmbeddedObject();
                
            case JsonTokenId.ID_START_ARRAY:
            	return _deserializeFromArray(parser, context);
        }
        return _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING,
                JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT);
    }
}
