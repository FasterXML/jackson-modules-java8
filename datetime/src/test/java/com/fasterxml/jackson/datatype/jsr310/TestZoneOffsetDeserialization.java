package com.fasterxml.jackson.datatype.jsr310;

import java.io.IOException;
import java.time.ZoneOffset;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestZoneOffsetDeserialization extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(ZoneOffset.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(ZoneOffset.of("+0300"), "'+0300'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            read("'notazonedoffset'");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Invalid ID for ZoneOffset");
        }
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read("['+0300']");
    	        fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
           // OK
        }
    }
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        try {
    		read("[]");
    	    fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
           // OK
        }
        try {
    		String json="[]";
        	newMapper()
        			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
        			.readerFor(ZoneOffset.class).readValue(aposToQuotes(json));
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
        String json="['+0300']";
        ZoneOffset value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.readerFor(ZoneOffset.class).readValue(aposToQuotes(json));
        notNull(value);
        expect(ZoneOffset.of("+0300"), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        String json="[]";
        ZoneOffset value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    			.readerFor(ZoneOffset.class).readValue(aposToQuotes(json));
    	assertNull(value);
    }

    private void expectSuccess(Object exp, String json) throws IOException {
        final ZoneOffset value = read(json);
        notNull(value);
        expect(exp, value);
    }

    private ZoneOffset read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
