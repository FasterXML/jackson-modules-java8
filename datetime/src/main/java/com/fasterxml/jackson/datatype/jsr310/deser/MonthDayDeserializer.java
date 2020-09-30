package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;

/**
 * Deserializer for Java 8 temporal {@link MonthDay}s.
 */
public class MonthDayDeserializer extends JSR310DateTimeDeserializerBase<MonthDay>
{
    private static final long serialVersionUID = 1L;

    public static final MonthDayDeserializer INSTANCE = new MonthDayDeserializer(null);

    public MonthDayDeserializer(DateTimeFormatter formatter) {
        super(MonthDay.class, formatter);
    }

    @Override
    protected MonthDayDeserializer withDateFormat(DateTimeFormatter dtf) {
        return new MonthDayDeserializer(dtf);
    }

    /**
     * Since 2.12
     */
    protected MonthDayDeserializer(MonthDayDeserializer base, Boolean leniency) {
        super(base, leniency);
    }

    @Override
    protected MonthDayDeserializer withLeniency(Boolean leniency) {
        return new MonthDayDeserializer(this, leniency);
    }

    @Override
    protected MonthDayDeserializer withShape(JsonFormat.Shape shape) { return this; }

    @Override
    public MonthDay deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(parser, context, parser.getText());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (parser.isExpectedStartObjectToken()) {
            return _fromString(parser, context,
                    context.extractScalarFromObject(parser, this, handledType()));
        }
        if (parser.isExpectedStartArrayToken()) {
            JsonToken t = parser.nextToken();
            if (t == JsonToken.END_ARRAY) {
                return null;
            }
            if ((t == JsonToken.VALUE_STRING || t == JsonToken.VALUE_EMBEDDED_OBJECT)
                    && context.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
                final MonthDay parsed = deserialize(parser, context);
                if (parser.nextToken() != JsonToken.END_ARRAY) {
                    handleMissingEndArrayForSingle(parser, context);
                }
                return parsed;
            }
            if (t != JsonToken.VALUE_NUMBER_INT) {
                _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "month");
            }
            int month = parser.getIntValue();
            int day = parser.nextIntValue(-1);
            if (day == -1) {
                if (!parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                    _reportWrongToken(context, JsonToken.VALUE_NUMBER_INT, "day");
                }
                day = parser.getIntValue();
            }
            if (parser.nextToken() != JsonToken.END_ARRAY) {
                throw context.wrongTokenException(parser, handledType(), JsonToken.END_ARRAY,
                        "Expected array to end");
            }
            return MonthDay.of(month, day);
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (MonthDay) parser.getEmbeddedObject();
        }
        return _handleUnexpectedToken(context, parser,
                JsonToken.VALUE_STRING, JsonToken.START_ARRAY);
    }

    protected MonthDay _fromString(JsonParser p, DeserializationContext ctxt,
            String string)  throws IOException
    {
        string = string.trim();
        if (string.length() == 0) {
            if (!isLenient()) {
                return _failForNotLenient(p, ctxt, JsonToken.VALUE_STRING);
            }
            return null;
        }
        try {
            if (_formatter == null) {
                return MonthDay.parse(string);
            }
            return MonthDay.parse(string, _formatter);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, e, string);
        }
    }
}
