package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;

import com.fasterxml.jackson.databind.DeserializationContext;

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

    @Override
    public Month deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        final JsonToken t = parser.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            return _fromString(parser, context, parser.getText());
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return _fromNumber(context, parser.getIntValue());
        }
        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return (Month) parser.getEmbeddedObject();
        }
        if (parser.hasToken(JsonToken.START_ARRAY)){
            return _deserializeFromArray(parser, context);
        }
        return _handleUnexpectedToken(context, parser, JsonToken.VALUE_STRING, JsonToken.VALUE_NUMBER_INT);
    }

    protected Month _fromString(JsonParser p, DeserializationContext ctxt,
            String string0)  throws IOException
    {
        String string = string0.trim();
        if (string.length() == 0) {
            // 22-Oct-2020, tatu: not sure if we should pass original (to distinguish
            //   b/w empty and blank); for now don't which will allow blanks to be
            //   handled like "regular" empty (same as pre-2.12)
            return _fromEmptyString(p, ctxt, string);
        }
        // 30-Sep-2020: Should allow use of "Timestamp as String" for XML/CSV
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                // "timestamp" is close enough; basically integer number in `long` range
                && _isValidTimestampString(string)) {
            return _fromNumber(ctxt, NumberInput.parseInt(string));
        }
        // Could we actually use formatter somehow?
        //
        // if (_formatter != null) {  }

        for (Month month : Month.values()) {
            if (month.name().equalsIgnoreCase(string)) {
                return month;
            }
        }
        return (Month) ctxt.handleWeirdStringValue(Month.class, string,
                "Unrecognized Month name");
    }

    protected Month _fromNumber(DeserializationContext ctxt, int monthNo) {
        if (_oneBaseMonths) {
            return Month.of(monthNo);
        }
        return Month.values()[monthNo];
    }
}
