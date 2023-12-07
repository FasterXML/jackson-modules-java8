package com.fasterxml.jackson.datatype.jsr310.deser;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.io.IOException;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

import static java.lang.String.format;
import static org.junit.Assert.*;

public class MonthDeserTest extends ModuleTestBase
{
    static class Wrapper {
        @JsonFormat(pattern="MM")
        public Month value;

        public Wrapper(Month v) { value = v; }
    }

    @Test
    public void testDeserializationAsString01_feature() throws Exception
    {
        ObjectReader READER = JsonMapper.builder()
            .addModule(new JavaTimeModule().enable(JavaTimeFeature.ONE_BASED_MONTHS))
            .build()
            .readerFor(Month.class);

        assertEquals(Month.JANUARY, READER.readValue("\"01\""));
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
    public void testBadDeserializationAsString01() {
        assertError(
            () -> readerForOneBased().readValue("\"notamonth\""),
            InvalidFormatException.class,
            "Cannot deserialize value of type `java.time.Month` from String \"notamonth\": not one of the values accepted for Enum class: [OCTOBER, SEPTEMBER, JUNE, MARCH, MAY, APRIL, JULY, JANUARY, FEBRUARY, DECEMBER, AUGUST, NOVEMBER]"
        );
    }

    static void assertError(ThrowingRunnable codeToRun, Class<? extends Throwable> expectedException, String expectedMessage) {
        try {
            codeToRun.run();
            fail(format("Expecting %s, but nothing was thrown!", expectedException.getName()));
        } catch (Throwable actualException) {
            if (!expectedException.isInstance(actualException)) {
                fail(format("Expecting exception of type %s, but %s was thrown instead", expectedException.getName(), actualException.getClass().getName()));
            }
            if (actualException.getMessage() == null || !actualException.getMessage().contains(expectedMessage)) {
                fail(format("Expecting exception with message containing:'%s', but the actual error message was:'%s'", expectedMessage, actualException.getMessage()));
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
    public void testDeserializationWithTypeInfo01_feature() throws Exception
    {
        ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule().enable(JavaTimeFeature.ONE_BASED_MONTHS));
        MAPPER.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        TemporalAccessor value = MAPPER.readValue("[\"java.time.Month\",11]", TemporalAccessor.class);
        assertEquals(Month.NOVEMBER, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01_default() throws Exception
    {
        ObjectMapper MAPPER = new ObjectMapper();
        MAPPER.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        TemporalAccessor value = MAPPER.readValue("[\"java.time.Month\",\"11\"]", TemporalAccessor.class);
        assertEquals(Month.DECEMBER, value);
    }


    @Test
    public void _testDeserializationWithTypeInfo01_default() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        TemporalAccessor value = mapper.readValue("[\"java.time.Month\",\"11\"]", TemporalAccessor.class);
        assertEquals(Month.DECEMBER, value);
    }

    @Test
    public void _testDeserializationWithTypeInfo01_feature() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule().enable(JavaTimeFeature.ONE_BASED_MONTHS));
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        TemporalAccessor value = mapper.readValue("[\"java.time.Month\", 11]", TemporalAccessor.class);
        assertEquals(Month.NOVEMBER, value);
    }

    @Test
    public void testFormatAnnotation() throws Exception
    {
        ObjectMapper MAPPER = newMapper();
        String json = newMapper().writeValueAsString(new Wrapper(Month.DECEMBER));
        assertEquals("{\"value\":\"12\"}", json);

        Wrapper output = MAPPER.readValue(json, Wrapper.class);
        assertEquals(new Wrapper(Month.of(12)).value, output.value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    // minor changes in 2.12
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

    private void expectFailure(String aposJson) throws Throwable {
        try {
            newMapper().registerModule(new JavaTimeModule())
                .readerFor(Month.class)
                .readValue(aposJson);
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
