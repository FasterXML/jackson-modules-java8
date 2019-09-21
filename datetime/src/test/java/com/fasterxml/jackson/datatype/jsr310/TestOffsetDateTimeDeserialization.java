package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import org.junit.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestOffsetDateTimeDeserialization extends ModuleTestBase
{
    private final static ObjectMapper MAPPER = newMapper();
    private final static ObjectReader READER = MAPPER.readerFor(OffsetDateTime.class);

    public static class WithoutContextTimezoneDateFieldBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public OffsetDateTime date;
    }

    public static class WithContextTimezoneDateFieldBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", with = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public OffsetDateTime date;
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(quote("2000-01-01T12:00Z")));
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 7, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(quote("2000-01-01T12:00+05:00")));
    }

    // [modules-java8#34]
    @Test
    public void testDeserializationWithShortFraction() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2017, 7, 25, 20, 22, 58, 800_000_000, ZoneOffset.UTC),
                READER.readValue(quote("2017-07-25T20:22:58.8Z")));
    }
    
    @Test
    public void testDeserializationAsString03() throws Exception
    {
        //
        // Verify that the offset in the json is preserved when we disable ADJUST_DATES_TO_CONTEXT_TIME_ZONE
        //
        ObjectReader reader2 = newMapper().disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE).readerFor(OffsetDateTime.class);
        OffsetDateTime parsed = reader2.readValue(aposToQuotes("'2000-01-01T12:00+05:00'"));
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(5)), parsed) ;
    }

    @Test
    public void testDeserializationWithContextTimezoneFeatureOverride() throws Exception
    {
        String inputStr = "{\"date\":\"2016-05-13T17:24:40.545+03\"}";
        WithContextTimezoneDateFieldBean result = newMapper().setTimeZone(TimeZone.getTimeZone("UTC")).
                disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE).readValue(inputStr, WithContextTimezoneDateFieldBean.class);
        assertEquals("The value is not correct.",
                OffsetDateTime.of(2016, 5, 13, 14, 24, 40, 545000000, ZoneOffset.UTC), result.date);
    }

    @Test
    public void testDeserializationWithoutContextTimezoneFeatureOverride() throws Exception
    {
        String inputStr = "{\"date\":\"2016-05-13T17:24:40.545+03\"}";
        WithoutContextTimezoneDateFieldBean result = newMapper().setTimeZone(TimeZone.getTimeZone("UTC")).
                enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE).readValue(inputStr, WithoutContextTimezoneDateFieldBean.class);
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2016, 5, 13, 17, 24, 40, 545000000, ZoneOffset.ofHours(3)), result.date);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure(quote("notanoffsetdatetime"));
    }

    @Test
    public void testDeserializationAsWithZeroZoneOffset01() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(quote("2000-01-01T12:00+00:00")));
    }

    @Test
    public void testDeserializationAsWithZeroZoneOffset02() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(quote("2000-01-01T12:00+0000")));
    }

    @Test
    public void testDeserializationAsWithZeroZoneOffset03() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(quote("2000-01-01T12:00+00")));
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("['2000-01-01T12:00+00']");
    		    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "START_ARRAY token");
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
        }
        try {
    		    newMapper()
    		        .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    		        .readerFor(OffsetDateTime.class)
    		        .readValue("[]");
    		    fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            // 25-Jul-2017, tatu: Ideally should note it's really missing value but...
            verifyException(e, "Unexpected token (END_ARRAY)");
        }
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        OffsetDateTime value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue("[\"2000-01-01T12:00+00\"]");
        assertEquals("The value is not correct.", OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        String json="[]";
        OffsetDateTime value = READER
    			.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    			.readValue(aposToQuotes(json));
        assertNull(value);
    }
    
    private void expectFailure(String json) throws Exception {
        try {
            READER.readValue(aposToQuotes(json));
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            Throwable t = e.getCause();
            if (t == null) {
                fail("Should have `cause` for exception: "+e);
            }
            if (!(t instanceof DateTimeParseException)) {
                fail("Should have DateTimeParseException as root cause, had: "+t);
            }
        }
    }
}
