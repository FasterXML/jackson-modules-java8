package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonParser;

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
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        switch (p.currentToken()) {
        case VALUE_NUMBER_INT:
            return _decodeMonth(p.getIntValue(), p);
        case VALUE_STRING:
            String monthSpec = p.getText();
            int oneBasedMonthNumber = _decodeNumber(monthSpec);
            if (oneBasedMonthNumber >= 0) {
                return _decodeMonth(oneBasedMonthNumber, p);
            }
            // Otherwise fall through to default handling
            break;
        default:
        }
        return getDelegatee().deserialize(p, ctxt);
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

    private Month _decodeMonth(int oneBasedMonthNumber, JsonParser parser) throws InvalidFormatException {
        if (Month.JANUARY.getValue() <= oneBasedMonthNumber && oneBasedMonthNumber <= Month.DECEMBER.getValue()) {
            return Month.of(oneBasedMonthNumber);
        }
        throw new InvalidFormatException(parser, "Month number " + oneBasedMonthNumber + " not allowed for 1-based Month.", oneBasedMonthNumber, Integer.class);
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> newDelegatee) {
        return new OneBasedMonthDeserializer(newDelegatee);
    }
}
