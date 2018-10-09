package com.fasterxml.jackson.datatype.jsr310;

import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.temporal.TemporalAmount;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class TestDurationDeserialization extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(Duration.class);

    @Test
    public void testDeserializationAsFloat01() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60.0");

        assertNotNull("The value should not be null.", value);
        Duration exp = Duration.ofSeconds(60L, 0);
        assertEquals("The value is not correct.", exp,  value);
    }

    @Test
    public void testDeserializationAsFloat02() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60.0");

        assertNotNull("The value should not be null.", value);
        Duration exp = Duration.ofSeconds(60L, 0);
        assertEquals("The value is not correct.", exp, value);
    }

    @Test
    public void testDeserializationAsFloat03() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498.000008374");
        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 8374), value);
    }

    @Test
    public void testDeserializationAsFloat04() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498.000008374");
        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 8374), value);
    }

    @Test(expected = DateTimeException.class)
    public void testDeserializationAsFloat05() throws Exception
    {
        String customInstant = new BigInteger(Long.toString(Long.MAX_VALUE)).add(BigInteger.ONE) + ".0";
        READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(customInstant);
    }

    /**
     * This test can potentially hang the VM, so exit if it doesn't finish
     * within a few seconds.
     * @throws Exception
     */
    @Test(timeout=3000, expected = DateTimeException.class)
    public void testDeserializationAsFloatWhereStringTooLarge() throws Exception
    {
        String customDuration = "1000000000e1000000000";
        READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(customDuration);
    }

    @Test
    public void testDeserializationAsInt01() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60");
        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(60L, 0),  value);
    }

    @Test
    public void testDeserializationAsInt02() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("60000");

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(60L, 0),  value);
    }

    @Test
    public void testDeserializationAsInt03() throws Exception
    {
        Duration value = READER.with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498");

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 0),  value);
    }

    @Test
    public void testDeserializationAsInt04() throws Exception
    {
        Duration value = READER.without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("13498000");

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 0),  value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        Duration exp = Duration.ofSeconds(60L, 0);
        Duration value = READER.readValue('"' + exp.toString() + '"');

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", exp,  value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        Duration exp = Duration.ofSeconds(13498L, 8374);
        Duration value = READER.readValue('"' + exp.toString() + '"');

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", exp,  value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        Duration value = READER.readValue("\"   \"");
        assertNull("The value should be null.", value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);

        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498.000008374]", TemporalAmount.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", duration, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498]", TemporalAmount.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L), value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + "13498837]", TemporalAmount.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", Duration.ofSeconds(13498L, 837000000), value);
    }

    @Test
    public void testDeserializationWithTypeInfo04() throws Exception
    {
        Duration duration = Duration.ofSeconds(13498L, 8374);

        String prefix = "[\"" + Duration.class.getName() + "\",";

        ObjectMapper mapper = newMapper();
        mapper.addMixIn(TemporalAmount.class, MockObjectConfiguration.class);
        TemporalAmount value = mapper.readValue(prefix + '"' + duration.toString() + "\"]", TemporalAmount.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Duration.", value instanceof Duration);
        assertEquals("The value is not correct.", duration, value);
    }
    
    @Test
    public void testDeserializationAsArrayDisabled() throws Exception {
    	Duration exp = Duration.ofSeconds(13498L, 8374);
    	try {
	        READER.readValue("[\"" + exp.toString() + "\"]");
	        fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        }
    }
    
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[]");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize instance of `java.time.Duration` out of START_ARRAY");
        }
        try {
            newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
        	        .readerFor(Duration.class).readValue(aposToQuotes("[]"));
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Unexpected token (END_ARRAY), expected one of");
        }
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Exception {
    	  Duration exp = Duration.ofSeconds(13498L, 8374);
          Duration value = newMapper()
      			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
      			.readerFor(Duration.class).readValue("[\"" + exp.toString() + "\"]");

          assertNotNull("The value should not be null.", value);
          assertEquals("The value is not correct.", exp,  value);
    }
   
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
    	String json="[]";
    	Duration value= newMapper()
    			.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
    			.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
    			.readerFor(Duration.class).readValue(aposToQuotes(json));
    	assertNull(value);
    }
}
