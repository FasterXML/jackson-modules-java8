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
     * Placeholder
     */
    BOGUS(false);

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
