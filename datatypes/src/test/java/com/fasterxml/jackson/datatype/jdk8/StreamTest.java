package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class StreamTest extends ModuleTestBase {

    private static final TypeReference<Collection<OptionalTest.TestBean>> COLLECTION_BEAN_TYPE = new TypeReference<Collection<OptionalTest.TestBean>>() {
    };
    private ObjectMapper MAPPER;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MAPPER = mapperWithModule();
    }

    public void testEmptyStream() throws Exception {
        assertEquals(Collections.emptyList(), roundTrip(Stream.empty()));
        assertEquals(Collections.emptyList(), roundTrip(DoubleStream.empty()));
        assertEquals(Collections.emptyList(), roundTrip(IntStream.empty()));
        assertEquals(Collections.emptyList(), roundTrip(LongStream.empty()));
    }

    public void testSingleElement() throws Exception {
        OptionalTest.TestBean bean = new OptionalTest.TestBean(10, "ten");
        assertEquals(Collections.singletonList("a"), roundTrip(Stream.of("a")));
        assertEquals(Collections.singletonList(bean), roundTrip(Stream.of(bean), COLLECTION_BEAN_TYPE));
        assertEquals(Collections.singletonList(1d), roundTrip(DoubleStream.of(1d)));
        assertEquals(Collections.singletonList(1), roundTrip(IntStream.of(1)));
        assertEquals(Collections.singletonList(1L), roundTrip(LongStream.of(1L)));
    }

    public void testMultiElements() throws Exception {
        OptionalTest.TestBean bean = new OptionalTest.TestBean(10, "ten");
        OptionalTest.TestBean bean2 = new OptionalTest.TestBean(2, "two");
        assertEquals(Arrays.asList("a", "B", "c"), roundTrip(Stream.of("a", "B", "c")));
        assertEquals(Arrays.asList(bean, bean2, bean, bean, bean2),
                roundTrip(Stream.of(bean, bean2, bean, bean, bean2), COLLECTION_BEAN_TYPE));
        assertEquals(Arrays.asList(1d, 0d, 6d, -3d), roundTrip(DoubleStream.of(1d, 0d, 6d, -3d)));
        assertEquals(Arrays.asList(1, 0, 6, -3), roundTrip(IntStream.of(1, 0, 6, -3)));
        assertEquals(Arrays.asList(1L, 0L, 6L, -3L), roundTrip(LongStream.of(1L, 0L, 6L, -3L)));
    }


    private <T> Collection<T> roundTrip(Stream<T> stream) throws IOException {
        return roundTrip(stream, new TypeReference<Collection<T>>() {
        });
    }

    private <T> Collection<T> roundTrip(Stream<T> stream,
                                        TypeReference<Collection<T>> typeRef) throws IOException {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(stream), typeRef);
    }


    private Collection<Double> roundTrip(DoubleStream stream) throws IOException {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(stream),
                new TypeReference<Collection<Double>>() {
                });
    }

    private Collection<Integer> roundTrip(IntStream stream) throws IOException {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(stream),
                new TypeReference<Collection<Integer>>() {
                });
    }

    private Collection<Long> roundTrip(LongStream stream) throws IOException {
        return MAPPER.readValue(MAPPER.writeValueAsBytes(stream),
                new TypeReference<Collection<Long>>() {
                });
    }

}
