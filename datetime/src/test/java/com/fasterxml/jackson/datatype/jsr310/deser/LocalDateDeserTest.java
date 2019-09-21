package com.fasterxml.jackson.datatype.jsr310.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

public class LocalDateDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(LocalDate.class);
    private final TypeReference<Map<String, LocalDate>> MAP_TYPE_REF = new TypeReference<Map<String, LocalDate>>() { };

    final static class Wrapper {
        @JsonFormat(pattern="yyyy_MM_dd'T'HH:mmZ",
                shape=JsonFormat.Shape.STRING)
        public LocalDate value;

        public Wrapper() { }
        public Wrapper(LocalDate v) { value = v; }
    }
    
    /*
    /**********************************************************
    /* Deserialization from Int array representation
    /**********************************************************
     */
    
    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        assertEquals("The value is not correct.", LocalDate.of(1986, Month.JANUARY, 17),
                READER.readValue("[1986,1,17]"));
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        assertEquals("The value is not correct.", LocalDate.of(2013, Month.AUGUST, 21),
                READER.readValue("[2013,8,21]"));
    }

    /*
    /**********************************************************
    /* Deserialization from String representation
    /**********************************************************
     */
    
    @Test
    public void testDeserializationAsString01() throws Exception
    {
        assertEquals("The value is not correct.", LocalDate.of(2000, Month.JANUARY, 1),
                READER.readValue(quote("2000-01-01")));

        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);
        assertEquals("The value is not correct.", date,
                READER.readValue('"' + date.toString() + '"'));

        date = LocalDate.of(2013, Month.AUGUST, 21);
        assertEquals("The value is not correct.", date,
                READER.readValue('"' + date.toString() + '"'));
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalDateTime date = LocalDateTime.now();
        assertEquals("The value is not correct.", date.toLocalDate(),
                READER.readValue('"' + date.toString() + '"'));
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        Instant instant = Instant.now();
        LocalDate value = READER.readValue('"' + instant.toString() + '"');
        assertEquals("The value is not correct.",
                LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate(),
                value);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            READER.readValue(quote("notalocaldate"));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type");
            verifyException(e, "from String \"");
        }
    }

    @Test
    public void testBadDeserializationAsString02() throws Exception
    {
        try {
            READER.readValue(quote("2015-06-19TShouldNotParse"));
            fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize value of type");
            verifyException(e, "from String \"");
        }
    }
    
    /*
    /**********************************************************
    /* Deserialization from alternate representation: int (number
    /* of days since Epoch)
    /**********************************************************
     */

    // By default, lenient handling on so we can do this:
    @Test
    public void testLenientDeserializeFromInt() throws Exception
    {
        assertEquals("The value is not correct.", LocalDate.of(1970, Month.JANUARY, 3),
                READER.readValue("2"));

        assertEquals("The value is not correct.", LocalDate.of(1970, Month.FEBRUARY, 10),
                READER.readValue("40"));
    }

    // But with alternate setting, not so
    @Test
    public void testStricDeserializeFromInt() throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
                .build();
        mapper.configOverride(LocalDate.class)
            .setFormat(JsonFormat.Value.forLeniency(false));
        try {
            mapper.readValue("2", LocalDate.class);
            fail("Should not pass");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize instance of");
            verifyException(e, "not allowed because 'strict' mode set for property or type");
        }

        // 17-Aug-2019, tatu: Should possibly test other mechanism too, but for now let's
        //    be content with just one...
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
     */
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "date";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String dateValAsNullStr = null;
        String dateValAsEmptyStr = "";

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, dateValAsNullStr));
        Map<String, LocalDate> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        LocalDate actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, dateValAsEmptyStr));
        Map<String, LocalDate> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        LocalDate actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting",actualDateFromNullStr, actualDateFromEmptyStr);
    }

    @Test( expected =  MismatchedInputException.class)
    public void testStrictDeserializFromEmptyString() throws Exception {

        final String key = "date";
        final ObjectMapper mapper = mapperBuilder().build();
        mapper.configOverride(LocalDate.class)
            .setFormat(JsonFormat.Value.forLeniency(false));
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);
        final String dateValAsNullStr = null;

        // even with strict, null value should be deserialized without throwing an exception
        String valueFromNullStr = mapper.writeValueAsString(asMap(key, dateValAsNullStr));
        Map<String, LocalDate> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String dateValAsEmptyStr = "";
        // TODO: nothing stops us from writing an empty string, maybe there should be a check there too?
        String valueFromEmptyStr = mapper.writeValueAsString(asMap("date", dateValAsEmptyStr));
        // with strict, deserializing an empty string is not permitted
        objectReader.readValue(valueFromEmptyStr);
    }

    /*
    /**********************************************************
    /* Tests for alternate array handling
    /**********************************************************
     */
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read(READER, "[\"2000-01-01\"]");
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
        ObjectMapper mapper = newMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, true);
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
        ObjectMapper mapper = newMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false);
        mapper.configOverride(LocalDate.class).setFormat(JsonFormat.Value.forPattern("dd-MMM-yyyy"));
        ObjectReader reader = mapper.readerFor(LocalDate.class);
        expectSuccess(reader, LocalDate.of(2000, Month.JANUARY, 1), "'01-Jan-2000'");
    }
    
    @Test
    public void testDeserializationCaseInsensitiveDisabled_InvalidDate() throws Throwable
    {
        ObjectMapper mapper = newMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES, false);
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
        LocalDate actual = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue("[\"2000-01-01\"]");
        assertEquals("The value is not correct.", LocalDate.of(2000, 1, 1), actual);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        LocalDate value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue("[]");
        assertNull(value);
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

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
               .addMixIn(Temporal.class, MockObjectConfiguration.class)
               .build();
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);
        Temporal value = mapper.readValue(
                "[\"" + LocalDate.class.getName() + "\",\"" + date.toString() + "\"]", Temporal.class
                );
        assertEquals("The value is not correct.", date, value);
    }

    /*
    /**********************************************************
    /* Custom format
    /**********************************************************
     */

    // for [datatype-jsr310#37]
    @Test
    public void testCustomFormat() throws Exception
    {
        Wrapper w = MAPPER.readValue("{\"value\":\"2015_07_28T13:53+0300\"}", Wrapper.class);
        LocalDate date = w.value; 
        assertEquals(28, date.getDayOfMonth());
    }
}



