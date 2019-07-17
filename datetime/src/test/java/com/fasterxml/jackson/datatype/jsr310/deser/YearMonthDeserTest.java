package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import java.io.IOException;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class YearMonthDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(YearMonth.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(YearMonth.of(2000, Month.JANUARY), "'2000-01'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notayearmonth'");
    }
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
    	try {
    		read("['2000-01']");
    	    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
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
    	String json="['2000-01']";
    	YearMonth value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.readerFor(YearMonth.class).readValue(aposToQuotes(json));
    	notNull(value);
        expect(YearMonth.of(2000, Month.JANUARY), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
    	String json="[]";
    	YearMonth value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    			.readerFor(YearMonth.class).readValue(aposToQuotes(json));
    	assertNull(value);
    }


    private void expectFailure(String json) throws Throwable {
        try {
            read(json);
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
        final YearMonth value = read(json);
        notNull(value);
        expect(exp, value);
    }

    private YearMonth read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
