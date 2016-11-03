package com.fasterxml.jackson.datatype.jsr310.old;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ModuleTestBase
{
    // 14-Mar-2016, tatu: Serialization of trailing zeroes may change [datatype-jsr310#67]
    //   Note, tho, that "0.0" itself is special case; need to avoid scientific notation:
    final static String NO_NANOSECS_SER = "0.0";
    final static String NO_NANOSECS_SUFFIX = ".000000000";
    
    @SuppressWarnings("deprecation")
    protected ObjectMapper newMapper() {
        return new ObjectMapper()
                .registerModule(new com.fasterxml.jackson.datatype.jsr310.JSR310Module());
    }
}
