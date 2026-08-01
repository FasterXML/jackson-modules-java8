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

    protected InstantSerializer() {
        super(Instant.class, Instant::toEpochMilli, Instant::getEpochSecond, Instant::getNano,
                // null -> use 'value.toString()', default format
                null);
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
    }

    protected InstantSerializer(InstantSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
    }

    /**
     * @since 2.23
     */
    protected InstantSerializer(InstantSerializer base, DateTimeFormatter defaultFormat) {
        super(base, defaultFormat);
    }

    /**
     * Method called by {@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule}
     * to apply module-level {@link JavaTimeFeature} settings.
     *
     * @since 2.23
     */
    public InstantSerializer withFeatures(JacksonFeatureSet<JavaTimeFeature> features) {
        if (features.isEnabled(JavaTimeFeature.ALWAYS_WRITE_SUBSECOND_DIGITS)) {
            return new InstantSerializer(this, SubSecondFormatters.INSTANT);
        }
        return this;
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
