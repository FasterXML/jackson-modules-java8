package com.fasterxml.jackson.datatype.jsr310.ser.key;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ZonedDateTimeKeySerializer extends JsonSerializer<ZonedDateTime> {

    public static final ZonedDateTimeKeySerializer INSTANCE = new ZonedDateTimeKeySerializer();

    private ZonedDateTimeKeySerializer() {
        // singleton
    }

    @Override
    public void serialize(ZonedDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException,
            JsonProcessingException {
        /* [modules-java8#127]: Serialization of timezone data is disabled by default, but can be
         * turned on by enabling `SerializationFeature.WRITE_DATES_WITH_ZONE_ID`
         */
        if (serializers.isEnabled(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)) {
            gen.writeFieldName(DateTimeFormatter.ISO_ZONED_DATE_TIME.format(value));
        } else {
            gen.writeFieldName(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
        }
    }

}
