package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestLocalDateTimeDeserialization extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(LocalDateTime.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(LocalDateTime.of(2000, Month.JANUARY, 1, 12, 0), "'2000-01-01T12:00'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notalocaldatetime'");
    }
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read("['2000-01-01T12:00']");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (VALUE_STRING) within Array");
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
        LocalDateTime value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(aposToQuotes("['2000-01-01T12:00']"));
        notNull(value);
        expect(LocalDateTime.of(2000, 1, 1, 12, 0, 0, 0), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        String json="[]";
    	    LocalDateTime value = ObjectMapper.builder()
    	            .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
    	                    DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    	            .build()
    	            .readerFor(LocalDateTime.class).readValue(aposToQuotes(json));
    	    assertNull(value);
    }

    @Test
    public void testDateTimeExceptionIsHandled() throws Throwable
    {
        LocalDateTime now = LocalDateTime.now();
        DeserializationProblemHandler handler = new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdStringValue(DeserializationContext ctxt, Class<?> targetType,
                   String valueToConvert, String failureMsg) throws IOException {
                if (LocalDateTime.class == targetType) {
                    if ("now".equals(valueToConvert)) {
                        return now;
                    }
                }
                return NOT_HANDLED;
            }
        };
        ObjectMapper mapper = newMapperBuilder()
                .addHandler(handler)
                .build();
        assertEquals(now, mapper.readValue(quote("now"), LocalDateTime.class));
    }

    @Test
    public void testUnexpectedTokenIsHandled() throws Throwable
    {
        LocalDateTime now = LocalDateTime.now();
        DeserializationProblemHandler handler = new DeserializationProblemHandler() {
            @Override
            public Object handleUnexpectedToken(DeserializationContext ctxt, Class<?> targetType,
                   JsonToken t, JsonParser p, String failureMsg) throws IOException {
                if (LocalDateTime.class == targetType) {
                    if (t.isBoolean()) {
                        return now;
                    }
                }
                return NOT_HANDLED;
            }
        };
        ObjectMapper mapper = newMapperBuilder()
                .addHandler(handler)
                .build();
        assertEquals(now, mapper.readValue("true", LocalDateTime.class));
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
        final LocalDateTime value = read(json);
        notNull(value);
        expect(exp, value);
    }

    private LocalDateTime read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
