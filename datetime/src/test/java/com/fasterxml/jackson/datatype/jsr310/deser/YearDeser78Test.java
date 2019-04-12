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
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class YearDeser78Test extends ModuleTestBase
{
    // [module-java8#78]
    final static class ObjectTest {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'Y'yyyy")
        public Year value;

        protected ObjectTest() { }
        public ObjectTest(Year y) {
            value = y;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) return true;
            ObjectTest other = (ObjectTest) o;
            return Objects.equals(this.value, other.value);
        }

        // stupid Javac 8 barfs on override missing?!
        @Override
        public int hashCode() { return 42; }
    }

    private final ObjectMapper MAPPER = newMapper();

    // [module-java8#78]
    @Test
    public void testWithCustomFormat() throws Exception
    {
        ObjectTest input = new ObjectTest(Year.of(2018));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":\"Y2018\"}", json);
        ObjectTest result = MAPPER.readValue(json, ObjectTest.class);
        assertEquals(input, result);
    }
}
