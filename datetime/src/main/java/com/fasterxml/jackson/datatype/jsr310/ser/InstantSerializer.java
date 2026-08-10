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

package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;

/**
 * Serializer for Java 8 temporal {@link Instant}s, {@link OffsetDateTime}, and {@link ZonedDateTime}s.
 *
 * @author Nick Williams
 * @since 2.2
 */
public class InstantSerializer extends InstantSerializerBase<Instant>
{
    private static final long serialVersionUID = 1L;

    public static final InstantSerializer INSTANCE = new InstantSerializer();

    /**
     * Whether {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature#ALWAYS_WRITE_SUBSECOND_DIGITS}
     * is enabled: if so, the default representation is padded to at least 3 sub-second
     * digits.
     *
     * @since 2.23
     */
    private final boolean _alwaysWriteSubsecondDigits;

    protected InstantSerializer() {
        super(Instant.class, Instant::toEpochMilli, Instant::getEpochSecond, Instant::getNano,
                // null -> use 'value.toString()', default format
                null);
        _alwaysWriteSubsecondDigits = false;
    }

    @Deprecated // since 2.14
    protected InstantSerializer(InstantSerializer base,
            Boolean useTimestamp, DateTimeFormatter formatter) {
        this(base, useTimestamp, base._useNanoseconds, formatter);
    }

    /**
     * @since 2.14
     */
    protected InstantSerializer(InstantSerializer base, Boolean useTimestamp,
            DateTimeFormatter formatter, JsonFormat.Shape shape) {
        super(base, useTimestamp, base._useNanoseconds, formatter, shape);
        _alwaysWriteSubsecondDigits = base._alwaysWriteSubsecondDigits;
    }

    protected InstantSerializer(InstantSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
        _alwaysWriteSubsecondDigits = base._alwaysWriteSubsecondDigits;
    }

    /**
     * @since 2.23
     */
    protected InstantSerializer(InstantSerializer base, boolean alwaysWriteSubsecondDigits) {
        super(base, base._useTimestamp, base._useNanoseconds, base._formatter, base._shape);
        _alwaysWriteSubsecondDigits = alwaysWriteSubsecondDigits;
    }

    /**
     * Method called by {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule}
     * to apply module-level {@link JavaTimeFeature} settings.
     *
     * @since 2.23
     */
    public InstantSerializer withFeatures(JacksonFeatureSet<JavaTimeFeature> features) {
        if (features.isEnabled(JavaTimeFeature.ALWAYS_WRITE_SUBSECOND_DIGITS)) {
            return new InstantSerializer(this, true);
        }
        return this;
    }

    /**
     * Overridden to implement
     * {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature#ALWAYS_WRITE_SUBSECOND_DIGITS}
     * by padding the default representation, instead of swapping in a different formatter.
     *<p>
     * Rationale: the default representation is {@link Instant#toString()}, that is,
     * {@link DateTimeFormatter#ISO_INSTANT}, which writes exactly 0, 3, 6 or 9 sub-second
     * digits -- so the only case needing a fix is the zero one. Formatting through a
     * zone-bound {@code DateTimeFormatter} instead would resolve the value via
     * {@link java.time.LocalDateTime}, whose year range is narrower than that of
     * {@code Instant}, and would thereby fail for {@link Instant#MIN} / {@link Instant#MAX}.
     *
     * @since 2.23
     */
    @Override
    protected String formatValue(Instant value, SerializerProvider provider)
    {
        String formatted = super.formatValue(value, provider);
        // Only applies to the default representation: an explicit formatter wins
        if (_alwaysWriteSubsecondDigits && (_formatter == null) && (value.getNano() == 0)) {
            final int last = formatted.length() - 1;
            // Defensive: `ISO_INSTANT` always ends in 'Z', but do not corrupt output if not
            if ((last >= 0) && (formatted.charAt(last) == 'Z')) {
                formatted = formatted.substring(0, last) + ".000Z";
            }
        }
        return formatted;
    }

    @Override
    protected JSR310FormattedSerializerBase<Instant> withFormat(Boolean useTimestamp,
            DateTimeFormatter formatter, JsonFormat.Shape shape) {
        return new InstantSerializer(this, useTimestamp, formatter, shape);
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new InstantSerializer(this, _useTimestamp, writeNanoseconds, _formatter);
    }
}
