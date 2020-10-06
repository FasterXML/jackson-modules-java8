package com.fasterxml.jackson.datatype.jsr310.deser;

import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer.DurationUnitParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Collection;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DurationUnitParserEmptyTest {

    private final String stringPattern;

    public DurationUnitParserEmptyTest(String stringPattern) {
        this.stringPattern = stringPattern;
    }

    @Test
    public void shouldNotMapToTemporalUnit() {
        Optional<DurationUnitParser> durationPattern = DurationUnitParser.from(stringPattern);

        assertEquals(empty(), durationPattern);
    }

    @Parameters
    public static Collection<Object[]> testCases() {
        return asList(
                // Estimated units
                asArray("WEEKS"),
                asArray("MONTHS"),
                asArray("YEARS"),
                asArray("DECADES"),
                asArray("CENTURIES"),
                asArray("MILLENNIA"),
                asArray("ERAS"),
                asArray("FOREVER"),
                // Is case sensitive
                asArray("Nanos"),
                asArray("nanos"),
                // Not matching at all
                asArray("DOESNOTMATCH"),
                // Nilables
                asArray(null),
                asArray(""),
                asArray("   ")
        );
    }

    private static Object[] asArray(Object... values) {
        return values;
    }
}