package com.fasterxml.jackson.datatype.jsr310.key;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Test;

import java.time.Year;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class YearAsKeyTest extends ModuleTestBase
{
    private static final TypeReference<Map<Year, String>> TYPE_REF = new TypeReference<Map<Year, String>>() {
    };
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization() throws Exception {
        assertEquals("Value is incorrect", mapAsString("3141", "test"),
                MAPPER.writeValueAsString(asMap(Year.of(3141), "test")));
    }

    @Test
    public void testDeserialization() throws Exception {
        assertEquals("Value is incorrect", asMap(Year.of(3141), "test"),
                READER.readValue(mapAsString("3141", "test")));
    }

    @Test(expected = InvalidFormatException.class)
    public void deserializeYearKey_notANumber() throws Exception {
        READER.readValue(mapAsString("10000BC", "test"));
    }

    @Test(expected = InvalidFormatException.class)
    public void deserializeYearKey_notAYear() throws Exception {
        READER.readValue(mapAsString(Integer.toString(Year.MAX_VALUE+1), "test"));
    }

    @Test
    public void serializeAndDeserializeYearKeyUnpadded() throws Exception {

        Year year1 = Year.of(1);
        Float float1 = 1F;

        Map<Year, Float> testMap = Collections.singletonMap(year1, 1F);

        String serialized = MAPPER.writeValueAsString(testMap);

        TypeReference<Map<Year, Float>> yearFloatTypeReference = new TypeReference<Map<Year, Float>>() {};

        Map<Year, Float> deserialized = MAPPER.readValue(serialized, yearFloatTypeReference);
        Float floatVal = deserialized.get(year1);
        assertEquals("Value is incorrect", String.valueOf(1F), floatVal.toString());
    }
}
