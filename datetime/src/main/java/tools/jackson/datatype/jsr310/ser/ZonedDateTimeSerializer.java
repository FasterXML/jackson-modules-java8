package tools.jackson.datatype.jsr310.ser;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;

public class ZonedDateTimeSerializer extends InstantSerializerBase<ZonedDateTime> {
    public static final ZonedDateTimeSerializer INSTANCE = new ZonedDateTimeSerializer();

    /**
     * Flag for <code>JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID</code>
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
            DateTimeFormatter formatter,
            Boolean useTimestamp, Boolean useNanoseconds,
            Boolean writeZoneId,
            JsonFormat.Shape shape)
    {
        super(base, formatter, useTimestamp, useNanoseconds, shape);
        _writeZoneId = writeZoneId;
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFormat(DateTimeFormatter formatter, 
            Boolean useTimestamp,
            JsonFormat.Shape shape)
    {
        return new ZonedDateTimeSerializer(this, formatter,
                useTimestamp, _useNanoseconds, _writeZoneId,
                shape);
    }

    @Override
    protected JSR310FormattedSerializerBase<?> withFeatures(Boolean writeZoneId,
            Boolean useNanoseconds)
    {
        return new ZonedDateTimeSerializer(this, _formatter,
                _useTimestamp, useNanoseconds, writeZoneId, _shape);
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
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

    public boolean shouldWriteWithZoneId(SerializerProvider ctxt) {
        return (_writeZoneId != null) ? _writeZoneId :
            ctxt.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
    }

    @Override
    protected JsonToken serializationShape(SerializerProvider provider) {
        if (!useTimestamp(provider) && shouldWriteWithZoneId(provider)) {
            return JsonToken.VALUE_STRING;
        }
        return super.serializationShape(provider);
    }
}
