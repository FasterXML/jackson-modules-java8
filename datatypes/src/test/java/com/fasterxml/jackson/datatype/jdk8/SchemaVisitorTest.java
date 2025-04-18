package com.fasterxml.jackson.datatype.jdk8;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.NumberType;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.*;

import static org.junit.jupiter.api.Assertions.*;

// trivial tests visitor used (mostly) for JSON Schema generation
public class SchemaVisitorTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    // for [datatype-jdk8#25]
    @Test
    public void testOptionalInteger() throws Exception
    {
        final AtomicReference<Object> result = new AtomicReference<>();
        MAPPER.acceptJsonFormatVisitor(OptionalInt.class,
                new JsonFormatVisitorWrapper.Base() {
            @Override
            public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
                return new JsonIntegerFormatVisitor.Base() {
                    @Override
                    public void numberType(NumberType t) {
                        result.set(t);
                    }
                };
            }
        });
        assertEquals(JsonParser.NumberType.INT, result.get());
    }

    // for [datatype-jdk8#25]
    @Test
    public void testOptionalLong() throws Exception
    {
        final AtomicReference<Object> result = new AtomicReference<>();
        MAPPER.acceptJsonFormatVisitor(OptionalLong.class,
                new JsonFormatVisitorWrapper.Base() {
            @Override
            public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
                return new JsonIntegerFormatVisitor.Base() {
                    @Override
                    public void numberType(NumberType t) {
                        result.set(t);
                    }
                };
            }
        });
        assertEquals(JsonParser.NumberType.LONG, result.get());
    }
    
    // for [datatype-jdk8#25]
    @Test
    public void testOptionalDouble() throws Exception
    {
        final AtomicReference<Object> result = new AtomicReference<>();
        MAPPER.acceptJsonFormatVisitor(OptionalDouble.class,
                new JsonFormatVisitorWrapper.Base() {
            @Override
            public JsonNumberFormatVisitor expectNumberFormat(JavaType type) {
                return new JsonNumberFormatVisitor.Base() {
                    @Override
                    public void numberType(NumberType t) {
                        result.set(t);
                    }
                };
            }
        });
        assertEquals(JsonParser.NumberType.DOUBLE, result.get());
    }
}
