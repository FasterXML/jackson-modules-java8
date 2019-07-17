package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import java.io.IOException;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class MonthDayDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(MonthDay.class);

    static class Wrapper {
        @JsonFormat(pattern="MM/dd")
        public MonthDay value;

        public Wrapper(MonthDay v) { value = v; }
        public Wrapper() { }
    }

    static class WrapperAsArray {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public MonthDay value;

        public WrapperAsArray(MonthDay v) { value = v; }
        public WrapperAsArray() { }
    }

    
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
            newMapper().configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
        			.readerFor(MonthDay.class).readValue("[]");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Unexpected token (END_ARRAY)");
           // OK
        }
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        MonthDay value = newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .readerFor(MonthDay.class).readValue(aposToQuotes("['--01-01']"));
        notNull(value);
        expect(MonthDay.of(Month.JANUARY, 1), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        MonthDay value = newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    			.readerFor(MonthDay.class).readValue("[]");
        assertNull(value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        assertEquals("The value is not correct.", MonthDay.of(Month.JANUARY, 17),
                MAPPER.readValue("\"--01-17\"", MonthDay.class));
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        assertEquals("The value is not correct.", MonthDay.of(Month.AUGUST, 21),
                MAPPER.readValue("\"--08-21\"", MonthDay.class));
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
               
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);
        TemporalAccessor value = mapper.readValue("[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", TemporalAccessor.class);
        assertEquals("The value is not correct.", monthDay, value);
    }

    @Test
    public void testFormatAnnotation() throws Exception
    {
        final Wrapper input = new Wrapper(MonthDay.of(12, 28));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":\"12/28\"}", json);

        Wrapper output = MAPPER.readValue(json, Wrapper.class);
        assertEquals(input.value, output.value);
    }

    @Test
    public void testFormatAnnotationArray() throws Exception
    {
        final WrapperAsArray input = new WrapperAsArray(MonthDay.of(12, 28));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":[12,28]}", json);

        // 13-May-2019, tatu: [modules-java#107] not fully implemented so can't yet test
/*        
        WrapperAsArray output = MAPPER.readValue(json, WrapperAsArray.class);
        assertEquals(input.value, output.value);
        */
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
