/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

public class PeriodDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final TypeReference<Map<String, Period>> MAP_TYPE_REF = new TypeReference<Map<String, Period>>() { };

    @Test
    public void testDeserialization01() throws Exception
    {
        Period period = Period.of(1, 6, 15);
        Period value = MAPPER.readValue('"' + period.toString() + '"', Period.class);
        assertEquals("The value is not correct.", period, value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        Period period = Period.of(0, 0, 21);
        Period value = MAPPER.readValue('"' + period.toString() + '"', Period.class);
        assertEquals("The value is not correct.", period, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        Period period = Period.of(5, 1, 12);

        final ObjectMapper mapper = mapperBuilder()
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .build();
        TemporalAmount value = mapper.readValue(
                "[\"" + Period.class.getName() + "\",\"" + period.toString() + "\"]", TemporalAmount.class
                );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Period.", value instanceof Period);
        assertEquals("The value is not correct.", period, value);
    }

       /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    @Test
    public void testLenientDeserializeFromEmptyString() throws Exception {

        String key = "period";
        ObjectMapper mapper = newMapper();
        ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, Period> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        Period actualDateFromNullStr = actualMapFromNullStr.get(key);
        assertNull(actualDateFromNullStr);

        String valueFromEmptyStr = mapper.writeValueAsString(asMap(key, ""));
        Map<String, Period> actualMapFromEmptyStr = objectReader.readValue(valueFromEmptyStr);
        Period actualDateFromEmptyStr = actualMapFromEmptyStr.get(key);
        assertEquals("empty string failed to deserialize to null with lenient setting",null, actualDateFromEmptyStr);
    }

    @Test( expected =  MismatchedInputException.class)
    public void testStrictDeserializeFromEmptyString() throws Exception {

        final String key = "period";
        final ObjectMapper mapper = mapperBuilder()
                .withConfigOverride(Period.class,
                        o -> o.setFormat(JsonFormat.Value.forLeniency(false)))
                .build();
        final ObjectReader objectReader = mapper.readerFor(MAP_TYPE_REF);

        String valueFromNullStr = mapper.writeValueAsString(asMap(key, null));
        Map<String, Period> actualMapFromNullStr = objectReader.readValue(valueFromNullStr);
        assertNull(actualMapFromNullStr.get(key));

        String valueFromEmptyStr = mapper.writeValueAsString(asMap("date", ""));
        objectReader.readValue(valueFromEmptyStr);
    }
}
