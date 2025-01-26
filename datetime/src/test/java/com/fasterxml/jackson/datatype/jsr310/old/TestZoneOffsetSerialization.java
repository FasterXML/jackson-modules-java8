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

import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class TestZoneOffsetSerialization extends ModuleTestBase
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
        ZoneOffset offset = ZoneOffset.of("Z");

        String value = this.mapper.writeValueAsString(offset);

        assertNotNull(value);
        assertEquals("\"Z\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("+0300");

        String value = this.mapper.writeValueAsString(offset);

        assertNotNull(value);
        assertEquals("\"+03:00\"", value);
    }

    @Test
    public void testSerialization03() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("-0630");

        String value = this.mapper.writeValueAsString(offset);

        assertNotNull(value);
        assertEquals("\"-06:30\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("+0415");

        this.mapper.addMixIn(ZoneId.class, MockObjectConfiguration.class);
        String value = this.mapper.writeValueAsString(offset);

        assertNotNull(value);
        assertEquals("[\"" + ZoneOffset.class.getName() + "\",\"+04:15\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        ZoneOffset value = this.mapper.readValue("\"Z\"", ZoneOffset.class);

        assertNotNull(value);
        assertEquals(ZoneOffset.of("Z"), value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        ZoneOffset value = this.mapper.readValue("\"+0300\"", ZoneOffset.class);

        assertNotNull(value);
        assertEquals(ZoneOffset.of("+0300"), value);
    }

    @Test
    public void testDeserialization03() throws Exception
    {
        ZoneOffset value = this.mapper.readValue("\"-06:30\"", ZoneOffset.class);

        assertNotNull(value);
        assertEquals(ZoneOffset.of("-0630"), value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        this.mapper.addMixIn(ZoneId.class, MockObjectConfiguration.class);
        ZoneId value = this.mapper.readValue("[\"" + ZoneOffset.class.getName() + "\",\"+0415\"]", ZoneId.class);

        assertNotNull(value);
        assertInstanceOf(ZoneOffset.class, value);
        assertEquals(ZoneOffset.of("+0415"), value);
    }
}
