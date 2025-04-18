package com.fasterxml.jackson.datatype.jdk8;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalBasicTest extends ModuleTestBase
{
    public static final class OptionalData {
        public Optional<String> myString;
    }

    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    public static final class OptionalGenericData<T> {
        Optional<T> myData;
    }
	
    @JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class)
    public static class Unit
    {
        public Optional<Unit> baseUnit;
        
		public Unit() {
		}

		public Unit(final Optional<Unit> u) {
			baseUnit = u;
		}
        
        public void link(final Unit u) {
            baseUnit = Optional.of(u);
        }
    }

    // To test handling of polymorphic value types
    
    public static class Container {
        public Optional<Contained> contained;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY)
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "ContainedImpl", value = ContainedImpl.class),
    })
    public static interface Contained { }

    public static class ContainedImpl implements Contained { }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

	@Test
    public void testOptionalTypeResolution() throws Exception {
		// With 2.6, we need to recognize it as ReferenceType
		JavaType t = MAPPER.constructType(Optional.class);
		assertNotNull(t);
		assertEquals(Optional.class, t.getRawClass());
		assertTrue(t.isReferenceType());
	}

	@Test
	public void testDeserAbsent() throws Exception {
		Optional<?> value = MAPPER.readValue("null",
				new TypeReference<Optional<String>>() {
				});
		assertFalse(value.isPresent());
	}

	@Test
	public void testDeserSimpleString() throws Exception {
		Optional<?> value = MAPPER.readValue("\"simpleString\"",
				new TypeReference<Optional<String>>() {
				});
		assertTrue(value.isPresent());
		assertEquals("simpleString", value.get());
	}

	@Test
	public void testDeserInsideObject() throws Exception {
		OptionalData data = MAPPER.readValue("{\"myString\":\"simpleString\"}",
				OptionalData.class);
		assertTrue(data.myString.isPresent());
		assertEquals("simpleString", data.myString.get());
	}

	@Test
	public void testDeserComplexObject() throws Exception {
		TypeReference<Optional<OptionalData>> type = new TypeReference<Optional<OptionalData>>() {
		};
		Optional<OptionalData> data = MAPPER.readValue(
				"{\"myString\":\"simpleString\"}", type);
		assertTrue(data.isPresent());
		assertTrue(data.get().myString.isPresent());
		assertEquals("simpleString", data.get().myString.get());
	}

	@Test
	public void testDeserGeneric() throws Exception {
		TypeReference<Optional<OptionalGenericData<String>>> type = new TypeReference<Optional<OptionalGenericData<String>>>() {
		};
		Optional<OptionalGenericData<String>> data = MAPPER.readValue(
				"{\"myData\":\"simpleString\"}", type);
		assertTrue(data.isPresent());
		assertTrue(data.get().myData.isPresent());
		assertEquals("simpleString", data.get().myData.get());
	}

	@Test
	public void testSerAbsent() throws Exception {
		String value = MAPPER.writeValueAsString(Optional.empty());
		assertEquals("null", value);
	}

	@Test
	public void testSerSimpleString() throws Exception {
		String value = MAPPER.writeValueAsString(Optional.of("simpleString"));
		assertEquals("\"simpleString\"", value);
	}

	@Test
	public void testSerInsideObject() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = Optional.of("simpleString");
		String value = MAPPER.writeValueAsString(data);
		assertEquals("{\"myString\":\"simpleString\"}", value);
	}

	@Test
	public void testSerComplexObject() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = Optional.of("simpleString");
		String value = MAPPER.writeValueAsString(Optional.of(data));
		assertEquals("{\"myString\":\"simpleString\"}", value);
	}

	@Test
	public void testSerGeneric() throws Exception {
		OptionalGenericData<String> data = new OptionalGenericData<String>();
		data.myData = Optional.of("simpleString");
		String value = MAPPER.writeValueAsString(Optional.of(data));
		assertEquals("{\"myData\":\"simpleString\"}", value);
	}

	@Test
	public void testSerNonNull() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = Optional.empty();
		// NOTE: pass 'true' to ensure "legacy" setting
		String value = mapperWithModule(true).setSerializationInclusion(
				JsonInclude.Include.NON_NULL).writeValueAsString(data);
		assertEquals("{}", value);
	}

	@Test
	public void testSerOptDefault() throws Exception {
		OptionalData data = new OptionalData();
		data.myString = Optional.empty();
		String value = mapperWithModule().setSerializationInclusion(
				JsonInclude.Include.ALWAYS).writeValueAsString(data);
		assertEquals("{\"myString\":null}", value);
	}

	@Test
    public void testSerOptNull() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_NULL).writeValueAsString(data);
        assertEquals("{}", value);
    }

    @SuppressWarnings("deprecation")
	@Test
    public void testSerOptDisableAsNull() throws Exception {
        final OptionalData data = new OptionalData();
        data.myString = Optional.empty();

        Jdk8Module mod = new Jdk8Module().configureAbsentsAsNulls(false);
        ObjectMapper mapper = new ObjectMapper().registerModule(mod)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        assertEquals("{\"myString\":null}", mapper.writeValueAsString(data));

        // but do exclude with NON_EMPTY
        mapper = new ObjectMapper().registerModule(mod)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        assertEquals("{}", mapper.writeValueAsString(data));

        // and with new (2.6) NON_ABSENT
        mapper = new ObjectMapper().registerModule(mod)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
        assertEquals("{}", mapper.writeValueAsString(data));
    }

	@Test
    public void testSerOptNonEmpty() throws Exception {
        OptionalData data = new OptionalData();
        data.myString = null;
        String value = mapperWithModule().setSerializationInclusion(
                JsonInclude.Include.NON_EMPTY).writeValueAsString(data);
        assertEquals("{}", value);
    }

	@Test
    public void testWithTypingEnabled() throws Exception {
		final ObjectMapper objectMapper = mapperWithModule();
		// ENABLE TYPING
		objectMapper.activateDefaultTyping(new NoCheckSubTypeValidator(),
				        ObjectMapper.DefaultTyping.OBJECT_AND_NON_CONCRETE);

		final OptionalData myData = new OptionalData();
		myData.myString = Optional.ofNullable("abc");

		final String json = objectMapper.writeValueAsString(myData);
		final OptionalData deserializedMyData = objectMapper.readValue(json,
				OptionalData.class);
		assertEquals(myData.myString, deserializedMyData.myString);
	}

	@Test
	public void testObjectId() throws Exception {
		final Unit input = new Unit();
		input.link(input);
		String json = MAPPER.writeValueAsString(input);
		Unit result = MAPPER.readValue(json, Unit.class);
		assertNotNull(result);
		assertNotNull(result.baseUnit);
		assertTrue(result.baseUnit.isPresent());
		Unit base = result.baseUnit.get();
		assertSame(result, base);
	}

	@Test
	public void testOptionalCollection() throws Exception {

		TypeReference<List<Optional<String>>> typeReference = new TypeReference<List<Optional<String>>>() {
		};

		List<Optional<String>> list = new ArrayList<Optional<String>>();
		list.add(Optional.of("2014-1-22"));
		list.add(Optional.<String> empty());
		list.add(Optional.of("2014-1-23"));

		String str = MAPPER.writeValueAsString(list);
		assertEquals("[\"2014-1-22\",null,\"2014-1-23\"]", str);

		List<Optional<String>> result = MAPPER.readValue(str, typeReference);
		assertEquals(list.size(), result.size());
		for (int i = 0; i < list.size(); ++i) {
			assertEquals(list.get(i), result.get(i), "Entry #" + i);
		}
	}

	@Test
	public void testPolymorphic() throws Exception
	{
	    final Container dto = new Container();
	    dto.contained = Optional.of((Contained) new ContainedImpl());

	    final String json = MAPPER.writeValueAsString(dto);

	    final Container fromJson = MAPPER.readValue(json, Container.class);
	    assertNotNull(fromJson.contained);
	    assertTrue(fromJson.contained.isPresent());
	    assertSame(ContainedImpl.class, fromJson.contained.get().getClass());
	}
}
