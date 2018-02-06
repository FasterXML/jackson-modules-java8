package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

public class LocalDateDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(LocalDate.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(LocalDate.of(2000, Month.JANUARY, 1), "'2000-01-01'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notalocaldate'");
    }
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue(aposToQuotes("['2000-01-01']"));
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_STRING) within Array");
        }
    }

    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        // works even without the feature enabled
        assertNull(READER.readValue(aposToQuotes("[]")));
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        LocalDate value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(aposToQuotes("['2000-01-01']"));
        expect(LocalDate.of(2000, 1, 1), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        LocalDate value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue("[]");
        assertNull(value);
    }

    private void expectFailure(String json) throws Throwable {
        try {
            READER.readValue(aposToQuotes(json));
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

    private void expectSuccess(Object exp, String json) throws IOException {
        final LocalDate value = READER.readValue(aposToQuotes(json));
        expect(exp, value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
