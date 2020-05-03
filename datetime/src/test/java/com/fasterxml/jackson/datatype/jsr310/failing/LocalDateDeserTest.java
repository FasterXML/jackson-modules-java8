package com.fasterxml.jackson.datatype.jsr310.failing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;
import org.junit.Test;

import java.time.LocalDate;

public class LocalDateDeserTest extends ModuleTestBase {
    private final ObjectMapper MAPPER = newMapper();

    final static class StrictWrapper {
        @JsonFormat(pattern="yyyy-MM-dd",
                lenient = OptBoolean.FALSE)
        public LocalDate value;

        public StrictWrapper() { }
        public StrictWrapper(LocalDate v) { value = v; }
    }

    @Test(expected = InvalidFormatException.class)
    public void testStrictCustomFormat() throws Exception
    {
        /*StrictWrapper w =*/ MAPPER.readValue("{\"value\":\"2019-11-31\"}", StrictWrapper.class);
    }
}
