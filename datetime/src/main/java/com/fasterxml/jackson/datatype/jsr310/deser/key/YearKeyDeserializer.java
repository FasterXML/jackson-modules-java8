package com.fasterxml.jackson.datatype.jsr310.deser.key;

import static java.time.temporal.ChronoField.YEAR;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import com.fasterxml.jackson.databind.DeserializationContext;

public class YearKeyDeserializer extends Jsr310KeyDeserializer {

    public static final YearKeyDeserializer INSTANCE = new YearKeyDeserializer();

    private YearKeyDeserializer() {
        // singleton
    }

    @Override
    protected Year deserialize(String key, DeserializationContext ctxt) throws IOException {

        try {
            return Year.of(Integer.parseInt(key));
        } catch (NumberFormatException nfe) {
            return _handleDateTimeException(ctxt, Year.class, new DateTimeException("Number format exception", nfe), key);
        } catch (DateTimeException dte) {
            return _handleDateTimeException(ctxt, Year.class, dte, key);
        }
    }
}
