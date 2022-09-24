package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class YearMonthDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(YearMonth.class);
    private final TypeReference<Map<String, YearMonth>> MAP_TYPE_REF = new TypeReference<Map<String, YearMonth>>() { };

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(YearMonth.of(2000, Month.JANUARY), "'2000-01'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Exception
    {
        expectFailure("'notayearmonth'");
    }
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Exception
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
    public void testDeserializationAsEmptyArrayDisabled() throws Exception
    {
        // works even without the feature enabled
        assertNull(read("[]"));
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Exception
    {
        String json="['2000-01']";
        YearMonth value= newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .readerFor(YearMonth.class).readValue(a2q(json));
        notNull(value);
        expect(YearMonth.of(2000, Month.JANUARY), value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Exception
    {
        String json="[]";
        YearMonth value = newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                .readerFor(YearMonth.class).readValue(a2q(json));
        assertNull(value);
    }

    // [modules-java8#249
    @Test
    public void testYearAbove10k() throws Exception
    {
        YearMonth input = YearMonth.of(10000, 1);
        String json = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        YearMonth result = READER.readValue(json);
        expect(input, result);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "yearMonth";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String dateValAsEmptyStr = "";

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, YearMonth> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        YearMonth actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, dateValAsEmptyStr));
        Map<String, YearMonth> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        YearMonth actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting",null, actualDateFromEmptyStr);
    }

    @Test( expected =  MismatchedInputException.class)
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "YearMonth";
        final ObjectMapper mapper = mapperBuilder().build();
        mapper.configOverride(YearMonth.class)
                .setFormat(JsonFormat.Value.forLeniency(false));
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, YearMonth> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap("date", ""));
        objectReader.readValue(valueFromEmptyStr);
    }

    private void expectFailure(String json) throws Exception {
        try {
            read(json);
            fail("expected DateTimeParseException");
        } catch (JsonProcessingException e) {
            if (e.getCause() == null) {
                throw e;
            }
            if (!(e.getCause() instanceof DateTimeParseException)) {
                throw (Exception) e.getCause();
            }
        } catch (IOException e) {
            throw e;
        }
    }

    private void expectSuccess(Object exp, String json) throws Exception {
        final YearMonth value = read(json);
        notNull(value);
        expect(exp, value);
    }

    private YearMonth read(final String json) throws Exception {
        return READER.readValue(a2q(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
