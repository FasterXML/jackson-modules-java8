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
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.util.DurationUnitConverter;

/**
 * Deserializer for Java 8 temporal {@link Duration}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class DurationDeserializer extends JSR310DeserializerBase<Duration>
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    public static final DurationDeserializer INSTANCE = new DurationDeserializer();

    /**
     * When defined (not {@code null}) integer values will be converted into duration
     * unit configured for the converter.
     * Using this converter will typically override the value specified in
     * {@link DeserializationFeature#READ_DATE_TIMESTAMPS_AS_NANOSECONDS} as it is
     * considered that the unit set in {@link JsonFormat#pattern()} has precedence
     * since it is more specific.
     *<p>
     * See [jackson-modules-java8#184] for more info.
     *
     * @since 2.12
     */
    protected final DurationUnitConverter _durationUnitConverter;

    public DurationDeserializer() {
        super(Duration.class);
        _durationUnitConverter = null;
    }

    /**
     * @since 2.11
     */
    protected DurationDeserializer(DurationDeserializer base, Boolean leniency) {
        super(base, leniency);
        _durationUnitConverter = base._durationUnitConverter;
    }

    /**
     * @since 2.12
     */
    protected DurationDeserializer(DurationDeserializer base, DurationUnitConverter converter) {
        super(base, base._isLenient);
        _durationUnitConverter = converter;
    }

    @Override
    protected DurationDeserializer withLeniency(Boolean leniency) {
        return new DurationDeserializer(this, leniency);
    }

    protected DurationDeserializer withConverter(DurationUnitConverter converter) {
        return new DurationDeserializer(this, converter);
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
                final String pattern = format.getPattern();
                DurationUnitConverter p = DurationUnitConverter.from(pattern);
                if (p == null) {
                    ctxt.reportBadDefinition(getValueType(ctxt),
                            String.format(
                                    "Bad 'pattern' definition (\"%s\") for `Duration`: expected one of [%s]",
                                    pattern, DurationUnitConverter.descForAllowed()));
                }
                deser = deser.withConverter(p);
            }
        }
        return deser;
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
                return _fromTimestamp(context, parser.getLongValue());
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
            String value0)  throws IOException
    {
        String value = value0.trim();
        if (value.length() == 0) {
            // 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
            //   b/w empty and blank); for now don't which will allow blanks to be
            //   handled like "regular" empty (same as pre-2.12)
            return _fromEmptyString(parser, ctxt, value);
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
        if (_durationUnitConverter != null) {
            return _durationUnitConverter.convert(ts);
        }
        // 20-Oct-2020, tatu: This makes absolutely no sense but... somehow
        //   became the default handling.
        if (ctxt.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)) {
            return Duration.ofSeconds(ts);
        }
        return Duration.ofMillis(ts);
    }
}
