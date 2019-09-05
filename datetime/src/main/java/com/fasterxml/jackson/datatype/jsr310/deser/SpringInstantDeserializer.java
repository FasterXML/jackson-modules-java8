package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.databind.JsonDeserializer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class SpringInstantDeserializer extends InstantDeserializer<OffsetDateTime>  {
    private static final long serialVersionUID = -237644245579626895L;

    public SpringInstantDeserializer() {
        super(InstantDeserializer.OFFSET_DATE_TIME,
                new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .optionalStart().appendOffset("+HH:MM", "+00:00").optionalEnd()
                        .optionalStart().appendOffset("+HHMM", "+0000").optionalEnd()
                        .optionalStart().appendOffset("+HH", "Z").optionalEnd()
                        .toFormatter());
    }

//    @Override
//    public JsonDeserializer<OffsetDateTime> createContextual(DeserializationContext ctxt, BeanProperty property) {
//        // ignore context (i.e. formatting pattern that will be used for serialization)
//        return this;
//    }
}
