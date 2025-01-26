package com.fasterxml.jackson.datatype.jsr310.ser;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.DecimalUtils;
import com.fasterxml.jackson.datatype.jsr310.MockObjectConfiguration;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OffsetDateTimeSerTest
    extends ModuleTestBase
{
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private static final ZoneId Z1 = ZoneId.of("America/Chicago");

    private static final ZoneId Z2 = ZoneId.of("America/Anchorage");

    private static final ZoneId Z3 = ZoneId.of("America/Los_Angeles");

    static class Wrapper {
        @JsonFormat(
                pattern="yyyy_MM_dd'T'HH:mm:ssZ",
                shape=JsonFormat.Shape.STRING)
        public OffsetDateTime value;

        public Wrapper() { }
        public Wrapper(OffsetDateTime v) { value = v; }
    }

    private ObjectMapper MAPPER = newMapper();

    @Test
    public void testSerializationAsTimestamp01Nanoseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "0.0", value);
    }

    @Test
    public void testSerializationAsTimestamp01Milliseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "0", value);
    }

    @Test
    public void testSerializationAsTimestamp02Nanoseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "123456789.183917322", value);
    }

    @Test
    public void testSerializationAsTimestamp02Milliseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", "123456789183", value);
    }

    @Test
    public void testSerializationAsTimestamp03Nanoseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", DecimalUtils.toDecimal(date.toEpochSecond(), date.getNano()), value);
    }

    @Test
    public void testSerializationAsTimestamp03Milliseconds() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", Long.toString(date.toInstant().toEpochMilli()), value);
    }

    @Test
    public void testSerializationAsString01() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"'
                + FORMATTER.withZone(Z1).format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString02() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"'
                + FORMATTER.withZone(Z2).format(date) + '"', value);
    }

    @Test
    public void testSerializationAsString03() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"'
                + FORMATTER.withZone(Z3).format(date) + '"', value);
    }

    // [modules-java#254]
    @Test
    public void testSerializationWithJsonFormat() throws Exception
    {
        OffsetDateTime t1 = OffsetDateTime.parse("2022-04-27T12:00:00+02:00");
        Wrapper input = new Wrapper(t1);

        // pattern="yyyy_MM_dd'T'HH:mm:ssZ"
        assertEquals(a2q("{'value':'2022_04_27T12:00:00+0200'}"),
                MAPPER.writeValueAsString(input));
 
        ObjectMapper m = mapperBuilder().withConfigOverride(OffsetDateTime.class,
                cfg -> cfg.setFormat(JsonFormat.Value.forPattern("yyyy.MM.dd'x'HH:mm:ss")))
            .build();
        assertEquals(a2q("'2022.04.27x12:00:00'"), m.writeValueAsString(t1));
    }
    
    @Test
    public void testSerializationAsStringWithMapperTimeZone01() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0L), Z1);
        String value = newMapper()
                .setTimeZone(TimeZone.getTimeZone(Z1))
                .writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsStringWithMapperTimeZone02() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = newMapper()
                .setTimeZone(TimeZone.getTimeZone(Z2))
                .writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationAsStringWithMapperTimeZone03() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = newMapper()
                .setTimeZone(TimeZone.getTimeZone(Z3))
                .writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.", '"' + FORMATTER.format(date) + '"', value);
    }

    @Test
    public void testSerializationWithTypeInfo01() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.",
                "[\"" + OffsetDateTime.class.getName() + "\",123456789.183917322]", value);
    }

    @Test
    public void testSerializationWithTypeInfo02() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.ofInstant(Instant.ofEpochSecond(123456789L, 183917322), Z2);
        String value = newMapper()
                .addMixIn(Temporal.class, MockObjectConfiguration.class)
                .writer()
                .with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .writeValueAsString(date);
        assertEquals("The value is not correct.",
                "[\"" + OffsetDateTime.class.getName() + "\",123456789183]", value);
    }

    @Test
    public void testSerializationWithTypeInfo03() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = newMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .writeValueAsString(date);
        assertEquals("The value is not correct.",
            "[\"" + OffsetDateTime.class.getName() + "\",\""
                    + FORMATTER.withZone(Z3).format(date) + "\"]", value);
    }

    @Test
    public void testSerializationWithTypeInfoAndMapperTimeZone() throws Exception
    {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = newMapper()
            .setTimeZone(TimeZone.getTimeZone(Z3))
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .addMixIn(Temporal.class, MockObjectConfiguration.class)
            .writeValueAsString(date);

        assertEquals("The value is not correct.",
                "[\"" + OffsetDateTime.class.getName() + "\",\"" + FORMATTER.format(date) + "\"]", value);
    }

    @Test
    public void testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOn() throws Exception {
        OffsetDateTime date = OffsetDateTime.now(Z3);
        String value = MAPPER.writer()
                .with(TimeZone.getTimeZone(Z2))
                .without(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                .writeValueAsString(date);

        // We expect to have the date written with the ZoneId Z2
        assertEquals("The value is incorrect", "\"" + FORMATTER.format(date.atZoneSameInstant(Z2)) + "\"", value);
    }

    @Test
    public void testSerializationAsStringWithDefaultTimeZoneAndContextTimeZoneOff() throws Exception {
        ZonedDateTime date = ZonedDateTime.now(Z3);
        String value = MAPPER.writer()
                .with(TimeZone.getTimeZone(Z2))
                .without(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .without(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                .writeValueAsString(date);

        // We expect to have the date written with the ZoneId Z3
        assertEquals("The value is incorrect", "\"" + FORMATTER.format(date) + "\"", value);
    }

    static class Pojo1 {
        @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
        public OffsetDateTime t1 = OffsetDateTime.parse("2022-04-27T12:00:00+02:00");
        public OffsetDateTime t2 = t1;
    }

    @Test
    public void testShapeInt() throws Exception {
        String json1 = newMapper().writeValueAsString(new Pojo1());
        assertEquals("{\"t1\":1651053600000,\"t2\":1651053600.000000000}", json1);
    }
}
