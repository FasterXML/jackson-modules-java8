package com.fasterxml.jackson.module.paramnames;

import java.io.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.*;

import static org.junit.Assert.assertEquals;

public class JDKSerializabilityTest extends ModuleTestBase
{
    static class BooleanBean {
        public boolean value = true;
    }

    @Test
    public void testJDKSerializability() throws Exception {
        final BooleanBean input = new BooleanBean();
        ObjectMapper mapper = newMapper();

        // No types supported, but serialize... something
        String json1 = mapper.writeValueAsString(input);

        // validate we can still use it to deserialize jackson objects
        ObjectMapper thawedMapper = serializeAndDeserialize(mapper);
        String json2 = thawedMapper.writeValueAsString(input);

        assertEquals(json1, json2);

        BooleanBean result = thawedMapper.readValue(json1, BooleanBean.class);
        assertEquals(input.value, result.value);
    }

    private ObjectMapper serializeAndDeserialize(ObjectMapper mapper) throws Exception {
        //verify serialization
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

        outputStream.writeObject(mapper);
        byte[] serializedBytes = byteArrayOutputStream.toByteArray();

        //verify deserialization
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedBytes);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);

        Object deserializedObject = inputStream.readObject();
        return (ObjectMapper) deserializedObject;
    }
}
