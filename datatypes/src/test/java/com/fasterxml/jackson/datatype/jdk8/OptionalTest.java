package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.annotation.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

public class OptionalTest extends ModuleTestBase
{
    private static final TypeReference<Optional<String>> OPTIONAL_STRING_TYPE = new TypeReference<Optional<String>>() {};
    private static final TypeReference<Optional<TestBean>> OPTIONAL_BEAN_TYPE = new TypeReference<Optional<TestBean>>() {};

    public static class TestBean
    {
        public int foo;
        public String bar;

        @JsonCreator
        public TestBean(@JsonProperty("foo") int foo, @JsonProperty("bar") String bar)
        {
            this.foo = foo;
            this.bar = bar;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj.getClass() != getClass()) {
                return false;
            }
            TestBean castObj = (TestBean) obj;
            return castObj.foo == foo && Objects.equals(castObj.bar, bar);
        }

        @Override
        public int hashCode() {
            return foo ^ bar.hashCode();
        }
    }

    static class OptionalStringBean {
        public Optional<String> value;

        public OptionalStringBean() { }
        OptionalStringBean(String str) {
            value = Optional.ofNullable(str);
        }
    }
    
    static class OptionalLongBean {
        public OptionalLong value;

        public OptionalLongBean() { value = OptionalLong.empty(); }
        OptionalLongBean(long v) {
            value = OptionalLong.of(v);
        }
    }
    
    // [datatype-jdk8#4]
    static class Issue4Entity {
        private final Optional<String> data;
 
        @JsonCreator
        public Issue4Entity(@JsonProperty("data") Optional<String> data) {
            this.data = Objects.requireNonNull(data, "data");
        }
 
        @JsonProperty ("data")
        public Optional<String> data() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Issue4Entity entity = (Issue4Entity) o;
            return data.equals(entity.data);
        }
    }

    static class BooleanBean {
        public Optional<Boolean> value;

        public BooleanBean() { }
        public BooleanBean(Boolean b) {
            value = Optional.ofNullable(b);
        }
    }

    static class CaseChangingStringWrapper {
        @JsonSerialize(contentUsing=UpperCasingSerializer.class)
        @JsonDeserialize(contentUsing=LowerCasingDeserializer.class)
        public Optional<String> value;

        CaseChangingStringWrapper() { }
        public CaseChangingStringWrapper(String s) { value = Optional.ofNullable(s); }
    }

    @SuppressWarnings("serial")
    public static class UpperCasingSerializer extends StdScalarSerializer<String>
    {
        public UpperCasingSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator gen,
                SerializerProvider provider) throws IOException {
            gen.writeString(value.toUpperCase());
        }
    }

    @SuppressWarnings("serial")
    public static class LowerCasingDeserializer extends StdScalarDeserializer<String>
    {
        public LowerCasingDeserializer() { super(String.class); }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            return p.getText().toLowerCase();
        }
    }

    private ObjectMapper MAPPER;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        MAPPER = mapperWithModule();
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testStringAbsent() throws Exception
    {
        assertFalse(roundtrip(Optional.empty(), OPTIONAL_STRING_TYPE).isPresent());
    }

    public void testStringPresent() throws Exception
    {
        assertEquals("test", roundtrip(Optional.of("test"), OPTIONAL_STRING_TYPE).get());
    }

    public void testIntAbsent() throws Exception
    {
        assertFalse(MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalInt.empty()), OptionalInt.class).isPresent());
    }

    public void testIntPresent() throws Exception
    {
        assertEquals(5, MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalInt.of(5)), OptionalInt.class).getAsInt());
    }

    public void testLongAbsent() throws Exception
    {
        assertFalse(MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalLong.empty()), OptionalLong.class).isPresent());
    }

    public void testLongPresent() throws Exception
    {
        assertEquals(Long.MAX_VALUE, MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalLong.of(Long.MAX_VALUE)), OptionalLong.class).getAsLong());
    }

    public void testDoubleAbsent() throws Exception
    {
        assertFalse(MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalInt.empty()), OptionalInt.class).isPresent());
    }

    public void testDoublePresent() throws Exception
    {
        assertEquals(Double.MIN_VALUE, MAPPER.readValue(MAPPER.writeValueAsBytes(OptionalDouble.of(Double.MIN_VALUE)), OptionalDouble.class).getAsDouble());
    }

    public void testBeanAbsent() throws Exception
    {
        assertFalse(roundtrip(Optional.empty(), OPTIONAL_BEAN_TYPE).isPresent());
    }

    public void testBeanPresent() throws Exception
    {
        final TestBean bean = new TestBean(Integer.MAX_VALUE, "woopwoopwoopwoopwoop");
        assertEquals(bean, roundtrip(Optional.of(bean), OPTIONAL_BEAN_TYPE).get());
    }

    // [issue#4]
    public void testBeanWithCreator() throws Exception
    {
        final Issue4Entity emptyEntity = new Issue4Entity(Optional.empty());
        final String json = MAPPER.writeValueAsString(emptyEntity);
        
        final Issue4Entity deserialisedEntity = MAPPER.readValue(json, Issue4Entity.class);
        if (!deserialisedEntity.equals(emptyEntity)) {
            throw new IOException("Entities not equal");
        }
    }
    
    // [issue#4]
    public void testOptionalStringInBean() throws Exception
    {
        OptionalStringBean bean = MAPPER.readValue("{\"value\":\"xyz\"}", OptionalStringBean.class);
        assertNotNull(bean.value);
        assertEquals("xyz", bean.value.get());
    }

    // To support [datatype-jdk8#8]
    public void testExcludeIfOptionalAbsent() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        assertEquals(aposToQuotes("{'value':'foo'}"),
                mapper.writeValueAsString(new OptionalStringBean("foo")));
        // absent is not strictly null so
        assertEquals(aposToQuotes("{'value':null}"),
                mapper.writeValueAsString(new OptionalStringBean(null)));

        // however:
        mapper = mapperWithModule()
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        assertEquals(aposToQuotes("{'value':'foo'}"),
                mapper.writeValueAsString(new OptionalStringBean("foo")));
        assertEquals(aposToQuotes("{}"),
                mapper.writeValueAsString(new OptionalStringBean(null)));
    }

    public void testExcludeIfOptionalLongAbsent() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        assertEquals(aposToQuotes("{'value':123}"),
                mapper.writeValueAsString(new OptionalLongBean(123L)));
        // absent is not strictly null so
        assertEquals(aposToQuotes("{'value':null}"),
                mapper.writeValueAsString(new OptionalLongBean()));

        // however:
        mapper = mapperWithModule()
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        assertEquals(aposToQuotes("{'value':456}"),
                mapper.writeValueAsString(new OptionalLongBean(456L)));
        assertEquals(aposToQuotes("{}"),
                mapper.writeValueAsString(new OptionalLongBean()));
    }

    // for [datatype-jdk8#23]
    public void testBoolean() throws Exception
    {
        // First, serialization
        String json = MAPPER.writeValueAsString(new BooleanBean(true));
        assertEquals(aposToQuotes("{'value':true}"), json);
        json = MAPPER.writeValueAsString(new BooleanBean());
        assertEquals(aposToQuotes("{'value':null}"), json);
        json = MAPPER.writeValueAsString(new BooleanBean(null));
        assertEquals(aposToQuotes("{'value':null}"), json);

        // then deser
        BooleanBean b = MAPPER.readValue(aposToQuotes("{'value':null}"), BooleanBean.class);
        assertNotNull(b.value);
        assertFalse(b.value.isPresent());

        b = MAPPER.readValue(aposToQuotes("{'value':false}"), BooleanBean.class);
        assertNotNull(b.value);
        assertTrue(b.value.isPresent());
        assertFalse(b.value.get().booleanValue());

        b = MAPPER.readValue(aposToQuotes("{'value':true}"), BooleanBean.class);
        assertNotNull(b.value);
        assertTrue(b.value.isPresent());
        assertTrue(b.value.get().booleanValue());

        // and looks like a special, somewhat non-conforming case is what a user had
        // issues with
        b = MAPPER.readValue(aposToQuotes("{'value':''}"), BooleanBean.class);
        assertNotNull(b.value);
        assertFalse(b.value.isPresent());
    }

    public void testWithCustomDeserializer() throws Exception
    {
        CaseChangingStringWrapper w = MAPPER.readValue(aposToQuotes("{'value':'FoobaR'}"),
                CaseChangingStringWrapper.class);
        assertEquals("foobar", w.value.get());
    }

    public void testCustomSerializer() throws Exception
    {
        final String VALUE = "fooBAR";
        String json = MAPPER.writeValueAsString(new CaseChangingStringWrapper(VALUE));
        assertEquals(json, aposToQuotes("{'value':'FOOBAR'}"));
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private <T> Optional<T> roundtrip(Optional<T> obj, TypeReference<Optional<T>> type) throws IOException
    {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(obj), type);
    }
}
