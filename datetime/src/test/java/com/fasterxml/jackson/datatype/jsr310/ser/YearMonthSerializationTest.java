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

package com.fasterxml.jackson.datatype.jsr310.ser;

import static org.junit.assertEquals;
import static org.junit.assertNotNull;
import static org.junit.assertTrue;

import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YearMonthSerializationTest
	extends ModuleTestBase
{
    private static class SimpleAggregate
    {
        @JsonProperty("yearMonth")
        @JsonFormat(pattern = "yyMM")
        final YearMonth yearMonth;

        @JsonCreator
        SimpleAggregate(@JsonProperty("yearMonth") YearMonth yearMonth)
        {
            this.yearMonth = yearMonth;
        }
    }

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerializationAsTimestamp01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);
        String value = MAPPER.writer()
        		.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(yearMonth);

        assertNotNull(value);
        assertEquals("[1986,1]", value);
    }

    @Test
    public void testSerializationAsTmestamp02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        String value = MAPPER.writer()
        		.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(yearMonth);

        assertNotNull(value);
        assertEquals("[2013,8]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);
        String value = MAPPER.writer()
        		.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(yearMonth);

        assertNotNull(value);
        assertEquals('"' + yearMonth.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        String value = MAPPER.writer()
        		.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(yearMonth);

        assertNotNull(value);
        assertEquals('"' + yearMonth.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2005, Month.NOVEMBER);
        ObjectMapper mapper = newMapper()
        		.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        		.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = mapper.writeValueAsString(yearMonth);

        assertNotNull(value);
        assertEquals("The value is not correct.",
                "[\"" + YearMonth.class.getName() + "\",\"" + yearMonth.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);
        YearMonth value = MAPPER.readValue("[1986,1]", YearMonth.class);

        assertNotNull(value);
        assertEquals(yearMonth, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        YearMonth value = MAPPER.readValue("[2013,8]", YearMonth.class);

        assertNotNull(value);
        assertEquals(yearMonth, value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);
        YearMonth value = MAPPER.readValue('"' + yearMonth.toString() + '"', YearMonth.class);

        assertNotNull(value);
        assertEquals(yearMonth, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        YearMonth value = this.MAPPER.readValue('"' + yearMonth.toString() + '"', YearMonth.class);

        assertNotNull(value);
        assertEquals(yearMonth, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2005, Month.NOVEMBER);

        ObjectMapper mapper = newMapper()
        		.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = mapper.readValue("[\"" + YearMonth.class.getName() + "\",\"" + yearMonth.toString() + "\"]", Temporal.class);

        assertNotNull(value);
        assertTrue("The value should be a YearMonth.", value instanceof YearMonth);
        assertEquals(yearMonth, value);
    }

    @Test
    public void testSerializationWithPattern01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        SimpleAggregate simpleAggregate = new SimpleAggregate(yearMonth);

        String value = MAPPER.writeValueAsString(simpleAggregate);

        assertNotNull(value);
        assertEquals("{\"yearMonth\":\"1308\"}", value);
    }

    @Test
    public void testDeserializationWithPattern01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        SimpleAggregate simpleAggregate = new SimpleAggregate(yearMonth);

        SimpleAggregate value = MAPPER.readValue("{\"yearMonth\":\"1308\"}", SimpleAggregate.class);

        assertNotNull(value);
        assertEquals(simpleAggregate.yearMonth, value.yearMonth);
    }
}
