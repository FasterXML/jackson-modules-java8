package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Duration;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for [modules-java8#232]: {@link JavaTimeFeature#ALLOW_STRINGIFIED_DURATION_VALUES}.
 */
public class DurationDeser232Test extends ModuleTestBase
{
    static class SecondsWrapper {
        @JsonFormat(pattern = "SECONDS")
        public Duration value;
    }

    // NOTE: cannot use `ModuleTestBase.mapperBuilder()` here, since it already registers a
    // plain `JavaTimeModule` and duplicate registrations of the same module are ignored
    private static JsonMapper.Builder builderWithFeature() {
        return JsonMapper.builder()
                .defaultLocale(Locale.ENGLISH)
                .addModule(new JavaTimeModule()
                        .enable(JavaTimeFeature.ALLOW_STRINGIFIED_DURATION_VALUES));
    }

    private final ObjectMapper MAPPER = builderWithFeature().build();
    private final ObjectReader READER = MAPPER.readerFor(Duration.class);
    private final ObjectReader DEFAULT_READER = newMapper().readerFor(Duration.class);

    @Test
    public void testStringifiedIntegerFailsByDefault() throws Exception
    {
        assertThrows(InvalidFormatException.class,
                () -> DEFAULT_READER.readValue(q("3600")));
    }

    @Test
    public void testIsoStringWorksByDefault() throws Exception
    {
        assertEquals(Duration.ofSeconds(3600L), DEFAULT_READER.readValue(q("PT3600S")));
    }

    @Test
    public void testStringifiedIntegerWhenEnabled() throws Exception
    {
        assertEquals(Duration.ofSeconds(3600L), READER.readValue(q("3600")));
    }

    @Test
    public void testStringifiedNegativeIntegerWhenEnabled() throws Exception
    {
        assertEquals(Duration.ofSeconds(-3600L), READER.readValue(q("-3600")));
    }

    @Test
    public void testStringifiedIntegerMillisWhenNanosDisabled() throws Exception
    {
        Duration value = READER
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(q("60000"));
        assertEquals(Duration.ofSeconds(60L), value);
    }

    @Test
    public void testStringifiedDecimalWhenEnabled() throws Exception
    {
        assertEquals(Duration.ofSeconds(60L, 500_000_000),
                READER.readValue(q("60.5")));
    }

    @Test
    public void testIsoStringStillWorksWhenEnabled() throws Exception
    {
        assertEquals(Duration.ofSeconds(25L), READER.readValue(q("PT25S")));
    }

    @Test
    public void testJsonFormatSecondsAppliesToStringifiedInteger() throws Exception
    {
        SecondsWrapper w = MAPPER.readerFor(SecondsWrapper.class)
                .readValue("{\"value\":\"3600\"}");
        assertEquals(Duration.ofSeconds(3600L), w.value);
    }

    @Test
    public void testJsonFormatSecondsDoesNotApplyWhenDisabled() throws Exception
    {
        assertThrows(InvalidFormatException.class,
                () -> newMapper().readerFor(SecondsWrapper.class)
                        .readValue("{\"value\":\"3600\"}"));
    }

    @Test
    public void testNonNumericStringStillFailsWhenEnabled() throws Exception
    {
        assertThrows(InvalidFormatException.class,
                () -> READER.readValue(q("not-a-duration")));
    }
}
