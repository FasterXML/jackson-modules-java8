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
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;

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

    private final static boolean DEFAULT_USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING
        = JavaTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING.enabledByDefault();

    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static final LocalDateTimeDeserializer INSTANCE = new LocalDateTimeDeserializer();

    /**
     * Flag for <code>JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS</code>
     *
     * @since 2.16
     */
    protected final Boolean _readTimestampsAsNanosOverride;

    /**
     * Flag set from
     * {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature#USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING}
     * to determine whether the {@link java.util.TimeZone} of the
     * {@link com.fasterxml.jackson.databind.DeserializationContext} is used
     * when leniently deserializing from the UTC/ISO instant format.
     *
     * @since 2.19
     */
    protected final boolean _useTimeZoneForLenientDateParsing;

    protected LocalDateTimeDeserializer() { // was private before 2.12
        this(DEFAULT_FORMATTER);
    }

    public LocalDateTimeDeserializer(DateTimeFormatter formatter) {
        super(LocalDateTime.class, formatter);
        _readTimestampsAsNanosOverride = null;
        _useTimeZoneForLenientDateParsing = DEFAULT_USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING;
    }

    /**
     * Since 2.10
     */
    protected LocalDateTimeDeserializer(LocalDateTimeDeserializer base, Boolean leniency) {
        super(base, leniency);
        _readTimestampsAsNanosOverride = base._readTimestampsAsNanosOverride;
        _useTimeZoneForLenientDateParsing = base._useTimeZoneForLenientDateParsing;
    }

    /**
     * Since 2.16
     */
    protected LocalDateTimeDeserializer(LocalDateTimeDeserializer base,
        Boolean leniency,
        DateTimeFormatter formatter,
        JsonFormat.Shape shape,
        Boolean readTimestampsAsNanosOverride) {
        super(base, leniency, formatter, shape);
        _readTimestampsAsNanosOverride = readTimestampsAsNanosOverride;
        _useTimeZoneForLenientDateParsing = base._useTimeZoneForLenientDateParsing;
    }

    /**
     * Since 2.19
     */
    protected LocalDateTimeDeserializer(LocalDateTimeDeserializer base, JacksonFeatureSet<JavaTimeFeature> features) {
        super(LocalDateTime.class, base._formatter);
        _readTimestampsAsNanosOverride = base._readTimestampsAsNanosOverride;
        _useTimeZoneForLenientDateParsing = features.isEnabled(JavaTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING);
    }

    @Override
    protected LocalDateTimeDeserializer withDateFormat(DateTimeFormatter dtf) {
        return new LocalDateTimeDeserializer(this, _isLenient, dtf, _shape, _readTimestampsAsNanosOverride);
    }

    @Override
    protected LocalDateTimeDeserializer withLeniency(Boolean leniency) {
        return new LocalDateTimeDeserializer(this, leniency);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<?> _withFormatOverrides(DeserializationContext ctxt,
        BeanProperty property, JsonFormat.Value formatOverrides) {
        LocalDateTimeDeserializer deser = (LocalDateTimeDeserializer)
            super._withFormatOverrides(ctxt, property, formatOverrides);
        Boolean readTimestampsAsNanosOverride = formatOverrides.getFeature(
            JsonFormat.Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        if (!Objects.equals(readTimestampsAsNanosOverride, deser._readTimestampsAsNanosOverride)) {
            return new LocalDateTimeDeserializer(deser, deser._isLenient, deser._formatter,
                deser._shape, readTimestampsAsNanosOverride);
        }
        return deser;
    }

    /**
     * Since 2.19
     */
    public LocalDateTimeDeserializer withFeatures(JacksonFeatureSet<JavaTimeFeature> features) {
        if (_useTimeZoneForLenientDateParsing ==
                features.isEnabled(JavaTimeFeature.USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING)) {
            return this;
        }
        return new LocalDateTimeDeserializer(this, features);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        if (p.hasTokenId(JsonTokenId.ID_STRING)) {
            return _fromString(p, ctxt, p.getText());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            // 17-May-2025, tatu: [databind#4656] need to check for `null`
            String str = ctxt.extractScalarFromObject(p, this, handledType());
            if (str != null) {
                return _fromString(p, ctxt, str);
            }
            return _handleUnexpectedToken(ctxt, p, "Expected array or string");
        }
        if (p.isExpectedStartArrayToken()) {
            JsonToken t = p.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                    && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final LocalDateTime parsed = deserialize(p, ctxt);
                if (p.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(p, ctxt);
                }
                return parsed;            
            }
            if (t == JsonToken.VALUE_NUMBER_INT) {
                LocalDateTime result;

                int year = p.getIntValue();
                int month = p.nextIntValue(-1);
                int day = p.nextIntValue(-1);
                int hour = p.nextIntValue(-1);
                int minute = p.nextIntValue(-1);

                t = p.nextToken();
                if (t == JsonToken.END_ARRAY) {
                    result = LocalDateTime.of(year, month, day, hour, minute);
                } else {
                    int second = p.getIntValue();
                    t = p.nextToken();
                    if (t == JsonToken.END_ARRAY) {
                        result = LocalDateTime.of(year, month, day, hour, minute, second);
                    } else {
                        int partialSecond = p.getIntValue();
                        if (partialSecond < 1_000 && !shouldReadTimestampsAsNanoseconds(ctxt))
                            partialSecond *= 1_000_000; // value is milliseconds, convert it to nanoseconds
                        if (p.nextToken() != JsonToken.END_ARRAY) {
                            throw ctxt.wrongTokenException(p, handledType(), JsonToken.END_ARRAY,
                                    "Expected array to end");
                        }
                        result = LocalDateTime.of(year, month, day, hour, minute, second, partialSecond);
                    }
                }
                return result;
            }
            ctxt.reportInputMismatch(handledType(),
                    "Unexpected token (%s) within Array, expected VALUE_NUMBER_INT",
                    t);
        }
        if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (LocalDateTime) p.getEmbeddedObject();
        }
        if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            _throwNoNumericTimestampNeedTimeZone(p, ctxt);
        }
        return _handleUnexpectedToken(ctxt, p, "Expected array or string");
    }

    protected boolean shouldReadTimestampsAsNanoseconds(DeserializationContext context) {
        return (_readTimestampsAsNanosOverride != null) ? _readTimestampsAsNanosOverride :
            context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    protected LocalDateTime _fromString(JsonParser p, DeserializationContext ctxt,
            String string0)  throws IOException
    {
        String string = string0.trim();
        if (string.length() == 0) {
            // 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
            //   b/w empty and blank); for now don't which will allow blanks to be
            //   handled like "regular" empty (same as pre-2.12)
            return _fromEmptyString(p, ctxt, string);
        }
        try {
            // 21-Oct-2020, tatu: Changed as per [modules-base#94] for 2.12,
            //    had bad timezone handle change from [modules-base#56]
            if (_formatter == DEFAULT_FORMATTER) {
                // ... only allow iff lenient mode enabled since
                // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                if (string.length() > 10 && string.charAt(10) == 'T') {
                   if (string.endsWith("Z")) {
                       if (isLenient()) {
                           if (_useTimeZoneForLenientDateParsing) {
                               return Instant.parse(string).atZone(ctxt.getTimeZone().toZoneId()).toLocalDateTime();
                           }
                           return LocalDateTime.parse(string.substring(0, string.length()-1),
                                   _formatter);
                       }
                       JavaType t = getValueType(ctxt);
                       return (LocalDateTime) ctxt.handleWeirdStringValue(t.getRawClass(),
                               string,
"Should not contain offset when 'strict' mode set for property or type (enable 'lenient' handling to allow)"
                               );
                   }
                }
            }
           return LocalDateTime.parse(string, _formatter);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, e, string);
        }
    }
}
