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

import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

public class ZoneOffsetDeserTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSimpleZoneOffsetDeser() throws Exception
    {
        assertEquals("The value is not correct.", ZoneOffset.of("Z"),
                MAPPER.readValue("\"Z\"", ZoneOffset.class));
        assertEquals("The value is not correct.", ZoneOffset.of("+0300"),
                MAPPER.readValue("\"+0300\"", ZoneOffset.class));
        assertEquals("The value is not correct.", ZoneOffset.of("-0630"),
                MAPPER.readValue("\"-06:30\"", ZoneOffset.class));
    }

    @Test
    public void testZoneOffsetDeserFromEmpty() throws Exception
    {
        // by default, should be fine
        assertNull(MAPPER.readValue(quote("  "), ZoneOffset.class));
        // but fail if coercion illegal
        try {
            MAPPER.readerFor(ZoneOffset.class)
                .without(DeserializationFeature.ALLOW_COERCION_OF_SCALARS)
                .readValue(quote(" "));
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce");
            verifyException(e, ZoneOffset.class.getName());
            verifyException(e, "enable `DeserializationFeature.ALLOW_COERCION_OF_SCALARS`");
        }
    }
    
    @Test
    public void testPolymorphicZoneOffsetDeser() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
            .addMixIn(ZoneId.class, MockObjectConfiguration.class)
            .build();
        ZoneId value = mapper.readValue("[\"" + ZoneOffset.class.getName() + "\",\"+0415\"]", ZoneId.class);
        assertTrue("The value should be a ZoneOffset.", value instanceof ZoneOffset);
        assertEquals("The value is not correct.", ZoneOffset.of("+0415"), value);
    }
}
