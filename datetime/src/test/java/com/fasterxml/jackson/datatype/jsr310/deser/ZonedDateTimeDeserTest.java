package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ZonedDateTimeDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(ZonedDateTime.class);
    private final TypeReference<Map<String, ZonedDateTime>> MAP_TYPE_REF = new TypeReference<Map<String, ZonedDateTime>>() { };

    static class WrapperWithFeatures {
        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public ZonedDateTime value;
    }

    static class WrapperWithReadTimestampsAsNanosDisabled {
        @JsonFormat(
            without=Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
        )
        public ZonedDateTime value;

        public WrapperWithReadTimestampsAsNanosDisabled() { }
        public WrapperWithReadTimestampsAsNanosDisabled(ZonedDateTime v) { value = v; }
    }

    static class WrapperWithReadTimestampsAsNanosEnabled {
        @JsonFormat(
            with=Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
        )
        public ZonedDateTime value;

        public WrapperWithReadTimestampsAsNanosEnabled() { }
        public WrapperWithReadTimestampsAsNanosEnabled(ZonedDateTime v) { value = v; }
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(q("2000-01-01T12:00Z")));
    }

    @Test
    public void testDeserializationAsInt01() throws Exception
    {
        ObjectReader reader = newMapper().readerFor(WrapperWithReadTimestampsAsNanosDisabled.class);
        ZonedDateTime date = ZonedDateTime.of(
            LocalDateTime.ofEpochSecond(1, 1000000, ZoneOffset.UTC),
            ZoneOffset.UTC);
        WrapperWithReadTimestampsAsNanosDisabled actual =
            reader.readValue(a2q("{'value':1001}"));
        assertEquals("The value is not correct.", date, actual.value);
    }

    @Test
    public void testDeserializationAsInt02() throws Exception
    {
        ObjectReader reader = newMapper().readerFor(WrapperWithReadTimestampsAsNanosEnabled.class);
        ZonedDateTime date = ZonedDateTime.of(
            LocalDateTime.ofEpochSecond(1, 0, ZoneOffset.UTC),
            ZoneOffset.UTC);
        WrapperWithReadTimestampsAsNanosEnabled actual =
            reader.readValue(a2q("{'value':1}"));
        assertEquals("The value is not correct.", date, actual.value);
    }

    @Test
    public void testDeserializationComparedToStandard() throws Throwable
    {
        String inputString = "2021-02-01T19:49:04.0513486Z";

        assertEquals("The value is not correct.",
                DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(inputString, ZonedDateTime::from),
                READER.readValue(q(inputString)));
    }

    @Test
    public void testDeserializationComparedToStandard2() throws Throwable
    {
        String inputString = "2021-02-01T19:49:04.0513486Z[UTC]";

        ZonedDateTime converted = newMapper()
                .configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false)
                .readerFor(ZonedDateTime.class).readValue(q(inputString));

        assertEquals("The value is not correct.",
                DateTimeFormatter.ISO_ZONED_DATE_TIME.parse(inputString, ZonedDateTime::from),
                converted);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure(q("notazone"));
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
        			.readerFor(ZonedDateTime.class).readValue(a2q(json));
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
    			.readerFor(ZonedDateTime.class).readValue(a2q(json));
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                value);

    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
    	String json="[]";
    	ZonedDateTime value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    			.readerFor(ZonedDateTime.class).readValue(a2q(json));
    	assertNull(value);
    }

    @Test
    public void testDeserializationWithZonePreserved() throws Throwable
    {
        WrapperWithFeatures wrapper = newMapper()
                .readerFor(WrapperWithFeatures.class)
                .readValue("{\"value\":\"2000-01-01T12:00+01:00\"}");
        assertEquals("Timezone should be preserved.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0 ,0, ZoneOffset.ofHours(1)),
                wrapper.value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "zoneDateTime";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, ZonedDateTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        ZonedDateTime actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        Map<String, ZonedDateTime> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        ZonedDateTime actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting", null, actualDateFromEmptyStr);
    }

    @Test ( expected =  MismatchedInputException.class)
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "zonedDateTime";
        final ObjectMapper mapper = mapperBuilder().build();
        mapper.configOverride(ZonedDateTime.class)
                .setFormat(JsonFormat.Value.forLeniency(false));

        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, ZonedDateTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        objectReader.readValue(valueFromEmptyStr);
    }

    /*
    /**********************************************************
    / Tests for Iso 8601s ZonedDateTimes that are colonless
    /**********************************************************
    */

    @Test
    public void testDeserializationWithoutColonInOffset() throws Throwable
    {
        WrapperWithFeatures wrapper = newMapper()
                .readerFor(WrapperWithFeatures.class)
                .readValue("{\"value\":\"2000-01-01T12:00+0100\"}");

        assertEquals("Value parses as if it were with colon",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0 ,0, ZoneOffset.ofHours(1)),
                wrapper.value);
    }

    @Test
    public void testDeserializationWithoutColonInTimeZoneWithTZDB() throws Throwable
    {
        WrapperWithFeatures wrapper = newMapper()
                .readerFor(WrapperWithFeatures.class)
                .readValue("{\"value\":\"2000-01-01T12:00+0100[Europe/Paris]\"}");
        assertEquals("Timezone should be preserved.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0 ,0, ZoneId.of("Europe/Paris")),
                wrapper.value);
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
}
