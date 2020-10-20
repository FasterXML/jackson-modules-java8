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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * Deserializer for Java 8 temporal {@link Duration}s.
 *
 * @author Nick Williams
 * @since 2.2.0
 */
public class DurationDeserializer extends JSR310DeserializerBase<Duration>
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    public static final DurationDeserializer INSTANCE = new DurationDeserializer();

    /**
     * Since 2.12
     * When set, integer values will be deserialized using the specified unit. Using this parser will tipically
     * override the value specified in {@link DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS} as it is
     * considered that the unit set in {@link JsonFormat#pattern()} has precedence since is more specific.
     *
     * @see [jackson-modules-java8#184] for more info
     */
    private DurationUnitParser _durationUnitParser;

    private DurationDeserializer() {
        super(Duration.class);
    }

    /**
     * Since 2.11
     */
    protected DurationDeserializer(DurationDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    protected DurationDeserializer(DurationDeserializer base, DurationUnitParser durationUnitParser) {
        super(base, base._isLenient);
        _durationUnitParser = durationUnitParser;
    }

    @Override
    protected DurationDeserializer withLeniency(Boolean leniency) {
        return new DurationDeserializer(this, leniency);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
                                                BeanProperty property) throws JsonMappingException
    {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        DurationDeserializer deser = this;
        if (format != null) {
            if (format.hasLenient()) {
                Boolean leniency = format.getLenient();
                if (leniency != null) {
                    deser = deser.withLeniency(leniency);
                }
            }
            if (format.hasPattern()) {
                deser = DurationUnitParser.from(format.getPattern())
                        .map(deser::withPattern)
                        .orElse(deser);
            }
        }
        return deser;
    }

    private DurationDeserializer withPattern(DurationUnitParser pattern) {
        return new DurationDeserializer(this, pattern);
    }

    @Override
    public Duration deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        switch (parser.currentTokenId())
        {
            case JsonTokenId.ID_NUMBER_FLOAT:
                BigDecimal value = parser.getDecimalValue();
                return DecimalUtils.extractSecondsAndNanos(value, Duration::ofSeconds);
            case JsonTokenId.ID_NUMBER_INT:
                long intValue = parser.getLongValue();
                if (_durationUnitParser != null) {
                    return _durationUnitParser.parse(intValue);
                }
                return _fromTimestamp(context, intValue);
            case JsonTokenId.ID_STRING:
                return _fromString(parser, context, parser.getText());
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            case JsonTokenId.ID_START_OBJECT:
                return _fromString(parser, context,
                        context.extractScalarFromObject(parser, this, handledType()));
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

    protected Duration _fromString(JsonParser parser, DeserializationContext ctxt,
            String value)  throws IOException
    {
        value = value.trim();
        if (value.length() == 0) {
            if (!isLenient()) {
                return _failForNotLenient(parser, ctxt, JsonToken.VALUE_STRING);
            }
            return null;
        }
        // 30-Sep-2020: Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isValidTimestampString(value)) {
            return _fromTimestamp(ctxt, NumberInput.parseLong(value));
        }

        try {
            return Duration.parse(value);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, e, value);
        }
    }

    protected Duration _fromTimestamp(DeserializationContext ctxt, long ts) {
        if (ctxt.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            return Duration.ofSeconds(ts);
        }
        return Duration.ofMillis(ts);
    }

    protected static class DurationUnitParser {
        final static Set<ChronoUnit> PARSEABLE_UNITS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                ChronoUnit.NANOS,
                ChronoUnit.MICROS,
                ChronoUnit.MILLIS,
                ChronoUnit.SECONDS,
                ChronoUnit.MINUTES,
                ChronoUnit.HOURS,
                ChronoUnit.HALF_DAYS,
                ChronoUnit.DAYS
        )));
        final TemporalUnit unit;

        DurationUnitParser(TemporalUnit unit) {
            this.unit = unit;
        }

        Duration parse(long value) {
            return Duration.of(value, unit);
        }

        static Optional<DurationUnitParser> from(String unit) {
            return PARSEABLE_UNITS.stream()
                    .filter(u -> u.name().equals(unit))
                    .map(DurationUnitParser::new)
                    .findFirst();
        }
    }
}
