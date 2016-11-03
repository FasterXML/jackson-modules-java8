package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectReader;

import org.junit.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class TestOffsetDateTimeDeserialization extends ModuleTestBase
{
   private final ObjectReader READER = newMapper().readerFor(OffsetDateTime.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC), "'2000-01-01T12:00Z'");
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        expectSuccess(OffsetDateTime.of(2000, 1, 1, 7, 0, 0, 0, ZoneOffset.UTC), "'2000-01-01T12:00+05:00'");
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        //
        // Verify that the offset in the json is preserved when we disable ADJUST_DATES_TO_CONTEXT_TIME_ZONE
        //
        ObjectReader reader2 = newMapper().disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE).readerFor(OffsetDateTime.class);
        OffsetDateTime parsed = reader2.readValue(aposToQuotes("'2000-01-01T12:00+05:00'"));
        notNull(parsed);
        expect(OffsetDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(5)), parsed) ;
    }

    public static class WithContextTimezoneDateFieldBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", with = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public OffsetDateTime date;
    }

    @Test
    public void testDeserializationWithContextTimezoneFeatureOverride() throws Exception
    {
        String inputStr = "{\"date\":\"2016-05-13T17:24:40.545+03\"}";
        WithContextTimezoneDateFieldBean result = newMapper().setTimeZone(TimeZone.getTimeZone("UTC")).
                disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE).readValue(inputStr, WithContextTimezoneDateFieldBean.class);
        notNull(result);
        expect(OffsetDateTime.of(2016, 5, 13, 14, 24, 40, 545000000, ZoneOffset.UTC), result.date);
    }

    public static class WithoutContextTimezoneDateFieldBean {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX", without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public OffsetDateTime date;
    }

    @Test
    public void testDeserializationWithoutContextTimezoneFeatureOverride() throws Exception
    {
        String inputStr = "{\"date\":\"2016-05-13T17:24:40.545+03\"}";
        WithoutContextTimezoneDateFieldBean result = newMapper().setTimeZone(TimeZone.getTimeZone("UTC")).
                enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE).readValue(inputStr, WithoutContextTimezoneDateFieldBean.class);
        notNull(result);
        expect(OffsetDateTime.of(2016, 5, 13, 17, 24, 40, 545000000, ZoneOffset.ofHours(3)), result.date);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notanoffsetdatetime'");
    }

    private void expectFailure(String json) throws Exception {
        try {
            read(json);
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

    private void expectSuccess(Object exp, String json) throws IOException {
        final OffsetDateTime value = read(json);
        notNull(value);
        expect(exp, value);
    }

    private OffsetDateTime read(final String json) throws IOException {
        return READER.readValue(aposToQuotes(json));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
