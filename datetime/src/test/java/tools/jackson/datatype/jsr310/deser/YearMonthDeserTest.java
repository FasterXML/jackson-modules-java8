package tools.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;
import java.time.YearMonth;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class YearMonthDeserTest extends ModuleTestBase
{
    private final ObjectReader READER = newMapper().readerFor(YearMonth.class);
    private final TypeReference<Map<String, YearMonth>> MAP_TYPE_REF = new TypeReference<Map<String, YearMonth>>() { };

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        final YearMonth value = read("'2000-01'");
        assertEquals("The value is not correct", YearMonth.of(2000, Month.JANUARY), value);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            read(q("notayearmonth"));
            fail("expected DateTimeParseException");
        } catch (InvalidFormatException e) {
            verifyException(e, "could not be parsed");
        }
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            read("['2000-01']");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e,
"Unexpected token (`JsonToken.VALUE_STRING`), expected `JsonToken.VALUE_NUMBER_INT`");
        }
    }

    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        // works even without the feature enabled
        assertNull(read("[]"));
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        YearMonth value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue(a2q("['2000-01']"));
        assertEquals("The value is not correct", YearMonth.of(2000, Month.JANUARY), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        YearMonth value = READER.with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .readValue( "[]");
        assertNull(value);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "yearMonth";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String dateValAsEmptyStr = "";

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, YearMonth> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        YearMonth actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, dateValAsEmptyStr));
        Map<String, YearMonth> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        YearMonth actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting",null, actualDateFromEmptyStr);
    }

    @Test( expected =  MismatchedInputException.class)
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "YearMonth";
        final ObjectMapper mapper = mapperBuilder()
                .withConfigOverride(YearMonth.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, YearMonth> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap("date", ""));
        objectReader.readValue(valueFromEmptyStr);
    }

    private YearMonth read(final String json) throws IOException {
        return READER.readValue(a2q(json));
    }
}
