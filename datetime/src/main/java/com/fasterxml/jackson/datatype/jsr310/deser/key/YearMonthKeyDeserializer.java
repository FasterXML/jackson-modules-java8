package com.fasterxml.jackson.datatype.jsr310.deser.key;

import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.time.DateTimeException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;

public class YearMonthKeyDeserializer extends Jsr310KeyDeserializer {
    public static final YearMonthKeyDeserializer INSTANCE = new YearMonthKeyDeserializer();

    // parser copied from YearMonth
    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .appendLiteral('-')
            .appendValue(MONTH_OF_YEAR, 2)
            .toFormatter();

    private YearMonthKeyDeserializer() { } // singleton

    @Override
    protected YearMonth deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        try {
            return YearMonth.parse(key, FORMATTER);
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, YearMonth.class, e, key);
        }
    }
}
