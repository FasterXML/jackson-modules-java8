package com.fasterxml.jackson.failing;

import java.util.Date;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.ModuleTestBase;

public class ContextualOptional17Test extends ModuleTestBase
{
    // [datatypes-java8#17]
    @JsonPropertyOrder({ "date1", "date2" })
    static class ContextualOptionals
    {
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
        public Optional<Date> date1;

        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM")
        public Optional<Date> date2;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testContextualOptionals() throws Exception
    {
        ContextualOptionals input = new ContextualOptionals();
        input.date1 = Optional.ofNullable(new Date(0L));
        input.date2 = Optional.ofNullable(new Date(0L));
        assertEquals(aposToQuotes("{'date1':'1970-01-01','date2':'1970-01'"),
                MAPPER.writeValueAsString(input));
    }        
}
