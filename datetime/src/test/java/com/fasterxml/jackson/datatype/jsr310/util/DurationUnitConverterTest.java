package com.fasterxml.jackson.datatype.jsr310.util;

import static org.junit.assertNotNull;
import static org.junit.assertNull;

import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DurationUnitConverterTest
    extends ModuleTestBase
{
    @Test
    public void shouldMapToTemporalUnit() {
        for (ChronoUnit inputUnit : new ChronoUnit[] {
                ChronoUnit.NANOS,
                ChronoUnit.MICROS,
                ChronoUnit.MILLIS,
                ChronoUnit.SECONDS,
                ChronoUnit.MINUTES,
                ChronoUnit.HOURS,
                ChronoUnit.HALF_DAYS,
                ChronoUnit.DAYS,
        }) {
            DurationUnitConverter conv = DurationUnitConverter.from(inputUnit.name());
            assertNotNull(conv);
            // is case-sensitive:
            assertNull(DurationUnitConverter.from(inputUnit.name().toLowerCase()));
        }
    }

    @Test
    public void shouldNotMapToTemporalUnit() {
        for (String invalid : new String[] {
                // Inaccurate units not (yet?) supported
                "WEEKS",
                "MONTHS",
                "YEARS",
                "DECADES",
                "CENTURIES",
                "MILLENNIA",
                "ERAS",
                "FOREVER",

                // Not matching at all
                "DOESNOTMATCH", "", "   "
        }) {
            assertNull("Should not map pattern '"+invalid+"'",
                    DurationUnitConverter.from(invalid));
        }
    }
}
