package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import org.junit.*;

import static org.assertj.core.api.BDDAssertions.*;

public class JsonCreatorTest
{

	@Test
	public void shouldDeserializeClassWithJsonCreatorOnStaticMethod() throws Exception {

		// given
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ParameterNamesModule());

		// when
		String json = "{\"first\":\"1st\",\"second\":\"2nd\"}";
		ClassWithJsonCreatorOnStaticMethod actual = objectMapper.readValue(json, ClassWithJsonCreatorOnStaticMethod.class);

		then(actual).isEqualToComparingFieldByField(new ClassWithJsonCreatorOnStaticMethod("1st", "2nd"));
	}

	static class ClassWithJsonCreatorOnStaticMethod {
		final String first;
		final String second;

		ClassWithJsonCreatorOnStaticMethod(String first, String second) {
			this.first = first;
			this.second = second;
		}

		@JsonCreator
		static ClassWithJsonCreatorOnStaticMethod factory(String first, String second) {

			return new ClassWithJsonCreatorOnStaticMethod(first, second);
		}
	}
}
