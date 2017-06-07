package com.fasterxml.jackson.module.paramnames.failing;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import org.junit.*;

import java.io.IOException;

import static org.assertj.core.api.BDDAssertions.*;

public class JsonCreatorTest
{
	@Test
	public void shouldDeserializeUsingDefaultPropertyCreatorSetting() throws IOException {
		// given
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new ParameterNamesModule());
		objectMapper.enable(MapperFeature.CREATOR_MODE_DEFAULT_PROPERTIES);
		int givenValue = 1;

		// when
		SinglePropertyValueClass actual = objectMapper.readValue("{\"value\":\"" + givenValue + "\"}",
		                                                         SinglePropertyValueClass.class);
		// then
        then(actual).isEqualToComparingFieldByField(new SinglePropertyValueClass(givenValue));
	}

	static class SinglePropertyValueClass {
		private final Integer value;

		SinglePropertyValueClass(Integer value) {
			this.value = value;
		}

		public Integer getValue() {
			return value;
		}
	}
}
