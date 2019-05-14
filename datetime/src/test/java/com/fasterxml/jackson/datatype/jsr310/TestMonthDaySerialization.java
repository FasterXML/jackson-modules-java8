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

    static class WrapperAsArray {
        @JsonFormat(shape = JsonFormat.Shape.ARRAY)
        public MonthDay value;

        public WrapperAsArray(MonthDay v) { value = v; }
        public WrapperAsArray() { }
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
        assertEquals("The value is not correct.", "\"--01-17\"",
                MAPPER.writeValueAsString(MonthDay.of(Month.JANUARY, 17)));
    }

    @Test
    public void testSerialization02() throws Exception
    {
        assertEquals("The value is not correct.", "\"--08-21\"",
                MAPPER.writeValueAsString(MonthDay.of(Month.AUGUST, 21)));
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);
        String value = mapper.writeValueAsString(monthDay);
        assertEquals("The value is not correct.", "[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        assertEquals("The value is not correct.", MonthDay.of(Month.JANUARY, 17),
                MAPPER.readValue("\"--01-17\"", MonthDay.class));
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        assertEquals("The value is not correct.", MonthDay.of(Month.AUGUST, 21),
                MAPPER.readValue("\"--08-21\"", MonthDay.class));
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
               
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);
        TemporalAccessor value = mapper.readValue("[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", TemporalAccessor.class);
        assertEquals("The value is not correct.", monthDay, value);
    }

    @Test
    public void testFormatAnnotation() throws Exception
    {
        final Wrapper input = new Wrapper(MonthDay.of(12, 28));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":\"12/28\"}", json);

        Wrapper output = MAPPER.readValue(json, Wrapper.class);
        assertEquals(input.value, output.value);
    }

    @Test
    public void testFormatAnnotationArray() throws Exception
    {
        final WrapperAsArray input = new WrapperAsArray(MonthDay.of(12, 28));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":[12,28]}", json);

        // 13-May-2019, tatu: [modules-java#107] not fully implemented so can't yet test
/*        
        WrapperAsArray output = MAPPER.readValue(json, WrapperAsArray.class);
        assertEquals(input.value, output.value);
        */
    }
}
