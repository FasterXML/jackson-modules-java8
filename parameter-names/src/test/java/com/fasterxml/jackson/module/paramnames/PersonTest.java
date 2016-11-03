package com.fasterxml.jackson.module.paramnames;

import static org.assertj.core.api.BDDAssertions.then;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class PersonTest
{
    @Test
    public void shouldBeAbleToDeserializePerson() throws IOException
    {
        // given
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule());

        // when
        Person actual = objectMapper.readValue("{\"name\":\"joe\",\"surname\":\"smith\",\"nickname\":\"joey\"}", Person.class);

        // then
        Person expected = new Person("joe", "smith");
        expected.setNickname("joey");
        then(actual).isEqualToComparingFieldByField(expected);

    }
}
