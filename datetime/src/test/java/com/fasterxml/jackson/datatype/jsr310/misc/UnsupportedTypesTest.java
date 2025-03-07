package com.fasterxml.jackson.datatype.jsr310.misc;

import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class UnsupportedTypesTest extends ModuleTestBase
{
    // [modules-java8#207]
    static class TAWrapper {
        public TemporalAdjuster a;

        public TAWrapper(TemporalAdjuster a) {
            this.a = a;
        }
    }

    // [modules-java#207]: should not fail on `TemporalAdjuster`
    @Test
    public void testTemporalAdjusterSerialization() throws Exception
    {
        ObjectMapper mapper = newMapper();

        // Not 100% sure how this happens, actually; should fail on empty "POJO"?
        assertEquals(a2q("{'a':{}}"),
                mapper.writeValueAsString(new TAWrapper(TemporalAdjusters.firstDayOfMonth())));
    }
}
