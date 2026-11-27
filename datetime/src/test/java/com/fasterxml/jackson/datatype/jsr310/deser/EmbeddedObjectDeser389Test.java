package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for [modules-java8#389]: "embedded object" values (as exposed by binary
 * formats like CBOR/Smile, and by token buffering) must be verified to be
 * compatible with the requested type; otherwise generic containers (like
 * {@code Map<String,ZoneId>}) would silently end up with wrong-typed values,
 * due to type erasure.
 */
public class EmbeddedObjectDeser389Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    static Stream<Arguments> supportedTypes()
    {
        return Stream.of(
                // Types handled by `JSR310StringParsableDeserializer`
                Arguments.of(Period.class, Period.ofDays(1), Duration.ofSeconds(1)),
                Arguments.of(ZoneId.class, ZoneId.of("Europe/Helsinki"), Period.ofDays(1)),
                // NOTE: `ZoneOffset` IS-A `ZoneId`, but not vice versa
                Arguments.of(ZoneOffset.class, ZoneOffset.ofHours(2), ZoneId.of("Europe/Helsinki")),

                // Types handled by `InstantDeserializer`: note that all are `Temporal`s
                // so an unchecked cast would NOT catch these
                Arguments.of(Instant.class, Instant.ofEpochSecond(1), LocalDate.of(2020, 1, 1)),
                Arguments.of(OffsetDateTime.class,
                        OffsetDateTime.parse("2020-01-01T00:00:00+02:00"), Instant.ofEpochSecond(1)),
                Arguments.of(ZonedDateTime.class,
                        ZonedDateTime.parse("2020-01-01T00:00:00+02:00"), Instant.ofEpochSecond(1)),

                // ... and the rest of `java.time` types with embedded-object support
                Arguments.of(Duration.class, Duration.ofSeconds(1), Period.ofDays(1)),
                Arguments.of(LocalDate.class, LocalDate.of(2020, 1, 1), Instant.ofEpochSecond(1)),
                Arguments.of(LocalDateTime.class,
                        LocalDateTime.of(2020, 1, 1, 12, 0), LocalDate.of(2020, 1, 1)),
                Arguments.of(LocalTime.class, LocalTime.of(12, 0), LocalDate.of(2020, 1, 1)),
                Arguments.of(OffsetTime.class,
                        OffsetTime.parse("12:00:00+02:00"), LocalTime.of(12, 0)),
                Arguments.of(Year.class, Year.of(2020), YearMonth.of(2020, 1)),
                Arguments.of(YearMonth.class, YearMonth.of(2020, 1), Year.of(2020)),
                Arguments.of(MonthDay.class, MonthDay.of(1, 1), YearMonth.of(2020, 1))
        );
    }

    // [modules-java8#389]
    @ParameterizedTest
    @MethodSource("supportedTypes")
    public void testAcceptsCompatibleEmbeddedObject(Class<?> targetType,
            Object validValue, Object incompatibleValue) throws Exception
    {
        assertSame(validValue, readEmbeddedValue(targetType, validValue));
    }

    // [modules-java8#389]
    @ParameterizedTest
    @MethodSource("supportedTypes")
    public void testRejectsIncompatibleEmbeddedObject(Class<?> targetType,
            Object validValue, Object incompatibleValue) throws Exception
    {
        // First: a value of completely unrelated type (as per #389, a CBOR byte string)
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> readEmbeddedValue(targetType, new byte[] { 1, 2, 3 }));
        verifyException(e, targetType.getName());

        // Second: a "near miss", that is, another `java.time` value; these would
        // NOT be caught by an unchecked/erased cast
        e = assertThrows(MismatchedInputException.class,
                () -> readEmbeddedValue(targetType, incompatibleValue));
        verifyException(e, targetType.getName());
    }

    // [modules-java8#389]
    @ParameterizedTest
    @MethodSource("supportedTypes")
    public void testAllowsNullEmbeddedObject(Class<?> targetType,
            Object validValue, Object incompatibleValue) throws Exception
    {
        assertNull(readEmbeddedValue(targetType, null));
    }

    private Object readEmbeddedValue(Class<?> targetType, Object embeddedValue)
            throws IOException
    {
        try (TokenBuffer buffer = new TokenBuffer(MAPPER, false)) {
            buffer.writeStartObject();
            buffer.writeFieldName("value");
            buffer.writeEmbeddedObject(embeddedValue);
            buffer.writeEndObject();

            JavaType mapType = MAPPER.getTypeFactory()
                    .constructMapType(Map.class, String.class, targetType);
            try (JsonParser parser = buffer.asParser()) {
                Map<?, ?> result = MAPPER.readValue(parser, mapType);
                return result.get("value");
            }
        }
    }
}
