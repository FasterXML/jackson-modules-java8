package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Month;
import java.time.temporal.TemporalAccessor;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
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
        final ObjectMapper mapper = newMapper();

        // Nulls are handled in general way, not by deserializer so they are ok
        Month m = mapper.readerFor(Month.class).readValue(" null ");
        assertNull(m);

        // But coercion from empty String not enabled for Enums by default:
        try {
            mapper.readerFor(Month.class).readValue("\"\"");
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce empty String");
        }
        // But can allow coercion of empty String to, say, null
        ObjectMapper emptyStringMapper = mapperBuilder()
                .withCoercionConfig(Month.class,
                        h -> h.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull))
                .build();
        m = emptyStringMapper.readerFor(Month.class).readValue("\"\"");
        assertNull(m);
    }

    private ObjectReader readerForZeroBased() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule()
                        .disable(JavaTimeFeature.ONE_BASED_MONTHS))
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
