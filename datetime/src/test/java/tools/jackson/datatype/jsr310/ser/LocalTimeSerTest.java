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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.datatype.jsr310.MockObjectConfiguration;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocalTimeSerTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectWriter writer = MAPPER.writer();

    // [modules-java8#115]
    static class CustomLocalTimeSerializer extends LocalTimeSerializer {
        public CustomLocalTimeSerializer() {
             // Default doesn't cut it for us.
             super(DateTimeFormatter.ofPattern("HH/mm"));
        }
    }

    static class CustomWrapper {
        @JsonSerialize(using = CustomLocalTimeSerializer.class)
        public LocalTime value;

        public CustomWrapper(LocalTime v) { value = v; }
    }

    @Test
    public void testSerializationAsTimestamp01() throws Exception
    {
        String json = writer.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(LocalTime.of(15, 43));
        assertEquals("The value is not correct.", "[15,43]", json);
    }

    @Test
    public void testSerializationAsTimestamp02() throws Exception
    {
        String json = writer.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString( LocalTime.of(9, 22, 57));
        assertEquals("The value is not correct.", "[9,22,57]", json);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        String json = writer.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .writeValueAsString(LocalTime.of(9, 22, 0, 57));
        assertEquals("The value is not correct.", "[9,22,0,57]", json);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 0, 57);
        ObjectMapper mapper = newMapperBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .build();
        String value = mapper.writeValueAsString(time);

        assertEquals("The value is not correct.", "[9,22,0,0]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Nanoseconds() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper mapper = newMapperBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true)
                .build();
        String value = mapper.writeValueAsString(time);
        assertEquals("The value is not correct.", "[22,31,5,829837]", value);
    }

    @Test
    public void testSerializationAsTimestamp04Milliseconds() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422829837);
        ObjectMapper mapper = newMapperBuilder()
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .build();
        String value = mapper.writeValueAsString(time);
        assertEquals("The value is not correct.", "[22,31,5,422]", value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        LocalTime time = LocalTime.of(15, 43, 20);
        ObjectMapper mapper = newMapperBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
        assertEquals("The value is not correct.", "\"15:43:20\"",
                mapper.writeValueAsString(time));
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);
        ObjectMapper mapper = newMapperBuilder()
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                    .build();
        String value = mapper.writeValueAsString(time);
        assertEquals("The value is not correct.", '"' + time.toString() + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper m = newMapperBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .build();
        String value = m.writeValueAsString(time);
        assertEquals("The value is not correct.", '"' + time.toString() + '"', value);
    }

    // [modules-java8#115]
    @Test
    public void testWithCustomSerializer() throws Exception
    {
        String json = MAPPER.writeValueAsString(new CustomWrapper(LocalTime.of(15, 43)));
        assertEquals("The value is not correct.", "{\"value\":\"15/43\"}", json);
    }
    
    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper m = newMapperBuilder()
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        String json = m.writeValueAsString(time);

        assertEquals("The value is not correct.",
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,829837]]",
                json);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 422829837);

        ObjectMapper m = newMapperBuilder()
                .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        String json = m.writeValueAsString(time);
        assertEquals("The value is not correct.",
                "[\"" + LocalTime.class.getName() + "\",[22,31,5,422]]",
                json);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        LocalTime time = LocalTime.of(22, 31, 5, 829837);
        ObjectMapper m = newMapperBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .build();
        String value = m.writeValueAsString(time);

        assertEquals("The value is not correct.",
                "[\"" + LocalTime.class.getName() + "\",\"" + time.toString() + "\"]", value);
    }
}
