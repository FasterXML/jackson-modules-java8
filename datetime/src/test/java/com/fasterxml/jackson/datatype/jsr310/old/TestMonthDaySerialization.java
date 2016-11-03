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
import java.time.MonthDay;
import java.time.temporal.TemporalAccessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMonthDaySerialization extends ModuleTestBase
{
    private ObjectMapper mapper;

    @Before
    public void setUp()
    {
        this.mapper = newMapper();
    }

    @After
    public void tearDown() { }

    @Test
    public void testSerialization01() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.JANUARY, 17);

        String value = this.mapper.writeValueAsString(monthDay);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"--01-17\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.AUGUST, 21);

        String value = this.mapper.writeValueAsString(monthDay);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"--08-21\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);

        this.mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(monthDay);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.JANUARY, 17);

        MonthDay value = this.mapper.readValue("\"--01-17\"", MonthDay.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", monthDay, value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.AUGUST, 21);

        MonthDay value = this.mapper.readValue("\"--08-21\"", MonthDay.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", monthDay, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);

        this.mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
        TemporalAccessor value = this.mapper.readValue("[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", TemporalAccessor.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a MonthDay.", value instanceof MonthDay);
        assertEquals("The value is not correct.", monthDay, value);
    }
}
