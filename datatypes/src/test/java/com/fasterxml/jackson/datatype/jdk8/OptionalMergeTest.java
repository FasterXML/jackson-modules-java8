package com.fasterxml.jackson.datatype.jdk8;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalMergeTest extends ModuleTestBase
{
    // [modules-java8#214]
    static class OptionalListWrapper {
        @JsonMerge
        public Optional<List<String>> list = Optional.empty();
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    // [modules-java8#214]: ReferenceType of List, merge
    @Test
    public void testMergeToListViaRef() throws Exception
    {
        OptionalListWrapper base = MAPPER.readValue(a2q("{'list':['a']}"),
                OptionalListWrapper.class);
        assertNotNull(base.list);
        assertEquals(Arrays.asList("a"), base.list.get());

        OptionalListWrapper merged = MAPPER.readerForUpdating(base)
                .readValue(a2q("{'list':['b']}"));
        assertSame(base, merged);
        assertEquals(Arrays.asList("a", "b"), base.list.get());
    }
}
