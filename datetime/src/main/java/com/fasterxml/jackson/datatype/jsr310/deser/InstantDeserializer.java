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
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserializer for Java 8 temporal {@link Instant}s, {@link OffsetDateTime},
 * and {@link ZonedDateTime}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class InstantDeserializer<T extends Temporal>
    extends JSR310DateTimeDeserializerBase<T>
{
    private static final long serialVersionUID = 1L;

    /**
     * Constants used to check if the time offset is zero. See [jackson-modules-java8#18]
     *
     * @since 2.9.0
     */
    private static final Pattern ISO8601_UTC_ZERO_OFFSET_SUFFIX_REGEX = Pattern.compile("\\+00:?(00)?$");

    /**
     * Constants used to check if ISO 8601 time string is colonless. See [jackson-modules-java8#131]
     *
     * @since 2.13
     */
    protected static final Pattern ISO8601_COLONLESS_OFFSET_REGEX = Pattern.compile("[+-][0-9]{4}(?=\\[|$)");

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
            (d, z) -> (d.isEqual(OffsetDateTime.MIN) || d.isEqual(OffsetDateTime.MAX) ? d : d.withOffsetSameInstant(z.getRules().getOffset(d.toLocalDateTime()))),
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
     *
     * @since 2.8
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
    @Override // @since 2.12.1
    protected JSR310DateTimeDeserializerBase<?> _withFormatOverrides(DeserializationContext ctxt,
            BeanProperty property, JsonFormat.Value formatOverrides)
    {
        InstantDeserializer<T> deser = (InstantDeserializer<T>) super._withFormatOverrides(ctxt,
                property, formatOverrides);
        Boolean B = formatOverrides.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        if (!Objects.equals(B, deser._adjustToContextTZOverride)) {
            return new InstantDeserializer<T>(deser, B);
        }
        return deser;
    }

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
                return _fromString(parser, context, parser.getText());
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            case JsonTokenId.ID_START_OBJECT:
                return _fromString(parser, context,
                        context.extractScalarFromObject(parser, this, handledType()));
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

    protected T _fromString(JsonParser p, DeserializationContext ctxt,
            String string0)  throws IOException
    {
        String string = string0.trim();
        if (string.length() == 0) {
            // 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
            //   b/w empty and blank); for now don't which will allow blanks to be
            //   handled like "regular" empty (same as pre-2.12)
            return _fromEmptyString(p, ctxt, string);
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
                        return _fromLong(ctxt, Long.parseLong(string));
                    }
                    if (dots == 1) {
                        return _fromDecimal(ctxt, new BigDecimal(string));
                    }
                } catch (NumberFormatException e) {
                    // fall through to default handling, to get error there
                }
            }

            string = replaceZeroOffsetAsZIfNecessary(string);
        }

        // For some reason DateTimeFormatter.ISO_INSTANT only supports UTC ISO 8601 strings, so it have to be excluded
        if (_formatter == DateTimeFormatter.ISO_OFFSET_DATE_TIME ||
            _formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {

            // 21-March-2021, Oeystein: Work-around to support basic iso 8601 format (colon-less).
            // As per JSR-310; Only extended 8601 formats (with colon) are supported for
            // ZonedDateTime.parse() and OffsetDateTime.parse().
            // https://github.com/FasterXML/jackson-modules-java8/issues/131
            string = addInColonToOffsetIfMissing(string);
        }

        T value;
        try {
            TemporalAccessor acc = _formatter.parse(string);
            value = parsedToValue.apply(acc);
            if (shouldAdjustToContextTimezone(ctxt)) {
                return adjust.apply(value, getZone(ctxt));
            }
        } catch (DateTimeException e) {
            value = _handleDateTimeException(ctxt, e, string);
        }
        return value;
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

    // @since 2.13
    private String addInColonToOffsetIfMissing(String text)
    {
        final Matcher matcher = ISO8601_COLONLESS_OFFSET_REGEX.matcher(text);
        if (matcher.find()){
            StringBuilder sb = new StringBuilder(matcher.group(0));
            sb.insert(3, ":");

            return matcher.replaceFirst(sb.toString());
        }
        return text;
    }

    public static class FromIntegerArguments // since 2.8.3
    {
        public final long value;
        public final ZoneId zoneId;

        FromIntegerArguments(long value, ZoneId zoneId)
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

        FromDecimalArguments(long integer, int fraction, ZoneId zoneId)
        {
            this.integer = integer;
            this.fraction = fraction;
            this.zoneId = zoneId;
        }
    }
}
