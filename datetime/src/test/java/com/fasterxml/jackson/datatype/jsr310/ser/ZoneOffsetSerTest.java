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

package com.fasterxml.jackson.datatype.jsr310.ser;

import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

public class ZoneOffsetSerTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerialization01() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("Z");
        String value = MAPPER.writeValueAsString(offset);
        assertEquals("The value is not correct.", "\"Z\"", value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("+0300");
        String value = MAPPER.writeValueAsString(offset);
        assertEquals("The value is not correct.", "\"+03:00\"", value);
    }

    @Test
    public void testSerialization03() throws Exception
    {
        ZoneOffset offset = ZoneOffset.of("-0630");
        String value = MAPPER.writeValueAsString(offset);
        assertEquals("The value is not correct.", "\"-06:30\"", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .addMixIn(ZoneId.class, MockObjectConfiguration.class)
                .build();
        ZoneOffset offset = ZoneOffset.of("+0415");
        String value = mapper.writeValueAsString(offset);
        assertEquals("The value is not correct.", "[\"" + ZoneOffset.class.getName() + "\",\"+04:15\"]", value);
    }
}
