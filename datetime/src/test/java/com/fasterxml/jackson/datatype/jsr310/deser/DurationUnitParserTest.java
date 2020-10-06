package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer.DurationUnitParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DurationUnitParserTest {

    private final String stringPattern;
    private final TemporalUnit temporalUnit;

    public DurationUnitParserTest(String stringPattern, TemporalUnit temporalUnit) {
        this.stringPattern = stringPattern;
        this.temporalUnit = temporalUnit;
    }

    @Test
    public void shouldMapToTemporalUnit() {
        Optional<DurationUnitParser> durationPattern = DurationUnitParser.from(stringPattern);

        assertEquals(of(temporalUnit), durationPattern.map(dp -> dp.unit));
    }

    @Parameters
    public static Collection<Object[]> testCases() {
        return asList(
                asArray("NANOS", ChronoUnit.NANOS),
                asArray("MICROS", ChronoUnit.MICROS),
                asArray("MILLIS", ChronoUnit.MILLIS),
                asArray("SECONDS", ChronoUnit.SECONDS),
                asArray("MINUTES", ChronoUnit.MINUTES),
                asArray("HOURS", ChronoUnit.HOURS),
                asArray("HALF_DAYS", ChronoUnit.HALF_DAYS),
                asArray("DAYS", ChronoUnit.DAYS)
        );
    }

    private static Object[] asArray(Object... values) {
        return values;
    }
}
