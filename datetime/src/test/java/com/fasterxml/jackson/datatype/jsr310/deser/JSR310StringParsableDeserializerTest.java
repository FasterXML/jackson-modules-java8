package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
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

public class JSR310StringParsableDeserializerTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    static Stream<Arguments> supportedTypes()
    {
        return Stream.of(
                Arguments.of(Period.class, Period.ofDays(1)),
                Arguments.of(ZoneId.class, ZoneId.of("Europe/Helsinki")),
                Arguments.of(ZoneOffset.class, ZoneOffset.ofHours(2))
        );
    }

    // [modules-java8#389]
    @ParameterizedTest
    @MethodSource("supportedTypes")
    public void testRejectsIncompatibleEmbeddedObject(Class<?> targetType,
            Object validValue) throws Exception
    {
        assertSame(validValue, readEmbeddedValue(targetType, validValue));

        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> readEmbeddedValue(targetType, new byte[] { 1, 2, 3 }));
        verifyException(e, targetType.getName());
    }

    @Test
    public void testAllowsNullEmbeddedObject() throws Exception
    {
        assertNull(readEmbeddedValue(ZoneId.class, null));
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
