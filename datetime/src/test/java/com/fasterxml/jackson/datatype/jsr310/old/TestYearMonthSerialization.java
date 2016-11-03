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

package com.fasterxml.jackson.datatype.jsr310.old;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Before;
import org.junit.Test;

public class TestYearMonthSerialization extends ModuleTestBase
{
    private ObjectMapper mapper;

    @Before
    public void setUp()
    {
        this.mapper = newMapper();
    }

    @Test
    public void testSerializationAsTimestamp01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(yearMonth);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[1986,1]", value);
    }

    @Test
    public void testSerializationAsTmestamp02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(yearMonth);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[2013,8]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(yearMonth);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + yearMonth.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(yearMonth);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + yearMonth.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2005, Month.NOVEMBER);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(yearMonth);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "[\"" + YearMonth.class.getName() + "\",\"" + yearMonth.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        YearMonth value = this.mapper.readValue("[1986,1]", YearMonth.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", yearMonth, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        YearMonth value = this.mapper.readValue("[2013,8]", YearMonth.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", yearMonth, value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(1986, Month.JANUARY);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        YearMonth value = this.mapper.readValue('"' + yearMonth.toString() + '"', YearMonth.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", yearMonth, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        YearMonth value = this.mapper.readValue('"' + yearMonth.toString() + '"', YearMonth.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", yearMonth, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2005, Month.NOVEMBER);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue("[\"" + YearMonth.class.getName() + "\",\"" + yearMonth.toString() + "\"]", Temporal.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a YearMonth.", value instanceof YearMonth);
        assertEquals("The value is not correct.", yearMonth, value);
    }

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

    @Test
    public void testSerializationWithPattern01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        SimpleAggregate simpleAggregate = new SimpleAggregate(yearMonth);

        String value = this.mapper.writeValueAsString(simpleAggregate);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "{\"yearMonth\":\"1308\"}", value);
    }

    @Test
    public void testDeserializationWithPattern01() throws Exception
    {
        YearMonth yearMonth = YearMonth.of(2013, Month.AUGUST);
        SimpleAggregate simpleAggregate = new SimpleAggregate(yearMonth);

        SimpleAggregate value = this.mapper.readValue("{\"yearMonth\":\"1308\"}", SimpleAggregate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", simpleAggregate.yearMonth, value.yearMonth);
    }
}
