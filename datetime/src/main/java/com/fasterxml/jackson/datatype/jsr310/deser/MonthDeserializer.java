package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class MonthDeserializer extends DelegatingDeserializer {
    private final boolean _enableOneBaseMonths;

    public MonthDeserializer(JsonDeserializer<Enum> defaultDeserializer, boolean oneBaseMonths) {
        super(defaultDeserializer);
        _enableOneBaseMonths = oneBaseMonths;
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        Month zeroBaseMonth = (Month) getDelegatee().deserialize(parser, context);
        if (!_enableOneBaseMonths || zeroBaseMonth == null) {
            return zeroBaseMonth;
        }

        JsonToken token = parser.currentToken();
        if (token == JsonToken.VALUE_NUMBER_INT || _isNumberAsString(parser.getText(), token)) {
            if (zeroBaseMonth == Month.JANUARY) {
                throw new InvalidFormatException(parser, "Month.JANUARY value not allowed for 1-based Month.", zeroBaseMonth, Month.class);
            } else {
                return zeroBaseMonth.minus(1);
            }
        }
        return zeroBaseMonth;
    }

    private boolean _isNumberAsString(String text, JsonToken token) throws IOException {
        String regex = "[\"']?\\d{1,2}[\"']?";
        return token == JsonToken.VALUE_STRING && text.matches(regex);
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new MonthDeserializer((JsonDeserializer<Enum>) newDelegatee, _enableOneBaseMonths);
    }
}