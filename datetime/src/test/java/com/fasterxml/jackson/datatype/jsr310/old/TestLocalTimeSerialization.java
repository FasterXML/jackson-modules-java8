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

import java.time.LocalTime;
import java.time.temporal.Temporal;

import org.junit.jupiter.api.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalTimeSerialization extends ModuleTestBase
{
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp()
    {
        this.mapper = newMapper();
    }

    @Test
    public void testSerializationAsTimestamp01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[15,43]", value);
    }

    @Test
    public void testSerializationAsTimestamp02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[9,22,57]", value);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 0, 57);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[9,22,0,57]", value);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 0, 57);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[9,22,0,0]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Nanoseconds() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[22,31,5,829837]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Milliseconds() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422829837);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[22,31,5,422]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43, 20);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("\"15:43:20\"", value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals('"' + time.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals('"' + time.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[\"" + LocalTime.class.getName() + "\",[22,31,5,829837]]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422829837);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        this.mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("[\"" + LocalTime.class.getName() + "\",[22,31,5,422]]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(time);

        assertNotNull(value);
        assertEquals("The value is not correct.",
                "[\"" + LocalTime.class.getName() + "\",\"" + time.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43);

        LocalTime value = this.mapper.readValue("[15,43]", LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);

        LocalTime value = this.mapper.readValue("[9,22,57]", LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Nanoseconds() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 0, 57);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        LocalTime value = this.mapper.readValue("[9,22,0,57]", LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Milliseconds() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 0, 57000000);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        LocalTime value = this.mapper.readValue("[9,22,0,57]", LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Nanoseconds() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        LocalTime value = this.mapper.readValue("[22,31,5,829837]", LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds01() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        LocalTime value = this.mapper.readValue("[22,31,5,829837]", LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds02() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829000000);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        LocalTime value = this.mapper.readValue("[22,31,5,829]", LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43);

        LocalTime value = this.mapper.readValue('"' + time.toString() + '"', LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);

        LocalTime value = this.mapper.readValue('"' + time.toString() + '"', LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        LocalTime value = this.mapper.readValue('"' + time.toString() + '"', LocalTime.class);

        assertNotNull(value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,829837]]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(LocalTime.class, value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422000000);

        this.mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,422]]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(LocalTime.class, value);
        assertEquals(time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + LocalTime.class.getName() + "\",\"" + time.toString() + "\"]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(LocalTime.class, value);
        assertEquals(time, value);
    }
}
