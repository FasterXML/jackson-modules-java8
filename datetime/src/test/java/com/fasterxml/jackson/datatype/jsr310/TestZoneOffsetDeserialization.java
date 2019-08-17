package com.fasterxml.jackson.datatype.jsr310;

import java.io.IOException;
import java.time.ZoneOffset;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static org.junit.Assert.assertEquals;
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
            READER.readValue("\"notazonedoffset\"");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Invalid ID for ZoneOffset");
        }
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[\"+0300\"]");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "out of START_ARRAY");
        }
    }

    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[]");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "out of START_ARRAY");
        }
        try {
            mapperBuilder()
        			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
        			.build()
        			.readerFor(ZoneOffset.class).readValue("[]");
    		    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Unexpected token (END_ARRAY)");
        }
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        ZoneOffset value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.readValue(aposToQuotes("['+0300']"));
        expect(ZoneOffset.of("+0300"), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        ZoneOffset value = READER
    			.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
    			        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    			.readValue("[]");
        assertNull(value);
    }

    private void expectSuccess(Object exp, String json) throws IOException {
        final ZoneOffset value = read(json);
        expect(exp, value);
    }

    private ZoneOffset read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
