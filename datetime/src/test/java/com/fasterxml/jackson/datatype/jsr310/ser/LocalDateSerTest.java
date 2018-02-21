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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.temporal.Temporal;
import org.junit.Test;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LocalDateSerTest
	extends ModuleTestBase
{
    final static class Wrapper {
        @JsonFormat(pattern="yyyy_MM_dd'T'HH:mmZ",
                shape=JsonFormat.Shape.STRING)
        public LocalDate value;

        public Wrapper() { }
        public Wrapper(LocalDate v) { value = v; }
    }

    final static class EpochDayWrapper {
        @JsonFormat(shape=JsonFormat.Shape.NUMBER_INT)
        public LocalDate value;

        public EpochDayWrapper() { }
        public EpochDayWrapper(LocalDate v) { value = v; }
    }

    static class VanillaWrapper {
        public LocalDate value;

        public VanillaWrapper() { }
        public VanillaWrapper(LocalDate v) { value = v; }
    }

    // [modules-java8#46]
    static class Holder46 {
        public LocalDate localDate;

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
        public Object object;

        public Holder46(LocalDate localDate, Object object) {
            this.localDate = localDate;
            this.object = object;
        }
    }    
    
    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerializationAsTimestamp01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);
        String value = MAPPER.writer()
        		.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[1986,1,17]", value);
    }

    @Test
    public void testSerializationAsTimestamp02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);
        String value = MAPPER.writer()
        		.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[2013,8,21]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);
        String value = MAPPER.writer()
        		.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);
        String value = MAPPER.writer()
        		.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        		.writeValueAsString(date);
        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", '"' + date.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);
        String value = mapper.writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.",
                "[\"" + LocalDate.class.getName() + "\",\"" + date.toString() + "\"]", value);
    }

    @Test
    public void testDeserializationAsTimestamp01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);
        LocalDate value = MAPPER.readValue("[1986,1,17]", LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsTimestamp02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);
        LocalDate value = MAPPER.readValue("[2013,8,21]", LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);
        LocalDate value = MAPPER.readValue('"' + date.toString() + '"', LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        LocalDate date = LocalDate.of(2013, Month.AUGUST, 21);
        LocalDate value = MAPPER.readValue('"' + date.toString() + '"', LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        LocalDateTime date = LocalDateTime.now();
        LocalDate value = MAPPER.readValue('"' + date.toString() + '"', LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", date.toLocalDate(), value);
    }

    @Test(expected = JsonMappingException.class)
    public void testDeserializationAsString04() throws Exception
    {
        this.MAPPER.readValue("\"2015-06-19TShouldNotParse\"", LocalDate.class);
    }

    @Test
    public void testDeserializationAsString05() throws Exception
    {
        Instant instant = Instant.now();
        LocalDate value = MAPPER.readValue('"' + instant.toString() + '"', LocalDate.class);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate(), value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
    			.addMixIn(Temporal.class, MockObjectConfiguration.class)
    			.build();
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);
        Temporal value = mapper.readValue(
                "[\"" + LocalDate.class.getName() + "\",\"" + date.toString() + "\"]", Temporal.class
                );

        assertNotNull("The value should not be null.", value);
        assertTrue("The value should be a LocalDate.", value instanceof LocalDate);
        assertEquals("The value is not correct.", date, value);
    }

    // for [datatype-jsr310#37]
    @Test
    public void testCustomFormat() throws Exception
    {
        Wrapper w = MAPPER.readValue("{\"value\":\"2015_07_28T13:53+0300\"}", Wrapper.class);
        LocalDate date = w.value; 
        assertNotNull(date);
        assertEquals(28, date.getDayOfMonth());
    }


    @Test
    public void testConfigOverrides() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .withConfigOverride(LocalDate.class,
                        o -> o.setFormat(JsonFormat.Value.forPattern("yyyy_MM_dd")))
                .build();
        LocalDate date = LocalDate.of(2005, Month.NOVEMBER, 5);
        VanillaWrapper input = new VanillaWrapper(date);
        final String EXP_DATE = "\"2005_11_05\"";
        String json = mapper.writeValueAsString(input);
        assertEquals("{\"value\":"+EXP_DATE+"}", json);
        assertEquals(EXP_DATE, mapper.writeValueAsString(date));

        // and read back, too
        VanillaWrapper output = mapper.readValue(json, VanillaWrapper.class);
        assertEquals(input.value, output.value);
        LocalDate date2 = mapper.readValue(EXP_DATE, LocalDate.class);
        assertEquals(date, date2);
    }

    @Test
    public void testConfigOverridesToEpochDay() throws Exception
    {
        ObjectMapper mapper = newMapperBuilder()
                .withConfigOverride(LocalDate.class,
                        o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER_INT)))
                .build();
        LocalDate date = LocalDate.ofEpochDay(1000);
        VanillaWrapper input = new VanillaWrapper(date);
        final String EXP_DATE = "1000";
        String json = mapper.writeValueAsString(input);
        assertEquals("{\"value\":"+EXP_DATE+"}", json);
        assertEquals(EXP_DATE, mapper.writeValueAsString(date));

        // and read back, too
        VanillaWrapper output = mapper.readValue(json, VanillaWrapper.class);
        assertEquals(input.value, output.value);
        LocalDate date2 = mapper.readValue(EXP_DATE, LocalDate.class);
        assertEquals(date, date2);
    }

    @Test
    public void testCustomFormatToEpochDay() throws Exception
    {
        EpochDayWrapper w = MAPPER.readValue("{\"value\": 1000}", EpochDayWrapper.class);
        LocalDate date = w.value;
        assertNotNull(date);
        assertEquals(LocalDate.ofEpochDay(1000), date);
    }

    // [modules-java8#46]
    @Test
    public void testPolymorphicSerialization() throws Exception
    {
        ObjectMapper mapper = newMapper();
        final LocalDate localDate = LocalDate.of(2017, 12, 5);
        String json = mapper.writeValueAsString(new Holder46(localDate, localDate));
        assertEquals(aposToQuotes("{\"localDate\":[2017,12,5],\"object\":{\"java.time.LocalDate\":[2017,12,5]}}"),
                json);
    }
}
