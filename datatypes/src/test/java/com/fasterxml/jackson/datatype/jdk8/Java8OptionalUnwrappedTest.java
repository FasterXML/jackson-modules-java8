package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;

import static org.junit.jupiter.api.Assertions.*;

public class Java8OptionalUnwrappedTest extends ModuleTestBase
{
	static class Child {
		public String name = "Bob";
	}

	static class Parent {
		private Child child = new Child();

		@JsonUnwrapped
		public Child getChild() {
			return child;
		}
	}

	static class OptionalParent {
		@JsonUnwrapped(prefix = "XX.")
		public Optional<Child> child = Optional.of(new Child());
	}

	static class Bean {
	    public String id;
	    @JsonUnwrapped(prefix="child")
	    public Optional<Bean2> bean2;

	    public Bean(String id, Optional<Bean2> bean2) {
	        this.id = id;
	        this.bean2 = bean2;
	    }
	}

	static class Bean2 {
	    public String name;
	}	

	@Test
	public void testUntypedWithOptionalsNotNulls() throws Exception
	{
		final ObjectMapper mapper = mapperWithModule(false);
		String jsonExp = a2q("{'XX.name':'Bob'}");
		String jsonAct = mapper.writeValueAsString(new OptionalParent());
		assertEquals(jsonExp, jsonAct);
	}

	// for [datatype-jdk8#20]
	@Test
	public void testShouldSerializeUnwrappedOptional() throws Exception {
         final ObjectMapper mapper = mapperWithModule(false);
	    
	    assertEquals("{\"id\":\"foo\"}",
	            mapper.writeValueAsString(new Bean("foo", Optional.<Bean2>empty())));
	}

	// for [datatype-jdk8#26]
	@Test
	public void testPropogatePrefixToSchema() throws Exception {
        final ObjectMapper mapper = mapperWithModule(false);

		final AtomicReference<String> propertyName = new AtomicReference<>();
		mapper.acceptJsonFormatVisitor(OptionalParent.class, new JsonFormatVisitorWrapper.Base(new DefaultSerializerProvider.Impl()) {
			@Override
			public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
				return new JsonObjectFormatVisitor.Base(getProvider()) {
					@Override
					public void optionalProperty(BeanProperty prop) {
						propertyName.set(prop.getName());
					}
				};
			}
		});

		assertEquals("XX.name", propertyName.get());
	}
}
