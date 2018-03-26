package com.fasterxml.jackson.datatype.jsr310.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeSerializer extends InstantSerializerBase<ZonedDateTime> {
    private static final long serialVersionUID = 1L;

    public static final ZonedDateTimeSerializer INSTANCE = new ZonedDateTimeSerializer();

    /**
     * Flag for <code>JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID</code>
     *
     * @since 2.8
     */
    protected final Boolean _writeZoneId;
    
    protected ZonedDateTimeSerializer() {
        // ISO_ZONED_DATE_TIME is not the ISO format, it is an extension of it
        this(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public ZonedDateTimeSerializer(DateTimeFormatter formatter) {
        super(ZonedDateTime.class, dt -> dt.toInstant().toEpochMilli(),
              ZonedDateTime::toEpochSecond, ZonedDateTime::getNano,
              formatter);
        _writeZoneId = null;
    }

    protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base,
            Boolean useTimestamp, DateTimeFormatter formatter, Boolean writeZoneId) {
        this(base, useTimestamp, null, formatter, writeZoneId);
    }

    protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter,
            Boolean writeZoneId) {
        super(base, useTimestamp, useNanoseconds, formatter);
        _writeZoneId = writeZoneId;
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFormat(
        Boolean useTimestamp,
        DateTimeFormatter formatter,
        JsonFormat.Shape shape) {
        return new ZonedDateTimeSerializer(this, useTimestamp, formatter, _writeZoneId);
    }

    @Override
    @Deprecated
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId) {
        return new ZonedDateTimeSerializer(this, _useTimestamp, _formatter, writeZoneId);
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId, Boolean writeNanoseconds) {
        return new ZonedDateTimeSerializer(this, _useTimestamp, writeNanoseconds, _formatter, writeZoneId);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        if (!useTimestamp(provider)) {
            if (shouldWriteWithZoneId(provider)) {
                // write with zone
                g.writeString(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value));
                return;
            }
        }
        super.serialize(value, g, provider);
    }

    /**
     * @since 2.8
     */
    public boolean shouldWriteWithZoneId(SerializerProvider ctxt) {
        return (_writeZoneId != null) ? _writeZoneId :
            ctxt.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    }

    @Override // since 2.9
    protected JsonToken serializationShape(SerializerProvider provider) {
        if (!useTimestamp(provider) && shouldWriteWithZoneId(provider)) {
            return JsonToken.VALUE_STRING;
        }
        return super.serializationShape(provider);
    }
}
