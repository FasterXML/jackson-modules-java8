package com.fasterxml.jackson.datatype.jsr310.deser.key;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * @since 2.10 (had a typo previously)
 */
public class YearMonthKeyDeserializer extends Jsr310KeyDeserializer {
    public static final YearMonthKeyDeserializer INSTANCE = new YearMonthKeyDeserializer();

    // parser copied from YearMonth
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .toFormatter();

    protected YearMonthKeyDeserializer() { } // only protected for 2.10, to allow sub-class

    @Override
    protected YearMonth deserialize(String key, DeserializationContext ctxt) throws IOException {
        try {
            return YearMonth.parse(key, FORMATTER);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, YearMonth.class, e, key);
        }
    }
}
