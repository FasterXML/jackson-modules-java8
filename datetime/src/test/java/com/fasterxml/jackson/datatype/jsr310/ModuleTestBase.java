package com.fasterxml.jackson.datatype.jsr310;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ModuleTestBase
{
    // 14-Mar-2016, tatu: Serialization of trailing zeroes may change [datatype-jsr310#67]
    //   Note, tho, that "0.0" itself is special case; need to avoid scientific notation:
    final static String NO_NANOSECS_SER = "0.0";
    final static String NO_NANOSECS_SUFFIX = ".000000000";
    
    protected ObjectMapper newMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }
}
