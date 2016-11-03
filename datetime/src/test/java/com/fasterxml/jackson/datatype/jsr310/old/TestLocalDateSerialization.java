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

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.Before;
import org.junit.Test;

public class TestLocalDateSerialization extends ModuleTestBase
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
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[1986,1,17]", value);
    }

    @Test
    public void testSerializationAsTimestamp02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[2013,8,21]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "[\"" + LocalDate.class.getName() + "\",\"" + date.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        LocalDate value = this.mapper.readValue("[1986,1,17]", LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        LocalDate value = this.mapper.readValue("[2013,8,21]", LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        LocalDate value = this.mapper.readValue('"' + date.toString() + '"', LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        LocalDate value = this.mapper.readValue('"' + date.toString() + '"', LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + LocalDate.class.getName() + "\",\"" + date.toString() + "\"]", Temporal.class
                );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a LocalDate.", value instanceof LocalDate);
        assertEquals("The value is not correct.", date, value);
    }
}
