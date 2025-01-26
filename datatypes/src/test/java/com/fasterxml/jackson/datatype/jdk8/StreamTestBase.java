package com.fasterxml.jackson.datatype.jdk8;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.BaseStream;

import org.junit.jupiter.api.BeforeEach;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

public class StreamTestBase {

    ObjectMapper objectMapper;

    /**
     * Throws the supplied checked exception without enforcing checking.
     *
     * @param t the throwable to sneaky throw.
     */
    static final void sneakyThrow(final Throwable t) {
        castAndThrow(t);
    }

    /**
     * Uses erasure to throw checked exceptions as unchecked.
     * <p>Called by {@link #sneakyThrow(Throwable)}</p>
     *
     * @param <T> the throwable type
     * @param t throwable to throw
     * @throws T the supplied throwable
     */
    @SuppressWarnings("unchecked")
    static <T extends Throwable> void castAndThrow(final Throwable t) throws T {
        throw (T) t;
    }

    @BeforeEach
    public void initObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        objectMapper = mapper;
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnSuccess(S baseStream, Consumer<S> roundTrip) {
        
        AtomicBoolean closed = new AtomicBoolean();
        
        roundTrip.accept(baseStream.onClose(() -> closed.set(true)));

        assertTrue(closed.get());
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnRuntimeException(String exMessage, Consumer<S> roundTrip,
            S baseStream) {

        AtomicBoolean closed = new AtomicBoolean();

        Exception actualEx = assertThrows(Exception.class,
            () -> roundTrip.accept(baseStream.onClose(() -> closed.set(true))));

        initExpectedException(actualEx, exMessage, closed);
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnIoException(String exMessage, Consumer<S> roundTrip,
            S baseStream) {

        AtomicBoolean closed = new AtomicBoolean();

        Exception actual = assertThrows(Exception.class,
            () -> roundTrip.accept(baseStream.onClose(() -> closed.set(true))));

        initExpectedExceptionIoException(actual, closed,
            "Unexpected IOException (of type java.io.IOException): " + exMessage);
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnWrappedIoException(String expectedMsg,
            Consumer<S> roundTrip, S baseStream) {

        AtomicBoolean closed = new AtomicBoolean();
        
        Exception actual = assertThrows(Exception.class,
            () -> roundTrip.accept(baseStream.onClose(() -> closed.set(true))));

        initExpectedExceptionIoException(actual, closed,
            "Unexpected IOException (of type java.io.IOException): " + expectedMsg);
    }

    void initExpectedExceptionIoException(final Exception actualException, AtomicBoolean closed, final String exceptionMessage)
    {
        assertInstanceOf(IOException.class, actualException);
        assertTrue(closed.get());
        assertTrue(actualException.getMessage().contains(exceptionMessage));
    }

    void initExpectedException(final Exception actualException, final String exceptionMessage, AtomicBoolean closed)
    {
        assertInstanceOf(JsonMappingException.class, actualException);
        assertTrue(closed.get());
        assertTrue(actualException.getMessage().contains(exceptionMessage));
    }

}
