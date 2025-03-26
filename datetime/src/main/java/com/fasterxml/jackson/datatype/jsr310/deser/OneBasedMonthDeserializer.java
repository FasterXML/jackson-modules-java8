package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

/**
 * @since 2.17
 */
public class OneBasedMonthDeserializer extends DelegatingDeserializer {
    private static final long serialVersionUID = 1L;

    public OneBasedMonthDeserializer(JsonDeserializer<?> defaultDeserializer) {
        super(defaultDeserializer);
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonToken token = parser.currentToken();
        if (_isPossibleNumericValue(token)) {
            String monthSpec = parser.getText();
            int oneBasedMonthNumber = _decodeNumber(monthSpec);
            if (1 <= oneBasedMonthNumber && oneBasedMonthNumber <= 12) {
                return Month.of(oneBasedMonthNumber);
            } else if (oneBasedMonthNumber >= 0) {
                throw new InvalidFormatException(parser, "Month number " + oneBasedMonthNumber + " not allowed for 1-based Month.", oneBasedMonthNumber, Integer.class);
            }
        }
        return getDelegatee().deserialize(parser, context);
    }

    private boolean _isPossibleNumericValue(JsonToken token) {
        return token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_STRING;
    }

    /**
     * @return Numeric value of input text that represents a 1-digit or 2-digit number.
     *         Negative value in other cases (empty string, not a number, 3 or more digits).
     */
    private int _decodeNumber(String text) {
        int numValue;
        switch (text.length()) {
            case 1:
                char c = text.charAt(0);
                boolean cValid = ('0' <= c && c <= '9');
                numValue = cValid ? (c - '0') : -1;
                break;
            case 2:
                char c1 = text.charAt(0);
                char c2 = text.charAt(1);
                boolean c12valid = ('0' <= c1 && c1 <= '9' && '0' <= c2 && c2 <= '9');
                numValue = c12valid ? (10 * (c1 - '0') + (c2 - '0')) : -1;
                break;
            default:
                numValue = -1;
        }
        return numValue;
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new OneBasedMonthDeserializer(newDelegatee);
    }
}
