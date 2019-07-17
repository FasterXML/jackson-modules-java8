package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class MonthDayDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(MonthDay.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(MonthDay.of(Month.JANUARY, 1), "'--01-01'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notamonthday'");
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read("['--01-01']");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "START_ARRAY token");
            // OK
        }
    }
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[]");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "START_ARRAY token");
            // OK
        }
        try {
            READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue("[]");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Unexpected token (END_ARRAY)");
           // OK
        }
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        MonthDay value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.readValue(aposToQuotes("['--01-01']"));
        expect(MonthDay.of(Month.JANUARY, 1), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        MonthDay value = READER
    			.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    			.readValue("[]");
        assertNull(value);
    }

    private void expectFailure(String aposJson) throws Throwable {
        try {
            read(aposJson);
            fail("expected DateTimeParseException");
        } catch (JsonProcessingException e) {
            if (e.getCause() == null) {
                throw e;
            }
            if (!(e.getCause() instanceof DateTimeParseException)) {
                throw e.getCause();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void expectSuccess(Object exp, String aposJson) throws IOException {
        final MonthDay value = read(aposJson);
        notNull(value);
        expect(exp, value);
    }

    private MonthDay read(final String aposJson) throws IOException {
        return READER.readValue(aposToQuotes(aposJson));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
