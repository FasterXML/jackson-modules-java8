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

import java.time.Year;
import java.time.temporal.Temporal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestYearSerialization extends ModuleTestBase
{
    private ObjectMapper mapper;

    @BeforeEach
    public void setUp()
    {
        this.mapper = newMapper();
    }

    @Test
    public void testSerialization01() throws Exception
    {
        Year year = Year.of(1986);

        String value = this.mapper.writeValueAsString(year);

        assertNotNull(value);
        assertEquals("1986", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        Year year = Year.of(2013);

        String value = this.mapper.writeValueAsString(year);

        assertNotNull(value);
        assertEquals("2013", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        Year year = Year.of(2005);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(year);

        assertNotNull(value);
        assertEquals("[\"" + Year.class.getName() + "\",2005]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        Year value = this.mapper.readValue("1986", Year.class);

        assertNotNull(value);
        assertEquals(Year.of(1986), value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        Year value = this.mapper.readValue("2013", Year.class);

        assertNotNull(value);
        assertEquals(Year.of(2013), value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue("[\"" + Year.class.getName() + "\",2005]", Temporal.class);

        assertNotNull(value);
        assertInstanceOf(Year.class, value, "The value should be a Year.");
        assertEquals(Year.of(2005), value);
    }
}
