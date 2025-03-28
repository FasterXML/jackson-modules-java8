package com.fasterxml.jackson.datatype.jdk8;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class PolymorphicOptionalTest extends ModuleTestBase
{
    // For [datatype-jdk8#14]
    public static class Container {
        public Optional<Contained> contained;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY)
    @JsonSubTypes({
        @JsonSubTypes.Type(name = "ContainedImpl", value = ContainedImpl.class),
    })
    public static interface Contained { }

    public static class ContainedImpl implements Contained { }

    private final ObjectMapper MAPPER = mapperWithModule();
    
    // [datatype-jdk8#14]
    @Test
    public void testPolymorphic14() throws Exception
    {
        final Container dto = new Container();
        dto.contained = Optional.of(new ContainedImpl());
        
        final String json = MAPPER.writeValueAsString(dto);

        final Container fromJson = MAPPER.readValue(json, Container.class);
        assertNotNull(fromJson.contained);
        assertTrue(fromJson.contained.isPresent());
        assertSame(ContainedImpl.class, fromJson.contained.get().getClass());
    }
}
