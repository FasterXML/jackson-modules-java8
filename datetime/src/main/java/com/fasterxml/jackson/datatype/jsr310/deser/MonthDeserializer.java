package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;

/**
 * Deserializer for Java 8 temporal {@link MonthDay}s.
 */
public class MonthDeserializer extends JSR310DateTimeDeserializerBase<Month>
{
    private static final long serialVersionUID = 1L;

    public static final MonthDeserializer INSTANCE = new MonthDeserializer();

    private boolean _oneBaseMonths = false;

    /**
     * NOTE: only {@code public} so that use via annotations (see [modules-java8#202])
     * is possible
     *
     * @since 2.12
     */
    public MonthDeserializer() {
        this(null);
    }

    public MonthDeserializer(DateTimeFormatter formatter) {
        super(Month.class, formatter);
    }

    public MonthDeserializer(DateTimeFormatter formatter, boolean _oneBaseMonths) {
        super(Month.class, formatter);
        this._oneBaseMonths = _oneBaseMonths;
    }


    @Override
    public Month deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        if (parser.currentToken() == JsonToken.VALUE_STRING) {
            String monthText = parser.getText();
            if (monthText.isEmpty()) {
                return null;
            }
            for(Month month : Month.values()) {
                if (month.name().equalsIgnoreCase(monthText)) {
                    return month;
                }
            }
            try {
                int monthNo = Integer.parseInt(monthText);
                if (_oneBaseMonths) {
                    return Month.of(monthNo);
                }
                return Month.values()[monthNo];
            } catch (NumberFormatException nfe) {
                throw new MonthParsingException(new DateTimeParseException("Cannot parse java.time.Month", monthText, 0, nfe));
            }
        }
        if(parser.currentToken() == JsonToken.VALUE_NUMBER_INT) {
            int monthNo = parser.getIntValue();
            if (_oneBaseMonths) {
                return Month.of(monthNo);
            }
            return Month.values()[monthNo];
        }
        return null;
    }

    @Override
    protected JSR310DateTimeDeserializerBase<Month> withDateFormat(DateTimeFormatter dtf) {
        return new MonthDeserializer(dtf, _oneBaseMonths);
    }

    @Override
    protected JSR310DateTimeDeserializerBase<Month> withLeniency(Boolean leniency) {
        return this;
    }

    public MonthDeserializer withFeatures(JacksonFeatureSet<JavaTimeFeature> features) {
        return new MonthDeserializer(this._formatter, features.isEnabled(JavaTimeFeature.ONE_BASED_MONTHS));
    }

    static class MonthParsingException extends JsonProcessingException {
        protected MonthParsingException(Throwable rootCause) {
            super(rootCause);
        }
    }
}
