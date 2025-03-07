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
        // ISO_ZONED_DATE_TIME is an extended version of ISO compliant format
        // ISO_OFFSET_DATE_TIME with additional information :Zone Id
        // (This is not part of the ISO-8601 standard)
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
        this(base, useTimestamp, base._useNanoseconds, formatter, base._shape, writeZoneId);
    }

    @Deprecated // since 2.14
    protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter,
            Boolean writeZoneId) {
        this(base, useTimestamp, useNanoseconds, formatter, base._shape, writeZoneId);
    }

    /**
     * @since 2.14
     */
    protected ZonedDateTimeSerializer(ZonedDateTimeSerializer base,
            Boolean useTimestamp, Boolean useNanoseconds, DateTimeFormatter formatter,
            JsonFormat.Shape shape, Boolean writeZoneId) {
        super(base, useTimestamp, useNanoseconds, formatter, shape);
        _writeZoneId = writeZoneId;
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFormat(
        Boolean useTimestamp,
        DateTimeFormatter formatter,
        JsonFormat.Shape shape) {
        return new ZonedDateTimeSerializer(this, useTimestamp, _useNanoseconds, formatter,
            shape, _writeZoneId);
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
            // [modules-java8#333]: `@JsonFormat` with pattern should override
            //   `SerializationFeature.WRITE_DATES_WITH_ZONE_ID`
            if ((_formatter != null) && (_shape == JsonFormat.Shape.STRING)) {
                ; // use default handling
            } else if (shouldWriteWithZoneId(provider)) {
                // write with zone
                g.writeString(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value));
                return;
            }
        }
        super.serialize(value, g, provider);
    }

    @Override
    protected String formatValue(ZonedDateTime value, SerializerProvider provider) {
        String formatted = super.formatValue(value, provider);
        // [modules-java8#333]: `@JsonFormat` with pattern should override
        //   `SerializationFeature.WRITE_DATES_WITH_ZONE_ID`
        if (_formatter != null && _shape == JsonFormat.Shape.STRING) {
            // Why not `if (shouldWriteWithZoneId(provider))` ?
            if (Boolean.TRUE.equals(_writeZoneId)) {
                formatted += "[" + value.getZone().getId() + "]";
            }
        }
        return formatted;
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
