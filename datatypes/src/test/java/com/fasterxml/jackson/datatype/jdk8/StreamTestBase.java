package com.fasterxml.jackson.datatype.jdk8;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.BaseStream;

import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({ "unqualified-field-access", "javadoc" })
public class StreamTestBase {

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    /** The should always be true after a serialization attempt closed. */
    final AtomicBoolean closed = new AtomicBoolean();

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

    /**
     * Resets closed to false.
     */
    @Before
    public void initClosedToFalse() {
        closed.set(false);
    }

    @Before
    public void initObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        objectMapper = mapper;
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnSuccess(S baseStream, Consumer<S> roundTrip) {

        assertFalse(closed.get());

        roundTrip.accept(baseStream.onClose(() -> closed.set(true)));

        assertTrue(closed.get());
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnRuntimeException(String exceptionMessage, Consumer<S> roundTrip,
            S baseStream) {

        assertFalse(closed.get());

        initExpectedException(RuntimeException.class, exceptionMessage);

        roundTrip.accept(baseStream.onClose(() -> closed.set(true)));

        assertTrue(closed.get());
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnIoException(String exceptionMessage, Consumer<S> roundTrip,
            S baseStream) {

        assertFalse(closed.get());

        initExpectedExceptionIoException(exceptionMessage);

        roundTrip.accept(baseStream.onClose(() -> closed.set(true)));

        assertTrue(closed.get());
    }

    <T, S extends BaseStream<T, S>> void assertClosesOnWrappedIoException(String exceptionMessage,
            Consumer<S> roundTrip, S baseStream) {

        final String actualMessage = "Unexpected IOException (of type java.io.IOException): " + exceptionMessage;

        assertFalse(closed.get());

        initExpectedExceptionIoException(actualMessage);

        roundTrip.accept(baseStream.onClose(() -> closed.set(true)));

        assertTrue(closed.get());
    }

    void initExpectedExceptionIoException(final String exceptionMessage) {

        this.expectedException.expect(new IsClosedMatcher());
        this.expectedException.expect(Is.isA(IOException.class));
        this.expectedException.expectMessage(exceptionMessage);
    }

    void initExpectedException(Class<? extends Throwable> cause, final String exceptionMessage) {
        this.expectedException.expect(AllOf.allOf(Is.isA(JsonMappingException.class), new IsClosedMatcher()));
        this.expectedException.expect(Is.isA(JsonMappingException.class));
        this.expectedException.expectCause(Is.isA(cause));
        this.expectedException.expectMessage(exceptionMessage);
    }

    /**
     * Matcher that matches when the {@link StreamTestBase#closed} value is set to true.
     */
    class IsClosedMatcher extends CustomMatcher<Object> {

        public IsClosedMatcher() {
            super("Check flag closed");
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            description.appendText("The onClose method was not called");
        }

        @Override
        public boolean matches(Object item) {

            return StreamTestBase.this.closed.get();
        }

    }
}
