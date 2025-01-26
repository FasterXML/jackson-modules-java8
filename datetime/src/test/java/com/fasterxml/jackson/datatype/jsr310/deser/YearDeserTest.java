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

import java.io.IOException;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class YearDeserTest extends ModuleTestBase
{
    private final TypeReference<Map<String, Year>> MAP_TYPE_REF = new TypeReference<Map<String, Year>>() { };

    static class FormattedYear {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "'Y'yyyy")
        public Year value;

        protected FormattedYear() {}
        public FormattedYear(Year year) {
            value = year;
        }
    }

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(Year.class);

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        assertEquals(Year.of(2000), READER.readValue(q("2000")),
                "The value is not correct.");
    }

    @Test
    public void testBadDeserializationAsString01() throws Throwable
    {
        expectFailure(q("notayear"));
    }

    @Test
    public void testDeserializationAsArrayDisabled() throws Throwable
    {
     try {
          read("['2000']");
         fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    }
    
    @Test
    public void testDeserializationAsEmptyArrayDisabled() throws Throwable
    {
     try {
          read("[]");
         fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
     try {
          String json="[]";
          newMapper()
                    .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                    .readerFor(Year.class).readValue(a2q(json));
         fail("expected JsonMappingException");
        } catch (JsonMappingException e) {
           // OK
        } catch (IOException e) {
            throw e;
        }
    }
    
    @Test
    public void testDeserializationAsArrayEnabled() throws Throwable
    {
        Year value= newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .readerFor(Year.class).readValue("[\"2000\"]");
        assertEquals(Year.of(2000), value, "The value is not correct.");
    }
    
    @Test
    public void testDeserializationAsEmptyArrayEnabled() throws Throwable
    {
        String json="[]";
        Year value= newMapper()
                .configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, true)
                .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
                .readerFor(Year.class).readValue(a2q(json));
        assertNull(value);
    }

    @Test
    public void testDefaultDeserialization() throws Exception
    {
        Year value = READER.readValue("1986");
        assertEquals(Year.of(1986), value, "The value is not correct.");
        value = READER.readValue("2013");
        assertEquals(Year.of(2013), value, "The value is not correct.");
    }

    @Test
    public void testDeserializationWithTypeInfo() throws Exception
    {
        ObjectMapper mapper = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = mapper.readValue("[\"" + Year.class.getName() + "\",2005]", Temporal.class);
        assertTrue(value instanceof Year, "The value should be a Year.");
        assertEquals(Year.of(2005), value, "The value is not correct.");
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

    /*
    /**********************************************************
    /* Tests for specific issues
    /**********************************************************
     */

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

    // [module-java8#78]
    @Test
    public void testWithCustomFormat78() throws Exception
    {
        ObjectTest input = new ObjectTest(Year.of(2018));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"value\":\"Y2018\"}", json);
        ObjectTest result = MAPPER.readValue(json, ObjectTest.class);
        assertEquals(input, result);
    }

    /*
    /**********************************************************
    /* Tests for empty string handling
    /**********************************************************
     */

    // minor changes in 2.12
    @Test
    public void testDeserializeFromEmptyString() throws Exception
    {
        final String key = "Year";
        final ObjectReader objectReader = MAPPER.readerFor(MAP_TYPE_REF);

        // First: by default, lenient, so empty String fine
        String doc = MAPPER.writeValueAsString(asMap(key, null));
        Map<String, Year> actualMapFromNullStr = objectReader.readValue(doc);
        assertNull(actualMapFromNullStr.get(key));

        doc = MAPPER.writeValueAsString(asMap("date", ""));
        Map<String, Year> actualMapFromEmptyStr = objectReader.readValue(doc);
        assertNotNull(actualMapFromEmptyStr);

        // But can make strict:
        final ObjectMapper strictMapper = mapperBuilder().build();
        strictMapper.configOverride(Year.class)
                .setFormat(JsonFormat.Value.forLeniency(false));
        doc = strictMapper.writeValueAsString(asMap("date", ""));
        try {
            actualMapFromEmptyStr = strictMapper.readerFor(MAP_TYPE_REF)
                    .readValue(doc);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "not allowed because 'strict' mode set for");
        }
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private Year read(final String json) throws IOException {
        return READER.readValue(a2q(json));
    }

    private void expectFailure(String json) throws Throwable {
        try {
            READER.readValue(json);
            fail("expected DateTimeParseException");
        } catch (JsonProcessingException e) {
            if (e.getCause() == null) {
                throw e;
            }
            if (!(e.getCause() instanceof DateTimeParseException)) {
                throw e.getCause();
            }
        }
    }
}
