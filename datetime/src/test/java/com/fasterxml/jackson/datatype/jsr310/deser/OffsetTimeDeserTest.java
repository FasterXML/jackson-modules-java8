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
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import java.io.IOException;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OffsetTimeDeserTest extends ModuleTestBase
{

    private final TypeReference<Map<String, OffsetTime>> MAP_TYPE_REF = new TypeReference<Map<String, OffsetTime>>() { };

    // for [datatype-jsr310#45]
    static class  Pojo45s {
        public String name;
        public List<Pojo45> objects;
    }

    static class Pojo45 {
        public java.time.LocalDate partDate;
        public java.time.OffsetTime starttime;
        public java.time.OffsetTime endtime;
        public String comments;
    }

    static class WrapperWithReadTimestampsAsNanosDisabled {
        @JsonFormat(
            without=Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
        )
        public OffsetTime value;

        public WrapperWithReadTimestampsAsNanosDisabled() { }
        public WrapperWithReadTimestampsAsNanosDisabled(OffsetTime v) { value = v; }
    }

    static class WrapperWithReadTimestampsAsNanosEnabled {
        @JsonFormat(
            with=Feature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS
        )
        public OffsetTime value;

        public WrapperWithReadTimestampsAsNanosEnabled() { }
        public WrapperWithReadTimestampsAsNanosEnabled(OffsetTime v) { value = v; }
    }

    private final ObjectReader READER = newMapper().readerFor(OffsetTime.class);

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        OffsetTime time = OffsetTime.of(15, 43, 0, 0, ZoneOffset.of("+0300"));
        OffsetTime value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
               .readValue("[15,43,\"+0300\"]");

        assertNotNull(value, "The value should not be null.");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        OffsetTime time = OffsetTime.of(9, 22, 57, 0, ZoneOffset.of("-0630"));
        OffsetTime value = READER.readValue("[9,22,57,\"-06:30\"]");

        assertNotNull(value, "The value should not be null.");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp03Nanoseconds() throws Exception
    {
        OffsetTime time = OffsetTime.of(9, 22, 0, 57, ZoneOffset.of("-0630"));
        OffsetTime value = READER
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
               .readValue("[9,22,0,57,\"-06:30\"]");

        assertNotNull(value, "The value should not be null.");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp03Milliseconds() throws Exception {
        OffsetTime time = OffsetTime.of(9, 22, 0, 57000000, ZoneOffset.of("-0630"));
        OffsetTime value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
               .readValue("[9,22,0,57,\"-06:30\"]");

        assertNotNull(value, "The value should not be null.");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp04Nanoseconds() throws Exception {
        OffsetTime time = OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"));
        OffsetTime value = READER
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
               .readValue("[22,31,5,829837,\"+11:00\"]");

        assertNotNull(value, "The value should not be null.");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds01() throws Exception
    {
        OffsetTime time = OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"));
        OffsetTime value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
               .readValue("[22,31,5,829837,\"+11:00\"]");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds02() throws Exception
    {
        OffsetTime time = OffsetTime.of(22, 31, 5, 829000000, ZoneOffset.of("+1100"));
        OffsetTime value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
               .readValue("[22,31,5,829,\"+11:00\"]");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp05Nanoseconds() throws Exception
    {
        ObjectReader reader = newMapper().readerFor(WrapperWithReadTimestampsAsNanosEnabled.class);
        OffsetTime time = OffsetTime.of(9, 22, 0, 57, ZoneOffset.of("-0630"));
        WrapperWithReadTimestampsAsNanosEnabled actual = reader
            .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .readValue(a2q("{'value':[9,22,0,57,'-06:30']}"));

        assertNotNull(actual, "The value should not be null.");
        assertEquals(time, actual.value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp05Milliseconds01() throws Exception
    {
        ObjectReader reader = newMapper().readerFor(WrapperWithReadTimestampsAsNanosDisabled.class);
        OffsetTime time = OffsetTime.of(9, 22, 0, 57000000, ZoneOffset.of("-0630"));
        WrapperWithReadTimestampsAsNanosDisabled actual = reader
            .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .readValue(a2q("{'value':[9,22,0,57,'-06:30']}"));

        assertNotNull(actual, "The value should not be null.");
        assertEquals(time, actual.value, "The value is not correct.");
    }

    @Test
    public void testDeserializationAsTimestamp05Milliseconds02() throws Exception
    {
        ObjectReader reader = newMapper().readerFor(WrapperWithReadTimestampsAsNanosDisabled.class);
        OffsetTime time = OffsetTime.of(9, 22, 0, 4257, ZoneOffset.of("-0630"));
        WrapperWithReadTimestampsAsNanosDisabled actual = reader
            .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .readValue(a2q("{'value':[9,22,0,4257,'-06:30']}"));

        assertNotNull(actual, "The value should not be null.");
        assertEquals(time, actual.value, "The value is not correct.");
    }

    @Test
    public void testDeserializationFromString01() throws Exception
    {
        OffsetTime time = OffsetTime.of(15, 43, 0, 0, ZoneOffset.of("+0300"));
        OffsetTime value = READER.readValue('"' + time.toString() + '"');
        assertEquals(time, value, "The value is not correct.");

        time = OffsetTime.of(9, 22, 57, 0, ZoneOffset.of("-0630"));
        value = READER.readValue('"' + time.toString() + '"');
        assertEquals(time, value, "The value is not correct.");

        time = OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"));
        value = READER.readValue('"' + time.toString() + '"');
        assertEquals(time, value, "The value is not correct.");

        expectSuccess(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC), "'12:00Z'");
    }

    @Test
    public void testBadDeserializationFromString01() throws Throwable
    {
        expectFailure(q("notanoffsettime"));
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        OffsetTime time = OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"));

        final ObjectMapper mapper = newMapper();
        mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        Temporal value = mapper.readValue(
                "[\"" + OffsetTime.class.getName() + "\",[22,31,5,829837,\"+11:00\"]]", Temporal.class
                );

        assertNotNull(value, "The value should not be null.");
        assertInstanceOf(OffsetTime.class, value, "The value should be a OffsetTime.");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        OffsetTime time = OffsetTime.of(22, 31, 5, 422000000, ZoneOffset.of("+1100"));

        final ObjectMapper mapper = newMapper();
        mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        Temporal value = mapper.readValue(
                "[\"" + OffsetTime.class.getName() + "\",[22,31,5,422,\"+11:00\"]]", Temporal.class
                );

        assertNotNull(value, "The value should not be null.");
        assertInstanceOf(OffsetTime.class, value, "The value should be a OffsetTime.");
        assertEquals(time, value, "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        OffsetTime time = OffsetTime.of(22, 31, 5, 829837, ZoneOffset.of("+1100"));

        final ObjectMapper mapper = newMapper();
        mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = mapper.readValue(
                "[\"" + OffsetTime.class.getName() + "\",\"" + time.toString() + "\"]", Temporal.class
        );
        assertTrue(value instanceof OffsetTime, "The value should be a OffsetTime.");
        assertEquals(time, value, "The value is not correct.");
    }

    // for [datatype-jsr310#45]
    @Test
    public void testDeserOfArrayOf() throws Exception
    {
        final String JSON = a2q
                ("{'name':'test','objects':[{'partDate':[2015,10,13],'starttime':[15,7,'+0'],'endtime':[2,14,'+0'],'comments':'in the comments'}]}");
        Pojo45s result = READER.forType(Pojo45s.class).readValue(JSON);
        assertNotNull(result);
        assertNotNull(result.objects);
        assertEquals(1, result.objects.size());
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read("['12:00Z']");
    	        fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
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
        OffsetTime value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.readValue(a2q("['12:00Z']"));
        expect(OffsetTime.of(12, 0, 0, 0, ZoneOffset.UTC), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        OffsetTime value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                    DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .readValue("[]");
        assertNull(value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "OffsetTime";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, OffsetTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        OffsetTime actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        Map<String, OffsetTime> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        OffsetTime actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals(null, actualDateFromEmptyStr, "empty string failed to deserialize to null with lenient setting");
    }

    @Test
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "OffsetTime";
        final ObjectMapper mapper = mapperBuilder().build();
        mapper.configOverride(OffsetTime.class)
                .setFormat(JsonFormat.Value.forLeniency(false));

        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, OffsetTime> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        assertThrows(MismatchedInputException.class, () -> objectReader.readValue(valueFromEmptyStr));
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
        }
    }

    private void expectSuccess(Object exp, String json) throws IOException {
        final OffsetTime value = read(json);
        assertNotNull(value, "The value should not be null.");
        assertEquals(exp, value, "The value is not correct.");
    }

    private OffsetTime read(final String json) throws IOException {
        return READER.readValue(a2q(json));
    }

    private static void notNull(Object value) {
        assertNotNull(value, "The value should not be null.");
    }

    private static void expect(Object exp, Object value) {
        assertEquals(exp, value, "The value is not correct.");
    }
}
