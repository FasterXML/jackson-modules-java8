package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Instant;
import java.util.Locale;

import org.junit.Test;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

// [modules-java8#291] InstantDeserializer fails to parse negative numeric timestamp strings for
//   pre-1970 values.
public class InstantDeser291Test 
    extends ModuleTestBase
{
    private final JsonMapper MAPPER = JsonMapper.builder()
        .defaultLocale(Locale.ENGLISH)
        .addModule(new JavaTimeModule()
            .enable(JavaTimeFeature.ALWAYS_ALLOW_STRINGIFIED_DATE_TIMESTAMPS))
        .build();
    private final ObjectReader READER = MAPPER.readerFor(Instant.class);
    private final ObjectReader READER_ALLOW_LEADING_PLUS = READER
        .withFeatures(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS);

    private static final Instant INSTANT_3_SEC_AFTER_EPOC = Instant.ofEpochSecond(3);
    private static final Instant INSTANT_3_SEC_BEFORE_EPOC = Instant.ofEpochSecond(-3);

    private static final String STR_3_SEC = "\"3.000000000\"";
    private static final String STR_POSITIVE_3 = "\"+3.000000000\"";
    private static final String STR_NEGATIVE_3 = "\"-3.000000000\"";

    /**
     * Baseline that always succeeds, even before resolution of issue 291
     * @throws Exception
     */
    @Test
    public void testNormalNumericalString() throws Exception {
        assertEquals(INSTANT_3_SEC_AFTER_EPOC, READER.readValue(STR_3_SEC));
    }

    /**
     * Should succeed after issue 291 is resolved.
     * @throws Exception
     */
    @Test
    public void testNegativeNumericalString() throws Exception {
        assertEquals(INSTANT_3_SEC_BEFORE_EPOC, READER.readValue(STR_NEGATIVE_3));
    }

    /**
     * This should always fail since {@link JsonReadFeature#ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS} is
     * not enabled
     * @throws Exception
     */
    @Test
    public void testPlusSignNumericalString() throws Exception {
        assertThrows(InvalidFormatException.class, () -> READER.readValue(STR_POSITIVE_3));
    }

    /**
     * Should succeed after issue 291 is resolved. Scenario where
     * {@link JsonReadFeature#ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS} is enabled
     * @throws Exception
     */
    @Test
    public void testAllowedPlusSignNumericalString() throws Exception {
        assertEquals(INSTANT_3_SEC_AFTER_EPOC, READER_ALLOW_LEADING_PLUS.readValue(STR_POSITIVE_3));
    }
}
