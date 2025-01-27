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

import java.time.Month;
import java.time.MonthDay;
import java.time.temporal.TemporalAccessor;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class MonthDaySerTest
    extends ModuleTestBase
{
    private ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerialization01() throws Exception
    {
        assertEquals("\"--01-17\"",
                MAPPER.writeValueAsString(MonthDay.of(Month.JANUARY, 17)));
    }

    @Test
    public void testSerialization02() throws Exception
    {
        assertEquals("\"--08-21\"",
                MAPPER.writeValueAsString(MonthDay.of(Month.AUGUST, 21)));
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
        mapper.addMixIn(TemporalAccessor.class, MockObjectConfiguration.class);
        MonthDay monthDay = MonthDay.of(Month.NOVEMBER, 5);
        String value = mapper.writeValueAsString(monthDay);
        assertEquals("[\"" + MonthDay.class.getName() + "\",\"--11-05\"]", value);
    }
}
