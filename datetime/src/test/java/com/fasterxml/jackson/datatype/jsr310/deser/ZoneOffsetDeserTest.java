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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

public class ZoneOffsetDeserTest extends ModuleTestBase
{
    private final static ObjectMapper MAPPER = newMapper();
    private final static ObjectReader READER = MAPPER.readerFor(ZoneOffset.class);

    @Test
    public void testDeserializationFromString() throws Exception
    {
        assertEquals("The value is not correct.", ZoneOffset.of("Z"),
                READER.readValue("\"Z\""));
        assertEquals("The value is not correct.", ZoneOffset.of("+0300"),
                READER.readValue(quote("+0300")));
        assertEquals("The value is not correct.", ZoneOffset.of("-0630"),
                READER.readValue("\"-06:30\""));
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        ObjectMapper mapper = newMapper()
            .addMixIn(ZoneId.class, MockObjectConfiguration.class);
        ZoneId value = mapper.readValue("[\"" + ZoneOffset.class.getName() + "\",\"+0415\"]", ZoneId.class);
        assertTrue("The value should be a ZoneOffset.", value instanceof ZoneOffset);
        assertEquals("The value is not correct.", ZoneOffset.of("+0415"), value);
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        try {
            READER.readValue("\"notazonedoffset\"");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Invalid ID for ZoneOffset");
        }
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[\"+0300\"]");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "out of START_ARRAY");
        }
    }

    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
        try {
            READER.readValue("[]");
            fail("expected MismatchedInputException");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "out of START_ARRAY");
        }
        try {
            READER
                .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .readValue("[]");
            fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
            verifyException(e, "Unexpected token (END_ARRAY)");
        }
    }

    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        ZoneOffset value = READER
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
               .readValue("[\"+0300\"]");
        assertEquals("The value is not correct.", ZoneOffset.of("+0300"), value);
    }

    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        ZoneOffset value = READER
               .with(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
               .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
               .readValue("[]");
        assertNull(value);
    }
}
