package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.core.util.JacksonFeature;

/**
 * Configurable on/off features for Java 8 Date/Time module ({@link JavaTimeModule}).
 *
 * @since 2.16
 */
public enum JavaTimeFeature implements JacksonFeature
{
    /**
     * Feature that determines whether {@link java.time.ZoneId} is normalized
     * (via call to {@code java.time.ZoneId#normalized()}) when deserializing
     * types like {@link java.time.ZonedDateTime}.
     *<p>
     * Default setting is enabled, for backwards-compatibility with
     * Jackson 2.15.
     */
    NORMALIZE_DESERIALIZED_ZONE_ID(true),

    /**
     * Feature that determines whether the {@link java.util.TimeZone} of the
     * {@link com.fasterxml.jackson.databind.DeserializationContext} is used
     * when leniently deserializing {@link java.time.LocalDate} or
     * {@link java.time.LocalDateTime} from the UTC/ISO instant format.
     * <p>
     * Default setting is disabled, for backwards-compatibility with
     * Jackson 2.18.
     *
     * @since 2.19
     */
    USE_TIME_ZONE_FOR_LENIENT_DATE_PARSING(false),

    /**
     * Feature that controls whether stringified numbers (Strings that without
     * quotes would be legal JSON Numbers) may be interpreted as
     * timestamps (enabled) or not (disabled), in case where there is an
     * explicitly defined pattern ({@code DateTimeFormatter}) for value.
     * <p>
     * Note that when the default pattern is used (no custom pattern defined),
     * stringified numbers are always accepted as timestamps regardless of
     * this feature.
     */
    ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS(false),

    /**
     * Feature that determines whether {@link java.time.Month} is serialized
     * and deserialized as using a zero-based index (FALSE) or a one-based index (TRUE).
     * For example, "1" would be serialized/deserialized as Month.JANUARY if TRUE and Month.FEBRUARY if FALSE.
     *<p>
     * Default setting is false, meaning that Month is serialized/deserialized as a zero-based index.
     */
    ONE_BASED_MONTHS(false),

    /**
     * Feature that determines whether sub-second digits are always written when
     * serializing {@link java.time.Instant}, {@link java.time.OffsetDateTime},
     * {@link java.time.ZonedDateTime} and {@link java.time.LocalDateTime} <b>values</b>
     * as ISO-8601 Strings using the default format.
     *<p>
     * When disabled (the default), the JDK-provided ISO formatters are used and
     * a zero sub-second value is omitted altogether -- {@code 2017-09-14T04:28:48Z}
     * -- which means that output width varies with the value, breaking systems
     * that expect fixed-precision timestamps (or that sort timestamps as text).
     *<p>
     * When enabled, at least 3 (millisecond) sub-second digits are always written,
     * zero-padded if necessary -- {@code 2017-09-14T04:28:48.000Z}. Higher precision
     * is preserved: a value with microsecond or nanosecond precision is written with
     * 6 or 9 digits respectively, so no information is lost.
     *<p>
     * Only affects the default format: an explicit {@code DateTimeFormatter} or
     * a {@link com.fasterxml.jackson.annotation.JsonFormat} pattern takes precedence,
     * as does writing values as numeric timestamps.
     *<p>
     * Also note that this only applies to values, and NOT to {@link java.util.Map}
     * keys: date/time keys keep being written using the JDK-provided ISO formatters,
     * so a zero sub-second value is still omitted there. Types other than the four
     * listed above -- notably {@link java.time.LocalTime} and
     * {@link java.time.OffsetTime}, whose ISO formats also omit the seconds field --
     * are likewise unaffected.
     *<p>
     * Default setting is disabled, for backwards compatibility.
     *
     * @since 2.23
     */
    ALWAYS_WRITE_SUBSECOND_DIGITS(false),

    /**
     * Feature that controls whether stringified numbers (JSON Strings that
     * without quotes would be legal JSON Numbers) may be deserialized as
     * {@link java.time.Duration} values (enabled) or not (disabled).
     * <p>
     * When disabled (the default), JSON Strings are parsed with
     * {@link java.time.Duration#parse} and must be ISO-8601 duration
     * representations such as {@code "PT1H"} -- an int-like String such as
     * {@code "3600"} fails.
     * When enabled, integer and decimal numeric Strings are handled the same
     * as JSON numbers: integers use {@link com.fasterxml.jackson.annotation.JsonFormat}
     * pattern unit conversion (or
     * {@link com.fasterxml.jackson.databind.DeserializationFeature#READ_DATE_TIMESTAMPS_AS_NANOSECONDS}),
     * and decimals are treated as seconds with fractional nanos.
     * ISO-8601 duration Strings remain accepted either way.
     * <p>
     * Default setting is disabled, for backwards compatibility.
     *
     * @since 2.23
     */
    ALLOW_STRINGIFIED_DURATION_VALUES(false)
    ;

  /**
     * Whether feature is enabled or disabled by default.
     */
    private final boolean _defaultState;

    private final int _mask;

    JavaTimeFeature(boolean enabledByDefault) {
        _defaultState = enabledByDefault;
        _mask = (1 << ordinal());
    }

    @Override
    public boolean enabledByDefault() { return _defaultState; }

    @Override
    public boolean enabledIn(int flags) { return (flags & _mask) != 0; }

    @Override
    public int getMask() { return _mask; }
}
