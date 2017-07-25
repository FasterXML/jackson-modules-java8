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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Before;
import org.junit.Test;

public class TestLocalTimeSerialization extends ModuleTestBase
{
    private ObjectMapper MAPPER;
    private ObjectReader reader;
    private ObjectWriter writer;

    @Before
    public void setUp()
    {
        MAPPER = newMapper();
        MAPPER.registerModule(new JavaTimeModule());
        reader = MAPPER.readerFor(LocalTime.class);
        writer = MAPPER.writer();
    }

    @Test
    public void testSerializationAsTimestamp01() throws Exception
    {
        String json = writer.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(LocalTime.of(15, 43));
        assertEquals("The value is not correct.", "[15,43]", json);
    }

    @Test
    public void testSerializationAsTimestamp02() throws Exception
    {
        String json = writer.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString( LocalTime.of(9, 22, 57));
        assertEquals("The value is not correct.", "[9,22,57]", json);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        String json = writer.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .writeValueAsString(LocalTime.of(9, 22, 0, 57));
        assertEquals("The value is not correct.", "[9,22,0,57]", json);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 0, 57);

        ObjectMapper mapper = newMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = mapper.writeValueAsString(time);

        assertEquals("The value is not correct.", "[9,22,0,0]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Nanoseconds() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        ObjectMapper mapper = newMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        String value = mapper.writeValueAsString(time);

        assertEquals("The value is not correct.", "[22,31,5,829837]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Milliseconds() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422829837);

        ObjectMapper mapper = newMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        String value = mapper.writeValueAsString(time);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[22,31,5,422]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43, 20);

        ObjectMapper mapper = newMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = mapper.writeValueAsString(time);

        assertEquals("The value is not correct.", "\"15:43:20\"", value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);

        ObjectMapper mapper = newMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = mapper.writeValueAsString(time);

        assertEquals("The value is not correct.", '"' + time.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        ObjectMapper m = newMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = m.writeValueAsString(time);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + time.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper m = newMapper();
        m.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.enable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        m.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String json = m.writeValueAsString(time);

System.err.println("DEBUG/1: "+json);
        assertEquals("The value is not correct.",
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,829837]]",
                json);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422829837);

        ObjectMapper m = newMapper();
        m.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        m.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String json = m.writeValueAsString(time);

System.err.println("DEBUG/2: "+json);
        assertEquals("The value is not correct.",
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,422]]",
                json);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper m = newMapper();
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = m.writeValueAsString(time);

        assertEquals("The value is not correct.",
                "[\"" + LocalTime.class.getName() + "\",\"" + time.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43);
        LocalTime value = reader.readValue("[15,43]");
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);
        LocalTime value = reader.readValue("[9,22,57]");
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsTimestamp03Nanoseconds() throws Exception
    {
        LocalTime value = reader
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[9,22,0,57]");
        assertEquals("The value is not correct.", LocalTime.of(9, 22, 0, 57), value);
    }

    @Test
    public void testDeserializationAsTimestamp03Milliseconds() throws Exception
    {
        LocalTime value = reader
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[9,22,0,57]");
        assertEquals("The value is not correct.", LocalTime.of(9, 22, 0, 57000000), value);
    }

    @Test
    public void testDeserializationAsTimestamp04Nanoseconds() throws Exception
    {
        LocalTime value = reader
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[22,31,5,829837]");
        assertEquals("The value is not correct.", LocalTime.of(22, 31, 5, 829837), value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds01() throws Exception
    {
        LocalTime value = reader
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[22,31,5,829837]");
        assertEquals("The value is not correct.", LocalTime.of(22, 31, 5, 829837), value);
    }

    @Test
    public void testDeserializationAsTimestamp04Milliseconds02() throws Exception
    {
        LocalTime value = reader
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("[22,31,5,829]");
        assertEquals("The value is not correct.", LocalTime.of(22, 31, 5, 829000000), value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43);
        LocalTime value = reader.readValue('"' + time.toString() + '"');
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);
        LocalTime value = reader.readValue('"' + time.toString() + '"');
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        LocalTime value = reader.readValue('"' + time.toString() + '"');
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = mapper.readValue(
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,829837]]", Temporal.class
                );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a LocalTime.", value instanceof LocalTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422000000);

        ObjectMapper mapper = newMapper();
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = mapper.readValue(
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,422]]", Temporal.class
                );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a LocalTime.", value instanceof LocalTime);
        assertEquals("The value is not correct.", time, value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper mapper = newMapper();
        mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = mapper.readValue(
                "[\"" + LocalTime.class.getName() + "\",\"" + time.toString() + "\"]", Temporal.class
                );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a LocalTime.", value instanceof LocalTime);
        assertEquals("The value is not correct.", time, value);
    }
}
