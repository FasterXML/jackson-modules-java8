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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.jupiter.api.Assertions.*;

public class TestLocalDateSerialization extends ModuleTestBase
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
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("[1986,1,17]", value);
    }

    @Test
    public void testSerializationAsTimestamp02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("[2013,8,21]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals('"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals('"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);

        this.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(date);

        assertNotNull(value);
        assertEquals("[\"" + LocalDate.class.getName() + "\",\"" + date.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        LocalDate value = this.mapper.readValue("[1986,1,17]", LocalDate.class);

        assertNotNull(value);
        assertEquals(date, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        LocalDate value = this.mapper.readValue("[2013,8,21]", LocalDate.class);

        assertNotNull(value);
        assertEquals(date, value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);

        LocalDate value = this.mapper.readValue('"' + date.toString() + '"', LocalDate.class);

        assertNotNull(value);
        assertEquals(date, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);

        LocalDate value = this.mapper.readValue('"' + date.toString() + '"', LocalDate.class);

        assertNotNull(value);
        assertEquals(date, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue(
                "[\"" + LocalDate.class.getName() + "\",\"" + date.toString() + "\"]", Temporal.class
                );

        assertNotNull(value);
        assertInstanceOf(LocalDate.class, value);
        assertEquals(date, value);
    }
}
