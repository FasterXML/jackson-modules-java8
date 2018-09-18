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

package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.Year;
import java.time.temporal.Temporal;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class YearDeserTest extends ModuleTestBase
{
    static class FormattedYear {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'Y'yyyy")
        public Year value;

        protected FormattedYear() {}
        public FormattedYear(Year year) {
            value = year;
        }
    }

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testDefaultDeserialization() throws Exception
    {
        Year value = MAPPER.readValue("1986", Year.class);
        assertEquals("The value is not correct.", Year.of(1986), value);
        value = MAPPER.readValue("2013", Year.class);
        assertEquals("The value is not correct.", Year.of(2013), value);
    }

    @Test
    public void testDeserializationWithTypeInfo() throws Exception
    {
        ObjectMapper mapper = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = mapper.readValue("[\"" + Year.class.getName() + "\",2005]", Temporal.class);
        assertTrue("The value should be a Year.", value instanceof Year);
        assertEquals("The value is not correct.", Year.of(2005), value);
    }

    @Test
    public void testWithCustomFormat() throws Exception
    {
        FormattedYear input = new FormattedYear(Year.of(2018));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":\"Y2018\"}", json);
        FormattedYear result = MAPPER.readValue(json, FormattedYear.class);
        assertEquals(input.value, result.value);
    }

    @Test
    public void testWithFormatViaConfigOverride() throws Exception
    {
        ObjectMapper mapper = newMapper();
        mapper.configOverride(Year.class)
                .setFormat(JsonFormat.Value.forPattern("'X'yyyy"));
        Year input = Year.of(2018);
        String json = mapper.writeValueAsString(input);
        assertEquals("\"X2018\"", json);
        Year result = mapper.readValue(json, Year.class);
        assertEquals(input, result);
    }
}
