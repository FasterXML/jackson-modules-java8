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

import java.time.Year;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestYearSerialization
{
    private ObjectMapper mapper;

    @Before
    public void setUp()
    {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    @After
    public void tearDown()
    {

    }

    @Test
    public void testSerialization01() throws Exception
    {
        Year year = Year.of(1986);

        String value = this.mapper.writeValueAsString(year);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "1986", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        Year year = Year.of(2013);

        String value = this.mapper.writeValueAsString(year);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "2013", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        Year year = Year.of(2005);

        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(year);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[\"" + Year.class.getName() + "\",2005]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        Year value = this.mapper.readValue("1986", Year.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Year.of(1986), value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        Year value = this.mapper.readValue("2013", Year.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", Year.of(2013), value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        this.mapper.addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = this.mapper.readValue("[\"" + Year.class.getName() + "\",2005]", Temporal.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a Year.", value instanceof Year);
        assertEquals("The value is not correct.", Year.of(2005), value);
    }
}
