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

import java.time.ZoneId;
import java.time.ZoneOffset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestZoneOffsetSerialization
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
        ZoneOffset offset = ZoneOffset.of("Z");

        String value = this.mapper.writeValueAsString(offset);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"Z\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("+0300");

        String value = this.mapper.writeValueAsString(offset);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"+03:00\"", value);
    }

    @Test
    public void testSerialization03() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("-0630");

        String value = this.mapper.writeValueAsString(offset);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "\"-06:30\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("+0415");

        this.mapper.addMixIn(ZoneId.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(offset);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[\"" + ZoneOffset.class.getName() + "\",\"+04:15\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        ZoneOffset value = this.mapper.readValue("\"Z\"", ZoneOffset.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", ZoneOffset.of("Z"), value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        ZoneOffset value = this.mapper.readValue("\"+0300\"", ZoneOffset.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", ZoneOffset.of("+0300"), value);
    }

    @Test
    public void testDeserialization03() throws Exception
    {
        ZoneOffset value = this.mapper.readValue("\"-06:30\"", ZoneOffset.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", ZoneOffset.of("-0630"), value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        this.mapper.addMixIn(ZoneId.class, MockObjectConfiguration.class);
        ZoneId value = this.mapper.readValue("[\"" + ZoneOffset.class.getName() + "\",\"+0415\"]", ZoneId.class);

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a ZoneOffset.", value instanceof ZoneOffset);
        assertEquals("The value is not correct.", ZoneOffset.of("+0415"), value);
    }
}
