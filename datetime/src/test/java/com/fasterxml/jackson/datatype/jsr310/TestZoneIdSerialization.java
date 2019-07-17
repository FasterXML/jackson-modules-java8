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

import java.time.ZoneId;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class TestZoneIdSerialization extends ModuleTestBase
{
    private ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerialization01() throws Exception
    {
        ZoneId id = ZoneId.of("America/Chicago");
        String value = MAPPER.writeValueAsString(id);
        assertEquals("The value is not correct.", "\"America/Chicago\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        ZoneId id = ZoneId.of("America/Anchorage");
        String value = MAPPER.writeValueAsString(id);
        assertEquals("The value is not correct.", "\"America/Anchorage\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ZoneId id = ZoneId.of("America/Denver");
        final ObjectMapper mapper = mapperBuilder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .build();
        String value = mapper.writeValueAsString(id);
        assertEquals("The value is not correct.", "[\"java.time.ZoneRegion\",\"America/Denver\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        ZoneId value = MAPPER.readValue("\"America/Chicago\"", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Chicago"), value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        ZoneId value = MAPPER.readValue("\"America/Anchorage\"", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Anchorage"), value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        final ObjectMapper mapper = mapperBuilder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .build();
        ZoneId value = mapper.readValue("[\"" + ZoneId.class.getName() + "\",\"America/Denver\"]", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Denver"), value);
    }
}
