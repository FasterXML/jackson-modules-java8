package tools.jackson.datatype.jsr310.ser.key;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.ValueSerializer;
import tools.jackson.datatype.jsr310.DecimalUtils;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;

public class ZonedDateTimeKeySerializer extends ValueSerializer<ZonedDateTime> {

    public static final ZonedDateTimeKeySerializer INSTANCE = new ZonedDateTimeKeySerializer();

    private ZonedDateTimeKeySerializer() {
        // singleton
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator g, SerializerProvider serializers)
        throws JacksonException
    {
        /* [modules-java8#127]: Serialization of timezone data is disabled by default, but can be
         * turned on by enabling `SerializationFeature.WRITE_DATES_WITH_ZONE_ID`
         */
        if (serializers.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)) {
            g.writeName(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value));
        } else if (useTimestamps(serializers)) {
            if (useNanos(serializers)) {
                g.writeName(DecimalUtils.toBigDecimal(value.toEpochSecond(), value.getNano()).toString());
            } else {
                g.writeName(String.valueOf(value.toInstant().toEpochMilli()));
            }
        } else {
            g.writeName(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
        }
    }

    private static boolean useNanos(SerializerProvider serializers) {
        return serializers.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    private static boolean useTimestamps(SerializerProvider serializers) {
        return serializers.isEnabled(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
    }
}
