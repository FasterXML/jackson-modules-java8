package tools.jackson.datatype.jsr310;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class TestOffsetDateTimeDeserialization extends ModuleTestBase
{
    private final static ObjectMapper MAPPER = newMapper();
    private final static ObjectReader READER = MAPPER.readerFor(OffsetDateTime.class);

    public static class WithContextTimezoneDateFieldBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", with = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public OffsetDateTime date;
    }

    public static class WithoutContextTimezoneDateFieldBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public OffsetDateTime date;
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(q("2000-01-01T12:00Z")));
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 7, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(q("2000-01-01T12:00+05:00")));
    }

    // [modules-java8#34]
    @Test
    public void testDeserializationWithShortFraction() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2017, 7, 25, 20, 22, 58, 800_000_000, ZoneOffset.UTC),
                READER.readValue(q("2017-07-25T20:22:58.8Z")));
    }
    
    @Test
    public void testDeserializationAsString03() throws Exception
    {
        //
        // Verify that the offset in the json is preserved when we disable ADJUST_DATES_TO_CONTEXT_TIME_ZONE
        //
        ObjectReader reader2 = READER
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        OffsetDateTime parsed = reader2.readValue(a2q("'2000-01-01T12:00+05:00'"));
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(5)), parsed) ;
    }

    @Test
    public void testDeserializationWithContextTimezoneFeatureOverride() throws Exception
    {
        String inputStr = "{\"date\":\"2016-05-13T17:24:40.545+03\"}";
        WithContextTimezoneDateFieldBean result = newMapper(TimeZone.getTimeZone("UTC"))
                .readerFor(WithContextTimezoneDateFieldBean.class)
                .without(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(inputStr);
        assertEquals("The value is not correct.",
                OffsetDateTime.of(2016, 5, 13, 14, 24, 40, 545000000, ZoneOffset.UTC), result.date);
    }

    @Test
    public void testDeserializationWithoutContextTimezoneFeatureOverride() throws Exception
    {
        String inputStr = "{\"date\":\"2016-05-13T17:24:40.545+03\"}";
        WithoutContextTimezoneDateFieldBean result = newMapper(TimeZone.getTimeZone("UTC"))
                .readerFor(WithoutContextTimezoneDateFieldBean.class)
                .with(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .readValue(inputStr);
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2016, 5, 13, 17, 24, 40, 545000000, ZoneOffset.ofHours(3)), result.date);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            READER.readValue(q("notanoffsetdatetime"));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to deserialize `java.time.OffsetDateTime`");
        }
    }

    @Test
    public void testDeserializationAsWithZeroZoneOffset01() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(q("2000-01-01T12:00+00:00")));
    }

    @Test
    public void testDeserializationAsWithZeroZoneOffset02() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(q("2000-01-01T12:00+0000")));
    }

    @Test
    public void testDeserializationAsWithZeroZoneOffset03() throws Exception
    {
        assertEquals("The value is not correct.", 
                OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                READER.readValue(q("2000-01-01T12:00+00")));
    }

    @Test
    public void testDeserializationAsArrayDisabled()
    {
        try {
            READER.readValue("['2000-01-01T12:00+00']");
            fail("expected InputMismatchException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type `java.time.OffsetDateTime` from Array value");
        }
    }
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[]");
            fail("expected InputMismatchException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type `java.time.OffsetDateTime` from Array value");
        }
        try {
    		    READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    		        .readValue("[]");
    		    fail("expected InputMismatchException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type `");
            verifyException(e, "from Array value");
        }
    }

    @Test
    public void testDeserializationAsArrayEnabled()
    {
        OffsetDateTime value = READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue("[\"2000-01-01T12:00+00\"]");
        assertEquals("The value is not correct.", OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC),
                value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled()
    {
        OffsetDateTime value = READER
    			.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
    			        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    			.readValue("[]");
        assertNull(value);
    }
}
