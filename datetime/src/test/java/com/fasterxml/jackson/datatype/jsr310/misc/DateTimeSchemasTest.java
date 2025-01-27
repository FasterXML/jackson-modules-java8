package com.fasterxml.jackson.datatype.jsr310.misc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.*;
import com.fasterxml.jackson.datatype.jsr310.ModuleTestBase;

public class DateTimeSchemasTest extends ModuleTestBase
{
    static class VisitorWrapper implements JsonFormatVisitorWrapper {
        SerializerProvider serializerProvider;
        final String baseName;
        final Map<String, String> traversedProperties;

        public VisitorWrapper(SerializerProvider serializerProvider, String baseName, Map<String, String> traversedProperties) {
            this.serializerProvider = serializerProvider;
            this.baseName = baseName;
            this.traversedProperties = traversedProperties;
        }

        VisitorWrapper createSubtraverser(String bn) {
            return new VisitorWrapper(getProvider(), bn, traversedProperties);
        }

        public Map<String, String> getTraversedProperties() {
            return traversedProperties;
        }

        @Override
        public JsonObjectFormatVisitor expectObjectFormat(JavaType type) throws JsonMappingException {
            return new JsonObjectFormatVisitor.Base(serializerProvider) {
                @Override
                public void property(BeanProperty prop) throws JsonMappingException {
                    anyProperty(prop);
                }

                @Override
                public void optionalProperty(BeanProperty prop) throws JsonMappingException {
                    anyProperty(prop);
                }

                private void anyProperty(BeanProperty prop) throws JsonMappingException {
                    final String propertyName = prop.getFullName().toString();
                    traversedProperties.put(baseName + propertyName, "");
                    serializerProvider.findPrimaryPropertySerializer(prop.getType(), prop)
                            .acceptJsonFormatVisitor(createSubtraverser(baseName + propertyName + "."), prop.getType());
                }
            };
        }

        @Override
        public JsonArrayFormatVisitor expectArrayFormat(JavaType type) throws JsonMappingException {
            traversedProperties.put(baseName, "ARRAY/"+type.getGenericSignature());
            return null;
        }

        @Override
        public JsonStringFormatVisitor expectStringFormat(JavaType type) throws JsonMappingException {
            return new JsonStringFormatVisitor.Base() {
                @Override
                public void format(JsonValueFormat format) {
                    traversedProperties.put(baseName, "STRING/"+format.name());
                }
            };
        }

        @Override
        public JsonNumberFormatVisitor expectNumberFormat(JavaType type) throws JsonMappingException {
            return new JsonNumberFormatVisitor.Base() {
                @Override
                public void numberType(JsonParser.NumberType format) {
                    traversedProperties.put(baseName, "NUMBER/"+format.name());
                }
            };
        }

        @Override
        public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) throws JsonMappingException {
            return new JsonIntegerFormatVisitor.Base() {
                @Override
                public void numberType(JsonParser.NumberType numberType) {
                    traversedProperties.put(baseName + "numberType", "INTEGER/" + numberType.name());
                }

                @Override
                public void format(JsonValueFormat format) {
                    traversedProperties.put(baseName + "format", "INTEGER/" + format.name());
                }
            };
        }

        @Override
        public JsonBooleanFormatVisitor expectBooleanFormat(JavaType type) throws JsonMappingException {
            traversedProperties.put(baseName, "BOOLEAN");
            return new JsonBooleanFormatVisitor.Base();
        }

        @Override
        public JsonNullFormatVisitor expectNullFormat(JavaType type) throws JsonMappingException {
            return new JsonNullFormatVisitor.Base();
        }

        @Override
        public JsonAnyFormatVisitor expectAnyFormat(JavaType type) throws JsonMappingException {
            traversedProperties.put(baseName, "ANY");
            return new JsonAnyFormatVisitor.Base();
        }

        @Override
        public JsonMapFormatVisitor expectMapFormat(JavaType type) throws JsonMappingException {
            traversedProperties.put(baseName, "MAP");
            return new JsonMapFormatVisitor.Base(serializerProvider);
        }

        @Override
        public SerializerProvider getProvider() {
            return serializerProvider;
        }

        @Override
        public void setProvider(SerializerProvider provider) {
            this.serializerProvider = provider;
        }
    }

    private final ObjectMapper MAPPER = newMapper();

    // // // Local date/time types

    // [modules-java8#105]
    @Test
    public void testLocalTimeSchema() throws Exception
    {
        VisitorWrapper wrapper = new VisitorWrapper(null, "", new HashMap<String, String>());
        MAPPER.writer().acceptJsonFormatVisitor(LocalTime.class, wrapper);
        Map<String, String> properties = wrapper.getTraversedProperties();

        // By default, serialized as an int array, so:
        Assert.assertEquals(1, properties.size());
        _verifyIntArrayType(properties.get(""));

        // but becomes date/time
        wrapper = new VisitorWrapper(null, "", new HashMap<String, String>());
        MAPPER.writer().without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .acceptJsonFormatVisitor(LocalTime.class, wrapper);
        properties = wrapper.getTraversedProperties();
        _verifyTimeType(properties.get(""));
    }

    @Test
    public void testLocalDateSchema() throws Exception
    {
        VisitorWrapper wrapper = new VisitorWrapper(null, "", new HashMap<String, String>());
        MAPPER.writer().acceptJsonFormatVisitor(LocalDate.class, wrapper);
        Map<String, String> properties = wrapper.getTraversedProperties();

        // By default, serialized as an int array, so:
        Assert.assertEquals(1, properties.size());
        _verifyIntArrayType(properties.get(""));

        // but becomes date/time
        wrapper = new VisitorWrapper(null, "", new HashMap<String, String>());
        MAPPER.writer().without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .acceptJsonFormatVisitor(LocalDate.class, wrapper);
        properties = wrapper.getTraversedProperties();
        _verifyDateType(properties.get(""));
    }

    // // // Zoned date/time types

    @Test
    public void testDateTimeSchema() throws Exception
    {
        VisitorWrapper wrapper = new VisitorWrapper(null, "", new HashMap<String, String>());
        MAPPER.writer().acceptJsonFormatVisitor(ZonedDateTime.class, wrapper);
        Map<String, String> properties = wrapper.getTraversedProperties();

        // By default, serialized as an int array, so:
        Assert.assertEquals(1, properties.size());
        _verifyBigDecimalType(properties.get(""));

        // but becomes long
        wrapper = new VisitorWrapper(null, "", new HashMap<String, String>());
        MAPPER.writer()
                .without(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
                .acceptJsonFormatVisitor(ZonedDateTime.class, wrapper);
        properties = wrapper.getTraversedProperties();
        _verifyLongType(properties.get("numberType"));
        _verifyLongFormat(properties.get("format"));

        // but becomes date/time
        wrapper = new VisitorWrapper(null, "", new HashMap<String, String>());
        MAPPER.writer().without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .acceptJsonFormatVisitor(ZonedDateTime.class, wrapper);
        properties = wrapper.getTraversedProperties();
        _verifyDateTimeType(properties.get(""));
    }

    private void _verifyIntArrayType(String desc) {
        Assert.assertEquals("ARRAY/Ljava/util/List<Ljava/lang/Integer;>;", desc);
    }

    private void _verifyTimeType(String desc) {
        Assert.assertEquals("STRING/TIME", desc);
    }

    private void _verifyDateType(String desc) {
        Assert.assertEquals("STRING/DATE", desc);
    }

    private void _verifyDateTimeType(String desc) {
        Assert.assertEquals("STRING/DATE_TIME", desc);
    }

    private void _verifyBigDecimalType(String desc) {
        Assert.assertEquals("NUMBER/BIG_DECIMAL", desc);
    }

    private void _verifyLongType(String desc) {
        Assert.assertEquals("INTEGER/LONG", desc);
    }

    private void _verifyLongFormat(String desc) {
        Assert.assertEquals("INTEGER/UTC_MILLISEC", desc);
    }
}
