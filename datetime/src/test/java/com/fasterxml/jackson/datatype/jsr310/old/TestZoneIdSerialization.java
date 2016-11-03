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

import java.time.ZoneId;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;

public class TestZoneIdSerialization extends ModuleTestBase
{
    private ObjectMapper mapper;

    @Before
    public void setUp()
    {
        this.mapper = newMapper();
    }

    @Test
    public void testSerialization01() throws Exception
    {
        ZoneId id = ZoneId.of("America/Chicago");

        String value = this.mapper.writeValueAsString(id);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"America/Chicago\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        ZoneId id = ZoneId.of("America/Anchorage");

        String value = this.mapper.writeValueAsString(id);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"America/Anchorage\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ZoneId id = ZoneId.of("America/Denver");

        this.mapper.addMixIn(ZoneId.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(id);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[\"java.time.ZoneRegion\",\"America/Denver\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        ZoneId value = this.mapper.readValue("\"America/Chicago\"", ZoneId.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", ZoneId.of("America/Chicago"), value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        ZoneId value = this.mapper.readValue("\"America/Anchorage\"", ZoneId.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", ZoneId.of("America/Anchorage"), value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        this.mapper.addMixIn(ZoneId.class, MockObjectConfiguration.class);
        ZoneId value = this.mapper.readValue("[\"" + ZoneId.class.getName() + "\",\"America/Denver\"]", ZoneId.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", ZoneId.of("America/Denver"), value);
    }
}
