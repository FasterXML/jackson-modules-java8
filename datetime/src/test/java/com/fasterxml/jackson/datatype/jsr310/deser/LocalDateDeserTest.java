package com.fasterxml.jackson.datatype.jsr310.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

public class LocalDateDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(LocalDate.class);

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
            read(READER, "['2000-01-01']");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_STRING) within Array");
        }
    }

    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        // works even without the feature enabled
        assertNull(read(READER, "[]"));
    }

    @Test
    public void testDeserializationCaseInsensitiveEnabledOnValue() throws Throwable
    {
        ObjectMapper mapper = newMapper();
        Value format = JsonFormat.Value
        		.forPattern("dd-MMM-yyyy")
        		.withFeature(Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        mapper.configOverride(LocalDate.class).setFormat(format);
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        String[] jsons = new String[] {"'01-Jan-2000'","'01-JAN-2000'", "'01-jan-2000'"};
        for(String json : jsons) {
            expectSuccess(reader, LocalDate.of(2000, Month.JANUARY, 1), json);
        }
    }
    
    @Test
    public void testDeserializationCaseInsensitiveEnabled() throws Throwable
    {
        ObjectMapper mapper = newMapper().configure(DeserializationFeature.ACCEPT_CASE_INSENSITIVE_VALUES, true);
        mapper.configOverride(LocalDate.class).setFormat(JsonFormat.Value.forPattern("dd-MMM-yyyy"));
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        String[] jsons = new String[] {"'01-Jan-2000'","'01-JAN-2000'", "'01-jan-2000'"};
        for(String json : jsons) {
            expectSuccess(reader, LocalDate.of(2000, Month.JANUARY, 1), json);
        }
    }
    
    @Test
    public void testDeserializationCaseInsensitiveDisabled() throws Throwable
    {
        ObjectMapper mapper = newMapper().configure(DeserializationFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false);
        mapper.configOverride(LocalDate.class).setFormat(JsonFormat.Value.forPattern("dd-MMM-yyyy"));
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        expectSuccess(reader, LocalDate.of(2000, Month.JANUARY, 1), "'01-Jan-2000'");
    }
    
    @Test
    public void testDeserializationCaseInsensitiveDisabled_InvalidDate() throws Throwable
    {
        ObjectMapper mapper = newMapper().configure(DeserializationFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false);
        mapper.configOverride(LocalDate.class).setFormat(JsonFormat.Value.forPattern("dd-MMM-yyyy"));
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        String[] jsons = new String[] {"'01-JAN-2000'", "'01-jan-2000'"};
        for(String json : jsons) {
        	expectFailure(reader, json);
        }
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        String json="['2000-01-01']";
        LocalDate value= newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .readerFor(LocalDate.class).readValue(aposToQuotes(json));
        notNull(value);
        expect(LocalDate.of(2000, 1, 1), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        String json="[]";
        LocalDate value= newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                .readerFor(LocalDate.class).readValue(aposToQuotes(json));
        assertNull(value);
    }

    private void expectFailure(String json) throws Throwable {
    	expectFailure(READER, json);
    }
    
    private void expectFailure(ObjectReader reader, String json) throws Throwable {
        try {
            read(reader, json);
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
    	expectSuccess(READER, exp, json);
    }
    private void expectSuccess(ObjectReader reader, Object exp, String json) throws IOException {
        final LocalDate value = read(reader, json);
        notNull(value);
        expect(exp, value);
    }

    private LocalDate read(ObjectReader reader, final String json) throws IOException {
        return reader.readValue(aposToQuotes(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
