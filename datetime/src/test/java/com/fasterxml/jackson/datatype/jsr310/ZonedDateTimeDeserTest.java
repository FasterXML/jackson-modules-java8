package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;

import org.junit.Test;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ZonedDateTimeDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(ZonedDateTime.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")), "'2000-01-01T12:00Z'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notazone'");
    }
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
    	try {
    		read("['2000-01-01T12:00Z']");
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
    		read("[]");
    	    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    	try {
    		String json="[]";
        	newMapper()
            	.readerFor(ZonedDateTime.class)
            	.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
            	.readValue(aposToQuotes(json));
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
    			.readerFor(ZonedDateTime.class)
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.readValue(aposToQuotes(json));
    	notNull(value);
        expect(ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        String json="[]";
        ZonedDateTime value = newMapper()
                .readerFor(ZonedDateTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue(aposToQuotes(json));
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
        final ZonedDateTime value = read(json);
        notNull(value);
        expect(exp, value);
    }

    private ZonedDateTime read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
