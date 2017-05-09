package com.fasterxml.jackson.datatype.jdk8;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.stream.DoubleStream;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({ "unqualified-field-access", "javadoc" })
public class DoubleStreamSerializerTest extends StreamTestBase {

    final double[] empty = {};

    final double[] single = { 1L };

    final double[] multipleValues = { Double.MIN_VALUE, Double.MAX_VALUE, 1.0, 0.0, 6.0, -3.0 };

    final String exceptionMessage = "DoubleStream peek threw";

    @Before
    public void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        objectMapper = mapper;
    }

    @Test
    public void testEmptyStream() throws Exception {

        assertArrayEquals(empty, roundTrip(DoubleStream.empty()), 0.0);
    }

    @Test
    public void testSingleElement() throws Exception {

        assertArrayEquals(single, roundTrip(DoubleStream.of(single)), 0.0);
    }

    @Test
    public void testMultiElements() throws Exception {

        assertArrayEquals(multipleValues, roundTrip(DoubleStream.of(multipleValues)), 0.0);
    }

    @Test
    public void testDoubleStreamCloses() throws Exception {

        assertClosesOnSuccess(DoubleStream.of(multipleValues), this::roundTrip);
    }

    @Test
    public void testDoubleStreamClosesOnRuntimeException() throws Exception {

        assertClosesOnRuntimeException(exceptionMessage, this::roundTrip, DoubleStream.of(multipleValues)
            .peek(e -> {
                throw new RuntimeException(exceptionMessage);
            }));

    }

    @Test
    public void testDoubleStreamClosesOnSneakyIOException() throws Exception {

        assertClosesOnIoException(exceptionMessage, this::roundTrip, DoubleStream.of(multipleValues)
            .peek(e -> {
                sneakyThrow(new IOException(exceptionMessage));
            }));

    }

    @Test
    public void testDoubleStreamClosesOnWrappedIoException() {

        assertClosesOnWrappedIoException(exceptionMessage, this::roundTrip, DoubleStream.of(multipleValues)
            .peek(e -> {
                throw new WrappedIOException(new IOException(exceptionMessage));
            }));

    }

    private double[] roundTrip(DoubleStream stream) {
        try {
            return objectMapper.readValue(objectMapper.writeValueAsBytes(stream), double[].class);
        } catch (IOException e) {
            sneakyThrow(e);
            return null;
        }
    }
}
