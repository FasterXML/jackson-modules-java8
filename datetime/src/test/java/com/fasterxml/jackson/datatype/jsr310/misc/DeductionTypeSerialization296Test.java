package com.fasterxml.jackson.datatype.jsr310.misc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

// for [modules-java8#296]: problem with `JsonTypeInfo.Id.DEDUCTION`
public class DeductionTypeSerialization296Test extends ModuleTestBase
{
    static class Wrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
        public Object value;

        public Wrapper(Object value) {
            this.value = value;
        }
    }

    private final ObjectMapper MAPPER = mapperBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    public void testLocalDate() throws Exception
    {
        LocalDate date = LocalDate.of(1986, Month.JANUARY, 17);
        Assert.assertEquals(a2q("{'value':'1986-01-17'}"),
                MAPPER.writeValueAsString(new Wrapper(date)));
    }

    @Test
    public void testLocalDateTime() throws Exception
    {
        LocalDateTime datetime = LocalDateTime.of(2013, Month.AUGUST, 21, 9, 22, 0, 57);
        Assert.assertEquals(a2q("{'value':'2013-08-21T09:22:00.000000057'}"),
                MAPPER.writeValueAsString(new Wrapper(datetime)));
    }

    @Test
    public void testLocalTime() throws Exception
    {
        LocalTime time = LocalTime.of(9, 22, 57);
        Assert.assertEquals(a2q("{'value':'09:22:57'}"),
                MAPPER.writeValueAsString(new Wrapper(time)));
    }
}
