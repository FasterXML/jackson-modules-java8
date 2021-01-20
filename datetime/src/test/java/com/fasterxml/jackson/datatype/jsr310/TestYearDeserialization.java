package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.junit.Test;

import java.io.IOException;
import java.time.Year;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestYearDeserialization extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(Year.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(Year.of(2000), "'2000'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notayear'");
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[\"2000\"]");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
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
           // OK
        }
        try {
    		JsonMapper.builder()
    		    .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
              .build()
    		    .readerFor(Year.class).readValue("[]");
    		fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        }
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        Year value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(a2q("['2000']"));
        expect(Year.of(2000), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        Year value = READER
    			.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
    			        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    			.readValue("[]");
        assertNull(value);
    }

    private void expectFailure(String json) throws Throwable {
        try {
            READER.readValue(a2q(json));
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

    private void expectSuccess(Object exp, String json) throws IOException {
        assertEquals("The value is not correct.", exp,
                READER.readValue(a2q(json)));
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
