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

package tools.jackson.datatype.jsr310.ser;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.Temporal;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.datatype.jsr310.MockObjectConfiguration;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.*;

public class LocalDateTimeSerTest
    extends ModuleTestBase
{
    private final ObjectMapper mapper = newMapper();

    @Test
    public void testSerializationAsTimestamp01() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(1986, Month.JANUARY, 17, 15, 43);
        String value = mapper.writeValueAsString(time);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[1986,1,17,15,43]", value);
    }

    @Test
    public void testSerializationAsTimestamp02() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 57);
        String value = mapper.writeValueAsString(time);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", "[2013,8,21,9,22,57]", value);
    }

    @Test
    public void testSerializationAsTimestamp03Nanosecond() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57);

        ObjectMapper m = newMapperBuilder()
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.", "[2013,8,21,9,22,0,57]", value);
    }

    @Test
    public void testSerializationAsTimestamp03Millisecond() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57);
        ObjectMapper m = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.", "[2013,8,21,9,22,0,0]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Nanosecond() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);

        final ObjectMapper m = newMapperBuilder()
                .enable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.", "[2005,11,5,22,31,5,829837]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Millisecond() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 422829837);

        final ObjectMapper m = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.", "[2005,11,5,22,31,5,422]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(1986, Month.JANUARY, 17, 15, 43, 05);
        final ObjectMapper m = newMapperBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
        assertEquals("The value is not correct.", "\"1986-01-17T15:43:05\"",
                m.writeValueAsString(time));
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 57);

        final ObjectMapper m = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.", '"' + time.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        final ObjectMapper m = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.", '"' + time.toString() + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);

        final ObjectMapper m = newMapperBuilder()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.",
                "[\"" + LocalDateTime.class.getName() + "\",[2005,11,5,22,31,5,829837]]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        final ObjectMapper m = newMapperBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 422829837);
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.",
                "[\"" + LocalDateTime.class.getName() + "\",[2005,11,5,22,31,5,422]]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        final ObjectMapper m = newMapperBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        LocalDateTime time = LocalDateTime.of(2005, Month.NOVEMBER, 5, 22, 31, 5, 829837);
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.",
                "[\"" + LocalDateTime.class.getName() + "\",\"" + time.toString() + "\"]", value);
    }
}
