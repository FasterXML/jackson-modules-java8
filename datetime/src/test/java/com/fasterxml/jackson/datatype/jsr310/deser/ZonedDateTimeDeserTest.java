package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ZonedDateTimeDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(ZonedDateTime.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")),
                READER.readValue(quote("2000-01-01T12:00Z")));
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure(quote("notazone"));
    }
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[\"2000-01-01T12:00Z\"]");
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
    	try {
    	    READER.readValue("[]");
    	    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    	try {
    		String json="[]";
        	newMapper()
        			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
        			.readerFor(ZonedDateTime.class).readValue(aposToQuotes(json));
    	    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        String json="['2000-01-01T12:00Z']";
        ZonedDateTime value = newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.readerFor(ZonedDateTime.class).readValue(aposToQuotes(json));
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")),
                value);

    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
    	String json="[]";
    	ZonedDateTime value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    			.readerFor(ZonedDateTime.class).readValue(aposToQuotes(json));
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
        }
    }
}
