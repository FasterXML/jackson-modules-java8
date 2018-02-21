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

import java.time.ZoneId;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestZoneIdSerialization
{
    private final ObjectMapper MAPPER = ObjectMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

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
        ObjectMapper mapper = ObjectMapper.builder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .addModule(new JavaTimeModule())
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
        ObjectMapper mapper = ObjectMapper.builder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .addModule(new JavaTimeModule())
                .build();
        ZoneId value = mapper.readValue("[\"" + ZoneId.class.getName() + "\",\"America/Denver\"]", ZoneId.class);
        assertEquals("The value is not correct.", ZoneId.of("America/Denver"), value);
    }
}
