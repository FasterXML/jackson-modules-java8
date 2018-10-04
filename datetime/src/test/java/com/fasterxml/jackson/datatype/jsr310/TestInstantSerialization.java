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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestInstantSerialization extends ModuleTestBase
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final String CUSTOM_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final ObjectMapper MAPPER = newMapper();

    static class Wrapper {
        @JsonFormat(
                /* 22-Jun-2015, tatu: I'll be damned if I understand why pattern does not
                 *    work here... but it doesn't. Someone with better date-fu has to come
                 *    and fix this; until then I will only verify that we can force textual
                 *    representation here
                 */
                //pattern="YYYY-mm-dd",
                shape=JsonFormat.Shape.STRING
        )
        public Instant value;

        public Wrapper() { }
        public Wrapper(Instant v) { value = v; }
    }

    static class WrapperWithCustomPattern {
        @JsonFormat(
                pattern = CUSTOM_PATTERN,
                shape=JsonFormat.Shape.STRING,
                timezone = "UTC"
        )
        public Instant valueInUTC;

        public WrapperWithCustomPattern() { }
        public WrapperWithCustomPattern(Instant v) {
            valueInUTC = v;
        }
    }

    @Test
    public void testSerializationAsTimestamp01Nanoseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);

        assertNotNull("The value should not be null.", value);
        assertEquals("The value is not correct.", NO_NANOSECS_SER, value);
    }

    @Test
    public void testSerializationAsTimestamp01Milliseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "0", value);
    }

    @Test
    public void testSerializationAsTimestamp02Nanoseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "123456789.183917322", value);
    }

    @Test
    public void testSerializationAsTimestamp02Milliseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "123456789183", value);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        Instant date = Instant.now();
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", DecimalUtils.toDecimal(date.getEpochSecond(), date.getNano()), value);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        Instant date = Instant.now();
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", Long.toString(date.toEpochMilli()), value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        Instant date = Instant.now();
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        ObjectMapper m = newMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
            .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, true);
        m.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = m.writeValueAsString(date);
        assertEquals("The value is not correct.", "[\"" + Instant.class.getName() + "\",123456789.183917322]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        ObjectMapper m = newMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        m.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = m.writeValueAsString(date);
        assertEquals("The value is not correct.", "[\"" + Instant.class.getName() + "\",123456789183]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        Instant date = Instant.now();
        ObjectMapper m = newMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        m.addMixIn(Temporal.class, MockObjectConfiguration.class);
        String value = m.writeValueAsString(date);
        assertEquals("The value is not correct.",
                "[\"" + Instant.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", value);
    }

    @Test
    public void testDeserializationAsFloat01() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        Instant value = MAPPER.readValue("0.000000000", Instant.class);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsFloat02() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        Instant value = MAPPER.readValue("123456789.183917322", Instant.class);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsFloat03() throws Exception
    {
        Instant date = Instant.now();
        Instant value = MAPPER.readValue(
                DecimalUtils.toDecimal(date.getEpochSecond(), date.getNano()), Instant.class
                );
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsInt01Nanoseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        Instant value = MAPPER.readerFor(Instant.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("0");
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsInt01Milliseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        Instant value = MAPPER.readerFor(Instant.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("0");
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsInt02Nanoseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 0);
        Instant value = MAPPER.readerFor(Instant.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("123456789");
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsInt02Milliseconds() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 422000000);
        Instant value = MAPPER.readerFor(Instant.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue("123456789422");
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsInt03Nanoseconds() throws Exception
    {
        Instant date = Instant.now();
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        Instant value = MAPPER.readerFor(Instant.class)
                .with(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(Long.toString(date.getEpochSecond()));
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsInt03Milliseconds() throws Exception
    {
        Instant date = Instant.now();
        date = date.minus(date.getNano(), ChronoUnit.NANOS);

        Instant value = MAPPER.readerFor(Instant.class)
                .without(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .readValue(Long.toString(date.toEpochMilli()));
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString01() throws Exception
    {
        Instant date = Instant.ofEpochSecond(0L);
        Instant value = MAPPER.readValue('"' + FORMATTER.format(date) + '"', Instant.class);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString02() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        Instant value = MAPPER.readValue('"' + FORMATTER.format(date) + '"', Instant.class);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationAsString03() throws Exception
    {
        Instant date = Instant.now();

        Instant value = MAPPER.readValue('"' + FORMATTER.format(date) + '"', Instant.class);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationWithTypeInfo01() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 183917322);
        ObjectMapper m = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + Instant.class.getName() + "\",123456789.183917322]", Temporal.class
                );
        assertTrue("The value should be an Instant.", value instanceof Instant);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationWithTypeInfo02() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 0);
        ObjectMapper m = newMapper()
                .enable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + Instant.class.getName() + "\",123456789]", Temporal.class
                );
        assertTrue("The value should be an Instant.", value instanceof Instant);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationWithTypeInfo03() throws Exception
    {
        Instant date = Instant.ofEpochSecond(123456789L, 422000000);
        ObjectMapper m = newMapper()
                .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + Instant.class.getName() + "\",123456789422]", Temporal.class
                );

        assertTrue("The value should be an Instant.", value instanceof Instant);
        assertEquals("The value is not correct.", date, value);
    }

    @Test
    public void testDeserializationWithTypeInfo04() throws Exception
    {
        Instant date = Instant.now();
        ObjectMapper m = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + Instant.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", Temporal.class
                );
        assertTrue("The value should be an Instant.", value instanceof Instant);
        assertEquals("The value is not correct.", date, value);
    }

    /**
     * This should be within the range of a max Instant and should pass
     * @throws Exception
     */
    @Test(timeout=3000)
    public void testDeserializationWithTypeInfo05() throws Exception
    {
        Instant date = Instant.MAX;
        String customInstant = date.getEpochSecond() +"."+ date.getNano();
        ObjectMapper m = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        Temporal value = m.readValue(
                "[\"" + Instant.class.getName() + "\","+customInstant+"]", Temporal.class
        );
        assertTrue("The value should be an Instant.", value instanceof Instant);
        assertEquals("The value is not correct.", date, value);
    }

    /**
     * This test can potentially hang the VM, so exit if it doesn't finish
     * within a few seconds.
     *
     * @throws Exception
     */
    @Test(timeout=3000, expected = JsonParseException.class)
    public void testDeserializationWithTypeInfoAndStringTooLarge01() throws Exception
    {
        String customInstant = "1000000000000e1000000000000";
        ObjectMapper m = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        m.readValue(
                "[\"" + Instant.class.getName() + "\","+customInstant+"]", Temporal.class
        );
    }

    /**
     * This test can potentially hang the VM, so exit if it doesn't finish
     * within a few seconds.
     *
     * @throws Exception
     */
    @Test(timeout=3000, expected = JsonParseException.class)
    public void testDeserializationWithTypeInfoAndStringTooLarge02() throws Exception
    {
        Instant date = Instant.MAX;
        // Add in an few extra zeros to be longer than what an epoch should be
        String customInstant = date.getEpochSecond() +"0000000000000000."+ date.getNano();
        ObjectMapper m = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        m.readValue(
                "[\"" + Instant.class.getName() + "\","+customInstant+"]", Temporal.class
        );
        System.out.println("test");
    }

    /**
     * This test can potentially hang the VM, so exit if it doesn't finish
     * within a few seconds.
     *
     * @throws Exception
     */
    @Test(timeout=13000, expected = JsonParseException.class)
    public void testDeserializationWithTypeInfoAndStringTooFractional01() throws Exception
    {
        String customInstant = "1e-100000000000";
        ObjectMapper m = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class);
        m.readValue(
                "[\"" + Instant.class.getName() + "\","+customInstant+"]", Temporal.class
        );
    }

    @Test
    public void testCustomPatternWithAnnotations01() throws Exception
    {
        final Wrapper input = new Wrapper(Instant.ofEpochMilli(0));
        String json = MAPPER.writeValueAsString(input);
        assertEquals(aposToQuotes("{'value':'1970-01-01T00:00:00Z'}"), json);

        Wrapper result = MAPPER.readValue(json, Wrapper.class);
        assertEquals(input.value, result.value);
    }

    // [datatype-jsr310#69]
    @Test
    public void testCustomPatternWithAnnotations02() throws Exception
    {
        //Test date is pushed one year after start of the epoch just to avoid possible issues with UTC-X TZs which could
        //push the instant before tha start of the epoch
        final Instant instant = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("UTC")).plusYears(1).toInstant();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CUSTOM_PATTERN);
        final String valueInUTC = formatter.withZone(ZoneId.of("UTC")).format(instant);

        final WrapperWithCustomPattern input = new WrapperWithCustomPattern(instant);
        String json = MAPPER.writeValueAsString(input);

        assertTrue("Instant in UTC timezone was not serialized as expected.",
                json.contains(aposToQuotes("'valueInUTC':'" + valueInUTC + "'")));

        WrapperWithCustomPattern result = MAPPER.readValue(json, WrapperWithCustomPattern.class);
        assertEquals("Instant in UTC timezone was not deserialized as expected.",
                input.valueInUTC, result.valueInUTC);
    }

    // [datatype-jsr310#16]
    @Test
    public void testDeserializationFromStringAsNumber() throws Exception
    {
        // First, baseline test with floating-point numbers
        Instant inst = Instant.now();
        String json = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(inst);
        Instant result = MAPPER.readValue(json, Instant.class);
        assertNotNull(result);
        assertEquals(result, inst);

        // but then quoted as JSON String
        result = MAPPER.readValue(String.format("\"%s\"", json),
                Instant.class);
        assertNotNull(result);
        assertEquals(result, inst);
    }

    // [datatype-jsr310#79]
    @Test
    public void testRoundTripOfInstantAndJavaUtilDate() throws Exception
    {
        ObjectMapper mapper = newMapper();
        mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

        Instant givenInstant = LocalDate.of(2016, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant();
        String json = mapper.writeValueAsString(java.util.Date.from(givenInstant));
        Instant actual = mapper.readValue(json, Instant.class);

        assertEquals(givenInstant, actual);
    }

    // [jackson-modules-java8#18]
    @Test
    public void testDeserializationFromStringWithZeroZoneOffset01() throws Exception {
        Instant date = Instant.now();
        String json = formatWithZeroZoneOffset(date, "+00:00");
        Instant result = MAPPER.readValue(json, Instant.class);
        assertEquals("The value is not correct.", date, result);
    }

    @Test
    public void testDeserializationFromStringWithZeroZoneOffset02() throws Exception {
        Instant date = Instant.now();
        String json = formatWithZeroZoneOffset(date, "+0000");
        Instant result = MAPPER.readValue(json, Instant.class);
        assertEquals("The value is not correct.", date, result);
    }

    @Test
    public void testDeserializationFromStringWithZeroZoneOffset03() throws Exception {
        Instant date = Instant.now();
        String json = formatWithZeroZoneOffset(date, "+00");
        Instant result = MAPPER.readValue(json, Instant.class);
        assertEquals("The value is not correct.", date, result);
    }

    private String formatWithZeroZoneOffset(Instant date, String offset){
        return '"' + FORMATTER.format(date).replaceFirst("Z$", offset) + '"';
    }
}
