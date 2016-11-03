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

package com.fasterxml.jackson.datatype.jsr310;

import java.time.Month;
import java.time.MonthDay;
import java.time.temporal.TemporalAccessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

public class TestMonthDaySerialization
{
    static class Wrapper {
        @JsonFormat(pattern="MM/dd")
        public MonthDay value;

        public Wrapper(MonthDay v) { value = v; }
        public Wrapper() { }
    }
    
    private ObjectMapper MAPPER;
    
    @Before
    public void setUp()
    {
        MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    }

    @Test
    public void testSerialization01() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.JANUARY, 17);

        String value = MAPPER.writeValueAsString(monthDay);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"--01-17\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.AUGUST, 21);

        String value = MAPPER.writeValueAsString(monthDay);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"--08-21\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);

        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);

        String value = mapper.writeValueAsString(monthDay);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.JANUARY, 17);

        MonthDay value = MAPPER.readValue("\"--01-17\"", MonthDay.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", monthDay, value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        MonthDay monthDay = MonthDay.of(Month.AUGUST, 21);

        MonthDay value = MAPPER.readValue("\"--08-21\"", MonthDay.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", monthDay, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
               
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);

        TemporalAccessor value = mapper.readValue("[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", TemporalAccessor.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a MonthDay.", value instanceof MonthDay);
        assertEquals("The value is not correct.", monthDay, value);
    }

    @Test
    public void testFormatAnnotation() throws Exception
    {
        Wrapper input = new Wrapper(MonthDay.of(12, 28));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":\"12/28\"}", json);

        Wrapper output = MAPPER.readValue(json, Wrapper.class);
        assertEquals(input.value, output.value);
    }
}
