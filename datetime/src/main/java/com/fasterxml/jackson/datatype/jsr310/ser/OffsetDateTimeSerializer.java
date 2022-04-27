package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeSerializer extends InstantSerializerBase<OffsetDateTime>
{
    private static final long serialVersionUID = 1L;

    public static final OffsetDateTimeSerializer INSTANCE = new OffsetDateTimeSerializer();

    protected OffsetDateTimeSerializer() {
        super(OffsetDateTime.class, dt -> dt.toInstant().toEpochMilli(),
                OffsetDateTime::toEpochSecond, OffsetDateTime::getNano,
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @Deprecated // since 2.14
    protected OffsetDateTimeSerializer(OffsetDateTimeSerializer base,
            Boolean useTimestamp, DateTimeFormatter formatter) {
        this(base, useTimestamp, base._useNanoseconds, formatter);
    }

    protected OffsetDateTimeSerializer(OffsetDateTimeSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter) {
        super(base, useTimestamp, useNanoseconds, formatter);
    }

    /**
     * @since 2.14
     */
    public OffsetDateTimeSerializer(OffsetDateTimeSerializer base, Boolean useTimestamp,
            DateTimeFormatter formatter, JsonFormat.Shape shape) {
        super(base, useTimestamp, base._useNanoseconds, formatter, shape);
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFormat(Boolean useTimestamp,
        DateTimeFormatter formatter, JsonFormat.Shape shape)
    {
        return new OffsetDateTimeSerializer(this, useTimestamp, formatter, shape);
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new OffsetDateTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter);
    }
}
