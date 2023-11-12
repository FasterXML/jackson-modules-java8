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
     * Feature that controls whether numeric strings are interpreted as numeric
     * timestamps (enabled) nor not (disabled) in addition to an explicitly
     * defined pattern.
     * <p>
     * Note that when a pattern is not explicitly defined numeric strings are
     * interpreted as a numeric timestamp.
     */
    READ_NUMERIC_STRINGS_AS_DATE_TIMESTAMP(false);

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