package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static java.time.format.DateTimeFormatter.ISO_DATE;
        import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
        import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE;
        import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        import static java.time.format.DateTimeFormatter.ISO_OFFSET_TIME;
        import static java.time.format.DateTimeFormatter.ISO_TIME;
        import static java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME;
public class ZonedDateTimeDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(ZonedDateTime.class);

    private final ObjectMapper SpringObjectMapper = newMapper().registerModules(
        new JavaTimeModule()
                .addDeserializer(OffsetDateTime.class,
                                 new SpringInstantDeserializer()));

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")),
                READER.readValue(quote("2000-01-01T12:00Z")));
    }

    @Test
    public void testJavaBug_JDK_8176547_extendedFormat_succeeds() throws Exception
    {
        String extendedStr = "2017-01-01T00:00:00.000+00:00";
        ZonedDateTime.parse(extendedStr); // succeeds

        // https://bugs.openjdk.java.net/browse/JDK-8176547
        // Class java.time.ZonedDateTime cannot parse all ISO 8601 date formats

        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(ISO_LOCAL_DATE_TIME)
                .parseLenient()
            .appendOffsetId()
            .parseStrict().toFormatter();


        dtf.parse(extendedStr);
    }

    @Test
    public void testJavaBug_JDK_8176547_basicFormat_fails() throws Exception
    {
        String basicStr = "2017-01-01T00:00:00.000+0000";

        // https://bugs.openjdk.java.net/browse/JDK-8176547
        // Class java.time.ZonedDateTime cannot parse all ISO 8601 date formats
        //ZonedDateTime.parse(basicStr); // fails!
        // java.time.format.DateTimeParseException: Text '2017-01-01T00:00:00.000+0000' could not be parsed,
        // unparsed text found at index 26
//        DateTimeFormatter dtf = new DateTimeFormatterBuilder()
//                .parseCaseInsensitive()
//                .append(ISO_LOCAL_DATE_TIME)
//                .parseLenient()
//                .appendOffsetId()
//                .parseLenient().toFormatter();


 //       dtf.parse(basicStr); // this still fails, bug was labeled "won't fix"

    }

    @Test
    public void testJavaJDKBug_8032051() throws Exception
    {
        // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=8032051
        //        For date-time string formats both ISO 8601 and RFC 3339 allow either a long time zone offset including a
        //        colon and minutes like this: +01:00
        //        _and_ they allow a shorter offset without the colon and minutes like this: +01
        //
        //        The "ZonedDateTime" class included in Java 8's new java.time.* classes is documented as parsing
        //        ISO 8601-like strings. While true of the longer time zone offsets, the shorter offsets cause the
        //        class to throw a DateTimeParseException, like this:
        //        ZonedDateTime.parse("2017-01-01T00:00:00.000+00:00"); // succeeds

        ZonedDateTime zonedDateTime_x = ZonedDateTime.parse( "2017-01-01T00:00:00.000+00:00" );  // Succeeds.
        ZonedDateTime zonedDateTime_z = ZonedDateTime.parse( "2013-12-11T21:25:04.800842+01:00" );  // Succeeds.
        ZonedDateTime zonedDateTime_y = ZonedDateTime.parse( "2013-12-11T21:25:04.800842+01" );  // (bug fix)
    }

    @Test
    public void testJavaJDKBug_8210336() throws Exception
    {

//        The bug fix for  JDK-8032051
//        "ZonedDateTime" class "parse" method fails with short time zone offset ("+01")
//        fixed DateTimeFormatter.ISO_ZONED_DATE_TIME, but it seems like other DateTimeFormatter predefined
//        formatters have the same problem, and need a similar fix.  That is, any formatter that accepts an offset "+01:00"
//        should also accept "+01" unless there's an industry spec to the contrary.


    }

    @Test
    public void test_working() throws Exception
    {
        //no exceptions
        test(ISO_ZONED_DATE_TIME, "2013-12-11T21:25:04.800842+01:00");
        test(ISO_ZONED_DATE_TIME, "2013-12-11T21:25:04.800842+01");
        test(ISO_OFFSET_DATE_TIME, "2013-12-11T21:25:04.800842+01:00");
        test(ISO_OFFSET_DATE_TIME, "2013-12-11T21:25:04.800842+01");

        // these were fixed since the bug was reported, fixed in another bug
        test(ISO_DATE_TIME, "2013-12-11T21:25:04.800842+01:00");
        test(ISO_OFFSET_DATE, "2013-12-11+01:00");
        test(ISO_DATE, "2013-12-11+01:00");
    }



    @Test
    public void test_fail2() throws Exception
    {
        test(ISO_DATE_TIME, "2013-12-11T21:25:04.800842+01");
    }


    @Test
    public void test_fail4() throws Exception
    {
        test(ISO_OFFSET_DATE, "2013-12-11+01");
    }

    @Test
    public void test_fail6() throws Exception
    {
        test(ISO_DATE, "2013-12-11+01");
    }

    static void test(DateTimeFormatter formatter, String x) throws Exception {

            formatter.parse(x);

    }

    @Test
    public void testBasicFormatWithColonSeparator() throws Exception
    {
        String x = "2013-12-11T21:25:04.800842+01:00";
        READER.readValue("[\"2013-12-11T21:25:04.800842+01:00\"]");
    }

    @Test
    public void testBasicFormatNoColonSeparator() throws Exception
    {
        READER.readValue("[\"2013-12-11T21:25:04.800842+01:00\"]");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure(quote("notazone"));
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
            	.readerFor(ZonedDateTime.class)
            	.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
            	.readValue(aposToQuotes(json));
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
        ZonedDateTime value = newMapper()
    			.readerFor(ZonedDateTime.class)
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
    			.readValue("[\"2000-01-01T12:00Z\"]");
        assertEquals("The value is not correct.",
                ZonedDateTime.of(2000, 1, 1, 12, 0, 0, 0, ZoneId.of("UTC")),
                value);
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        String json="[]";
        ZonedDateTime value = newMapper()
                .readerFor(ZonedDateTime.class)
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                        DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readValue(aposToQuotes(json));
        assertNull(value);
    }

    private void expectFailure(String json) throws Throwable {
        try {
            READER.readValue(aposToQuotes(json));
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
