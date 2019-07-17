package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;
import java.time.YearMonth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class YearMonthDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(YearMonth.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        final YearMonth value = read("'2000-01'");
        assertEquals("The value is not correct", YearMonth.of(2000, Month.JANUARY), value);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            read(quote("notayearmonth"));
            fail("expected DateTimeParseException");
        } catch (InvalidFormatException e) {
            verifyException(e, "could not be parsed");
        }
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read("['2000-01']");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Unexpected token (VALUE_STRING)");
        }
    }

    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        // works even without the feature enabled
        assertNull(read("[]"));
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        YearMonth value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(aposToQuotes("['2000-01']"));
        assertEquals("The value is not correct", YearMonth.of(2000, Month.JANUARY), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        YearMonth value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .readValue( "[]");
        assertNull(value);
    }

    private YearMonth read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }
}
