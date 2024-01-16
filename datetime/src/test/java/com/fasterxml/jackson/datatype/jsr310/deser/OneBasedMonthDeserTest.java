package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Month;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.Assert.*;

public class OneBasedMonthDeserTest extends ModuleTestBase
{
    static class Wrapper {
        public Month value;

        public Wrapper(Month v) { value = v; }
        public Wrapper() { }
    }

    @Test
    public void testDeserializationAsString01_oneBased() throws Exception
    {
        assertEquals(Month.JANUARY, readerForOneBased().readValue("\"01\""));
    }

    @Test
    public void testDeserializationAsString01_zeroBased() throws Exception
    {
        assertEquals(Month.FEBRUARY, readerForZeroBased().readValue("\"01\""));
    }


    @Test
    public void testDeserializationAsString02_oneBased() throws Exception
    {
        assertEquals(Month.JANUARY, readerForOneBased().readValue("\"JANUARY\""));
    }

    @Test
    public void testDeserializationAsString02_zeroBased() throws Exception
    {
        assertEquals(Month.JANUARY, readerForZeroBased().readValue("\"JANUARY\""));
    }

    @Test
    public void testBadDeserializationAsString01_oneBased() {
        assertError(
            () -> readerForOneBased().readValue("\"notamonth\""),
            InvalidFormatException.class,
            "Cannot deserialize value of type `java.time.Month` from String \"notamonth\": not one of the values accepted for Enum class: [OCTOBER, SEPTEMBER, JUNE, MARCH, MAY, APRIL, JULY, JANUARY, FEBRUARY, DECEMBER, AUGUST, NOVEMBER]"
        );
    }

    static void assertError(ThrowingRunnable codeToRun, Class<? extends Throwable> expectedException, String expectedMessage) {
        try {
            codeToRun.run();
            fail(String.format("Expecting %s, but nothing was thrown!", expectedException.getName()));
        } catch (Throwable actualException) {
            if (!expectedException.isInstance(actualException)) {
                fail(String.format("Expecting exception of type %s, but %s was thrown instead", expectedException.getName(), actualException.getClass().getName()));
            }
            if (actualException.getMessage() == null || !actualException.getMessage().contains(expectedMessage)) {
                fail(String.format("Expecting exception with message containing:'%s', but the actual error message was:'%s'", expectedMessage, actualException.getMessage()));
            }
        }
    }


    @Test
    public void testDeserialization01_zeroBased() throws Exception
    {
        assertEquals(Month.FEBRUARY, readerForZeroBased().readValue("1"));
    }

    @Test
    public void testDeserialization01_oneBased() throws Exception
    {
        assertEquals(Month.JANUARY, readerForOneBased().readValue("1"));
    }

    @Test
    public void testDeserialization02_zeroBased() throws Exception
    {
        assertEquals(Month.SEPTEMBER, readerForZeroBased().readValue("\"08\""));
    }

    @Test
    public void testDeserialization02_oneBased() throws Exception
    {
        assertEquals(Month.AUGUST, readerForOneBased().readValue("\"08\""));
    }

    @Test
    public void testDeserializationWithTypeInfo01_oneBased() throws Exception
    {
        ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule().enable(JavaTimeFeature.ONE_BASED_MONTHS));
        MAPPER.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        TemporalAccessor value = MAPPER.readValue("[\"java.time.Month\",11]", TemporalAccessor.class);
        assertEquals(Month.NOVEMBER, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01_zeroBased() throws Exception
    {
        ObjectMapper MAPPER = new ObjectMapper();
        MAPPER.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        TemporalAccessor value = MAPPER.readValue("[\"java.time.Month\",\"11\"]", TemporalAccessor.class);
        assertEquals(Month.DECEMBER, value);
    }

    @Test
    public void testFormatAnnotation_zeroBased() throws Exception
    {
        Wrapper output = readerForZeroBased().readValue("{\"value\":\"11\"}", Wrapper.class);
        assertEquals(new Wrapper(Month.DECEMBER).value, output.value);
    }

    @Test
    public void testFormatAnnotation_oneBased() throws Exception
    {
        Wrapper output = readerForOneBased().readValue("{\"value\":\"11\"}", Wrapper.class);
        assertEquals(new Wrapper(Month.NOVEMBER).value, output.value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testDeserializeFromEmptyString() throws Exception
    {
        // First: by default, lenient, so empty String fine
        TypeReference<Map<String, Month>> MAP_TYPE_REF = new TypeReference<Map<String, Month>>() { };
        ObjectReader objectReader = newMapper().registerModule(new JavaTimeModule())
            .readerFor(MAP_TYPE_REF);

        Map<String, Month> map = objectReader.readValue("{\"month\":null}");
        assertNull(map.get("month"));

        Map<String, Month> map2 = objectReader.readValue("{\"month\":\"\"}");
        assertNotNull(map2);

        // But can make strict:
        ObjectMapper strictMapper = mapperBuilder()
            .addModule(new JavaTimeModule())
            .build();
        strictMapper.configOverride(Month.class)
            .setFormat(JsonFormat.Value.forLeniency(false));

        try {
            strictMapper.readerFor(MAP_TYPE_REF).readValue("{\"date\":\"\"}");
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "not allowed because 'strict' mode set for");
        }
    }

    private ObjectReader readerForZeroBased() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule().disable(JavaTimeFeature.ONE_BASED_MONTHS))
                .build()
                .readerFor(Month.class);
    }

    private ObjectReader readerForOneBased() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule().enable(JavaTimeFeature.ONE_BASED_MONTHS))
                .build()
                .readerFor(Month.class);
    }

}
