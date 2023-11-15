package com.fasterxml.jackson.datatype.jsr310.failing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Test;

import java.io.IOException;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Map;

import static org.junit.Assert.*;

// for [modules-java8#274]
public class MonthDeser274Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(Month.class);
    private final TypeReference<Map<String, Month>> MAP_TYPE_REF = new TypeReference<Map<String, Month>>() { };

    static class Wrapper {
        @JsonFormat(pattern="MM")
        public Month value;

        public Wrapper(Month v) { value = v; }
        public Wrapper() { }
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        expectSuccess(Month.of(1), "'01'");
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        expectSuccess(Month.of(1), "'JANUARY'");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure("'notamonth'");
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        assertEquals("The value is not correct.", Month.of(1),
                MAPPER.readValue("1", Month.class));
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        assertEquals("The value is not correct.", Month.of(8),
                MAPPER.readValue("\"08\"", Month.class));
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        Month month = Month.of(11);
        TemporalAccessor value = mapper.readValue("[\"" + Month.class.getName() + "\",\"11\"]", TemporalAccessor.class);
        assertEquals("The value is not correct.", month, value);
    }

    @Test
    public void testFormatAnnotation() throws Exception
    {
        final Wrapper input = new Wrapper(Month.of(12));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":\"12\"}", json);

        Wrapper output = MAPPER.readValue(json, Wrapper.class);
        assertEquals(input.value, output.value);
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
        final String key = "month";

        // First: by default, lenient, so empty String fine
        final ObjectReader objectReader = MAPPER.readerFor(MAP_TYPE_REF);

        String doc = MAPPER.writeValueAsString(asMap(key, null));
        Map<String, Month> actualMapFromNullStr = objectReader.readValue(doc);
        assertNull(actualMapFromNullStr.get(key));

        doc = MAPPER.writeValueAsString(asMap(key, ""));
        assertNotNull(objectReader.readValue(doc));

        // But can make strict:
        final ObjectMapper strictMapper = mapperBuilder().build();
        strictMapper.configOverride(Month.class)
                .setFormat(JsonFormat.Value.forLeniency(false));
        doc = strictMapper.writeValueAsString(asMap("date", ""));
        try {
            strictMapper.readerFor(MAP_TYPE_REF)
                    .readValue(doc);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "not allowed because 'strict' mode set for");
        }
    }

    private void expectFailure(String aposJson) throws Throwable {
        try {
            read(aposJson);
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

    private void expectSuccess(Object exp, String aposJson) throws IOException {
        final Month value = read(aposJson);
        notNull(value);
        expect(exp, value);
    }

    private Month read(final String aposJson) throws IOException {
        return READER.readValue(a2q(aposJson));
    }

    private static void notNull(Object value) {
        assertNotNull("The value should not be null.", value);
    }

    private static void expect(Object exp, Object value) {
        assertEquals("The value is not correct.", exp,  value);
    }
}
