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
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Deserializer for Java 8 temporal {@link Instant}s, {@link OffsetDateTime}, and {@link ZonedDateTime}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class InstantDeserializer<T extends Temporal>
    extends JSR310DateTimeDeserializerBase<T>
{
    /**
     * Constants used to check if the time offset is zero. See [jackson-modules-java8#18]
     */
    private static final Pattern ISO8601_UTC_ZERO_OFFSET_SUFFIX_REGEX = Pattern.compile("\\+00:?(00)?$");

    public static final InstantDeserializer<Instant> INSTANT = new InstantDeserializer<>(
            Instant.class, DateTimeFormatter.ISO_INSTANT,
            Instant::from,
            a -> Instant.ofEpochMilli(a.value),
            a -> Instant.ofEpochSecond(a.integer, a.fraction),
            null,
            true // yes, replace zero offset with Z
    );

    public static final InstantDeserializer<OffsetDateTime> OFFSET_DATE_TIME = new InstantDeserializer<>(
            OffsetDateTime.class, DateTimeFormatter.ISO_OFFSET_DATE_TIME,
            OffsetDateTime::from,
            a -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
            a -> OffsetDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
            (d, z) -> d.withOffsetSameInstant(z.getRules().getOffset(d.toLocalDateTime())),
            true // yes, replace zero offset with Z
    );

    public static final InstantDeserializer<ZonedDateTime> ZONED_DATE_TIME = new InstantDeserializer<>(
            ZonedDateTime.class, DateTimeFormatter.ISO_ZONED_DATE_TIME,
            ZonedDateTime::from,
            a -> ZonedDateTime.ofInstant(Instant.ofEpochMilli(a.value), a.zoneId),
            a -> ZonedDateTime.ofInstant(Instant.ofEpochSecond(a.integer, a.fraction), a.zoneId),
            ZonedDateTime::withZoneSameInstant,
            false // keep zero offset and Z separate since zones explicitly supported
    );

    protected final Function<FromIntegerArguments, T> fromMilliseconds;

    protected final Function<FromDecimalArguments, T> fromNanoseconds;

    protected final Function<TemporalAccessor, T> parsedToValue;

    protected final BiFunction<T, ZoneId, T> adjust;

    /**
     * In case of vanilla `Instant` we seem to need to translate "+0000 | +00:00 | +00"
     * timezone designator into plain "Z" for some reason; see
     * [jackson-modules-java8#18] for more info
     *
     * @since 2.9.0
     */
    protected final boolean replaceZeroOffsetAsZ;

    /**
     * Flag for <code>JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE</code>
     */
    protected final Boolean _adjustToContextTZOverride;

    protected InstantDeserializer(Class<T> supportedType,
            DateTimeFormatter formatter,
            Function<TemporalAccessor, T> parsedToValue,
            Function<FromIntegerArguments, T> fromMilliseconds,
            Function<FromDecimalArguments, T> fromNanoseconds,
            BiFunction<T, ZoneId, T> adjust,
            boolean replaceZeroOffsetAsZ)
    {
        super(supportedType, formatter);
        this.parsedToValue = parsedToValue;
        this.fromMilliseconds = fromMilliseconds;
        this.fromNanoseconds = fromNanoseconds;
        this.adjust = adjust == null ? ((d, z) -> d) : adjust;
        this.replaceZeroOffsetAsZ = replaceZeroOffsetAsZ;
        _adjustToContextTZOverride = null;
    }

    @SuppressWarnings("unchecked")
    protected InstantDeserializer(InstantDeserializer<T> base, DateTimeFormatter f)
    {
        super((Class<T>) base.handledType(), f);
        parsedToValue = base.parsedToValue;
        fromMilliseconds = base.fromMilliseconds;
        fromNanoseconds = base.fromNanoseconds;
        adjust = base.adjust;
        replaceZeroOffsetAsZ = (_formatter == DateTimeFormatter.ISO_INSTANT);
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    @SuppressWarnings("unchecked")
    protected InstantDeserializer(InstantDeserializer<T> base, Boolean adjustToContextTimezoneOverride)
    {
        super((Class<T>) base.handledType(), base._formatter);
        parsedToValue = base.parsedToValue;
        fromMilliseconds = base.fromMilliseconds;
        fromNanoseconds = base.fromNanoseconds;
        adjust = base.adjust;
        replaceZeroOffsetAsZ = base.replaceZeroOffsetAsZ;
        _adjustToContextTZOverride = adjustToContextTimezoneOverride;
    }

    @SuppressWarnings("unchecked")
    protected InstantDeserializer(InstantDeserializer<T> base, DateTimeFormatter f, Boolean leniency)
    {
        super((Class<T>) base.handledType(), f, leniency);
        parsedToValue = base.parsedToValue;
        fromMilliseconds = base.fromMilliseconds;
        fromNanoseconds = base.fromNanoseconds;
        adjust = base.adjust;
        replaceZeroOffsetAsZ = (_formatter == DateTimeFormatter.ISO_INSTANT);
        _adjustToContextTZOverride = base._adjustToContextTZOverride;
    }

    @Override
    protected InstantDeserializer<T> withDateFormat(DateTimeFormatter dtf) {
        if (dtf == _formatter) {
            return this;
        }
        return new InstantDeserializer<T>(this, dtf);
    }

    @Override
    protected InstantDeserializer<T> withLeniency(Boolean leniency) {
        return new InstantDeserializer<T>(this, _formatter, leniency);
    }

    @Override
    protected InstantDeserializer<T> withShape(JsonFormat.Shape shape) { return this; }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        //NOTE: Timestamps contain no timezone info, and are always in configured TZ. Only
        //string values have to be adjusted to the configured TZ.
        switch (parser.currentTokenId())
        {
            case JsonTokenId.ID_NUMBER_FLOAT:
                return _fromDecimal(context, parser.getDecimalValue());

            case JsonTokenId.ID_NUMBER_INT:
                return _fromLong(context, parser.getLongValue());

            case JsonTokenId.ID_STRING:
            {
                String string = parser.getText().trim();
                if (string.length() == 0) {
                    if (!isLenient()) {
                        return _failForNotLenient(parser, context, JsonToken.VALUE_STRING);
                    }
                    return null;
                }
                // only check for other parsing modes if we are using default formatter
                if (_formatter == DateTimeFormatter.ISO_INSTANT ||
                    _formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME ||
                    _formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
                    // 22-Jan-2016, [datatype-jsr310#16]: Allow quoted numbers too
                    int dots = _countPeriods(string);
                    if (dots >= 0) { // negative if not simple number
                        try {
                            if (dots == 0) {
                                return _fromLong(context, Long.parseLong(string));
                            }
                            if (dots == 1) {
                                return _fromDecimal(context, new BigDecimal(string));
                            }
                        } catch (NumberFormatException e) {
                            // fall through to default handling, to get error there
                        }
                    }

                    string = replaceZeroOffsetAsZIfNecessary(string);
                }

                T value;
                try {
                    TemporalAccessor acc = _formatter.parse(string);
                    value = parsedToValue.apply(acc);
                    if (shouldAdjustToContextTimezone(context)) {
                        return adjust.apply(value, this.getZone(context));
                    }
                } catch (DateTimeException e) {
                    value = _handleDateTimeFormatException(context, e, _formatter, string);
                }
                return value;
            }

            case JsonTokenId.ID_EMBEDDED_OBJECT:
                // 20-Apr-2016, tatu: Related to [databind#1208], can try supporting embedded
                //    values quite easily
                return (T) parser.getEmbeddedObject();

            case JsonTokenId.ID_START_ARRAY:
            	return _deserializeFromArray(parser, context);
        }
        return _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING,
                JsonToken.VALUE_NUMBER_INT, JsonToken.VALUE_NUMBER_FLOAT);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonDeserializer<T> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        InstantDeserializer<T> deserializer =
                (InstantDeserializer<T>)super.createContextual(ctxt, property);
        if (deserializer != this) {
            JsonFormat.Value val = findFormatOverrides(ctxt, property, handledType());
            if (val != null) {
                deserializer = new InstantDeserializer<>(deserializer, val.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE));
                if (val.hasLenient()) {
                    Boolean leniency = val.getLenient();
                    if (leniency != null) {
                        deserializer = deserializer.withLeniency(leniency);
                    }
                }
            }
        }
        return deserializer;
    }

    protected boolean shouldAdjustToContextTimezone(DeserializationContext context) {
        return (_adjustToContextTZOverride != null) ? _adjustToContextTZOverride :
                context.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
    }

    // Helper method to find Strings of form "all digits" and "digits-comma-digits"
    protected int _countPeriods(String str)
    {
        int commas = 0;
        for (int i = 0, end = str.length(); i < end; ++i) {
            int ch = str.charAt(i);
            if (ch < '0' || ch > '9') {
                if (ch == '.') {
                    ++commas;
                } else {
                    return -1;
                }
            }
        }
        return commas;
    }

    protected T _fromLong(DeserializationContext context, long timestamp)
    {
        if(context.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)){
            return fromNanoseconds.apply(new FromDecimalArguments(
                    timestamp, 0, this.getZone(context)
            ));
        }
        return fromMilliseconds.apply(new FromIntegerArguments(
                timestamp, this.getZone(context)));
    }

    protected T _fromDecimal(DeserializationContext context, BigDecimal value)
    {
        FromDecimalArguments args =
            DecimalUtils.extractSecondsAndNanos(value, (s, ns) -> new FromDecimalArguments(s, ns, getZone(context)));
        return fromNanoseconds.apply(args);
    }

    private ZoneId getZone(DeserializationContext context)
    {
        // Instants are always in UTC, so don't waste compute cycles
        return (_valueClass == Instant.class) ? null : context.getTimeZone().toZoneId();
    }

    private String replaceZeroOffsetAsZIfNecessary(String text)
    {
        if (replaceZeroOffsetAsZ) {
            return ISO8601_UTC_ZERO_OFFSET_SUFFIX_REGEX.matcher(text).replaceFirst("Z");
        }

        return text;
    }

    public static class FromIntegerArguments // since 2.8.3
    {
        public final long value;
        public final ZoneId zoneId;

        private FromIntegerArguments(long value, ZoneId zoneId)
        {
            this.value = value;
            this.zoneId = zoneId;
        }
    }

    public static class FromDecimalArguments // since 2.8.3
    {
        public final long integer;
        public final int fraction;
        public final ZoneId zoneId;

        private FromDecimalArguments(long integer, int fraction, ZoneId zoneId)
        {
            this.integer = integer;
            this.fraction = fraction;
            this.zoneId = zoneId;
        }
    }
}
