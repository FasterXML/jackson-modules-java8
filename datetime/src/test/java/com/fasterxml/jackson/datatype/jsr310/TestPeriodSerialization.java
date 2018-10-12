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
import static org.junit.Assert.assertTrue;

import java.time.Period;
import java.time.temporal.TemporalAmount;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.junit.Test;

public class TestPeriodSerialization
{
    private final ObjectMapper MAPPER = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

    @Test
    public void testSerialization01() throws Exception
    {
        Period period = Period.of(1, 6, 15);
        String value = MAPPER.writeValueAsString(period);
        assertEquals("The value is not correct.", '"' + period.toString() + '"', value);
    }

    @Test
    public void testSerialization02() throws Exception
    {
        Period period = Period.of(0, 0, 21);
        String value = MAPPER.writeValueAsString(period);
        assertEquals("The value is not correct.", '"' + period.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        Period period = Period.of(5, 1, 12);
        ObjectMapper mapper = MAPPER.rebuild()
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .build();
        String value = mapper.writeValueAsString(period);
        assertEquals("The value is not correct.",
                "[\"" + Period.class.getName() + "\",\"" + period.toString() + "\"]", value);
    }

    @Test
    public void testDeserialization01() throws Exception
    {
        Period period = Period.of(1, 6, 15);
        Period value = MAPPER.readValue('"' + period.toString() + '"', Period.class);
        assertEquals("The value is not correct.", period, value);
    }

    @Test
    public void testDeserialization02() throws Exception
    {
        Period period = Period.of(0, 0, 21);
        Period value = MAPPER.readValue('"' + period.toString() + '"', Period.class);
        assertEquals("The value is not correct.", period, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        Period period = Period.of(5, 1, 12);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .addMixIn(TemporalAmount.class, MockObjectConfiguration.class)
                .build();
        TemporalAmount value = mapper.readValue(
                "[\"" + Period.class.getName() + "\",\"" + period.toString() + "\"]", TemporalAmount.class
                );
        assertTrue("The value should be a Period.", value instanceof Period);
        assertEquals("The value is not correct.", period, value);
    }
}
