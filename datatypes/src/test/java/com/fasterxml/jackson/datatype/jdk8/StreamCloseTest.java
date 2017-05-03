package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.BaseStream;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;


import org.hamcrest.CustomMatcher;
import org.hamcrest.Description;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StreamCloseTest  {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    private ObjectMapper MAPPER;

    AtomicBoolean closed = new AtomicBoolean();
    
    static final void sneakyThrow( final Throwable t) {
        castAndThrow(t);
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable> void castAndThrow(final Throwable t) throws T {
        throw (T) t;
    }
    
    @Before
    public void setUp() throws Exception {
        MAPPER = new ObjectMapper().registerModule(new Jdk8Module());
        closed.set(false);
    }
    
    @Test
    public void testStreamCloses() throws Exception {
   	assertClosesOnSuccess(Stream.of("a"));
    }
    
    @Test
    public void testStreamClosesOnRuntimeException() throws Exception {
 	
 	String exceptionMessage = "Stream peek threw";
	
 	assertClosesOnRuntimeException(exceptionMessage,Stream.of("a")
		.peek(d-> {
		    throw new RuntimeException(exceptionMessage);
		}));

 
    }    
 
    @Test
    public void testStreamClosesOnSneakyIOException() throws Exception {
 	
 	String exceptionMessage = "Stream peek threw";
 	
 	assertClosesOnIoException(exceptionMessage,Stream.of("a")
 		.peek(d-> {
 		   sneakyThrow(new IOException(exceptionMessage));
 		}));
 	
    }
    

    @Test
    public void testStreamClosesOnWrappedIoException() throws Exception {
 	
	final String exceptionMessage = "Stream peek threw";
 	
 	assertClosesOnWrappedIoException(exceptionMessage,Stream.of("a")
 		.peek(d-> {
 		   throw new WrappedIOException(new IOException(exceptionMessage));
 		}));

    }
    
    
    @Test
    public void testLongStreamCloses() throws Exception {
   	assertClosesOnSuccess( LongStream.of(1L));
    }
    
    @Test
    public void testLongStreamClosesOnRuntimeException() throws Exception {
 	
 
 	final String exceptionMessage = "LongStream peek threw";
	
 	assertClosesOnRuntimeException(exceptionMessage,LongStream.of(1L)
		.peek(d-> {
		    throw new RuntimeException(exceptionMessage);
		}));

    }

    @Test
    public void testLongStreamClosesOnSneakyIOException() throws Exception {
 	
 	String exceptionMessage = "LongStream peek threw";
 	
 	assertClosesOnIoException(exceptionMessage,LongStream.of(1L)
 		.peek(d-> {
 		   sneakyThrow(new IOException(exceptionMessage));
 		}));
 	
    }   
    
    @Test
    public void testLongStreamClosesOnWrappedIoException() throws Exception {
 	
	final String exceptionMessage = "LongStream peek threw";
 	
 	assertClosesOnWrappedIoException(exceptionMessage,LongStream.of(1L)
 		.peek(d-> {
 		   throw new WrappedIOException(new IOException(exceptionMessage));
 		}));


    }
    
    @Test
    public void testIntStreamCloses() throws Exception {
	
	assertClosesOnSuccess( IntStream.of(1));
  
    }
    
    @Test
    public void testIntStreamClosesOnRuntimeException() throws Exception {
	
	String exceptionMessage = "IntStream peek threw";
	
 	assertClosesOnRuntimeException(exceptionMessage,IntStream.of(1)
		.peek(d-> {
		    throw new RuntimeException(exceptionMessage);
		}));
    }
    
    @Test
    public void testIntStreamClosesOnSneakyIOException() throws Exception {
 	
 	String exceptionMessage = "IntStream peek threw";
 	
 	assertClosesOnIoException(exceptionMessage,IntStream.of(1)
 		.peek(d-> {
 		   sneakyThrow(new IOException(exceptionMessage));
 		}));
 	
    }   
    
    
    @Test
    public void testIntStreamClosesOnWrappedIoException() throws Exception {
 	
	final String exceptionMessage = "IntStream peek threw";
 	
 	assertClosesOnWrappedIoException(exceptionMessage,IntStream.of(1)
 		.peek(d-> {
 		   throw new WrappedIOException(new IOException(exceptionMessage));
 		}));

    }
    
    @Test
    public void testDoubleStreamCloses() throws Exception {
	
	assertClosesOnSuccess( DoubleStream.of(1.0d));
	  
      }
    
    @Test
    public void testDoubleStreamClosesOnRuntimeException() throws Exception {
	
	String exceptionMessage = "DoubleStream peek threw";
	
 	assertClosesOnRuntimeException(exceptionMessage,DoubleStream.of(1.1)
		.peek(d-> {
		    throw new RuntimeException(exceptionMessage);
		}));
	
    }
    
    @Test
    public void testDoubleStreamClosesOnSneakyIOException() throws Exception {
 	
 	String exceptionMessage = "DoubleStream peek threw";
 	
 	assertClosesOnIoException(exceptionMessage,DoubleStream.of(1.1)
 		.peek(d-> {
 		   sneakyThrow(new IOException(exceptionMessage));
 		}));
 	
    }   
    
    @Test
    public void testDoubleStreamClosesOnWrappedIoException() throws Exception {
 	
	final String exceptionMessage = "DoubleStream peek threw";
 	
 	assertClosesOnWrappedIoException(exceptionMessage,DoubleStream.of(1.1d)
 		.peek(d-> {
 		   throw new WrappedIOException(new IOException(exceptionMessage));
 		}));

    }

    
    private <T,S extends BaseStream<T,S>> void assertClosesOnSuccess(S baseStream) throws IOException{
	
	assertFalse(closed.get());
   	
	roundTrip(baseStream.onClose(()-> closed.set(true)));
   	
   	assertTrue(closed.get());
    } 
    
    private <T,S extends BaseStream<T,S>> void assertClosesOnRuntimeException(String exceptionMessage, S baseStream) throws IOException{
        
	assertFalse(closed.get());
 	
 	initExpectedException(RuntimeException.class,exceptionMessage);
	
	roundTrip(baseStream.onClose(()-> closed.set(true)));
    	
    	assertTrue(closed.get());
     }
    
    private <T,S extends BaseStream<T,S>> void assertClosesOnIoException(String exceptionMessage, S baseStream) throws IOException{
        
 	assertFalse(closed.get());
  	
 	initExpectedExceptionIoException(exceptionMessage);
 	
 	roundTrip(baseStream.onClose(()-> closed.set(true)));
     	
     	assertTrue(closed.get());
      } 
    
    private <T,S extends BaseStream<T,S>> void assertClosesOnWrappedIoException(String exceptionMessage, S baseStream) throws IOException{
        
	
	final String actualMessage = "Unexpected IOException (of type java.io.IOException): "+exceptionMessage;
	
 	assertFalse(closed.get());
  	
 	initExpectedExceptionIoException(actualMessage);
 	
 	roundTrip(baseStream.onClose(()-> closed.set(true)));
     	
     	assertTrue(closed.get());
      } 

    
    @SuppressWarnings("unchecked")
    private <T,S extends BaseStream<T,S>> void roundTrip(S stream) throws IOException {
	
	 if (stream instanceof LongStream) {
	     roundTrip((LongStream)stream);
	     return;
	 }
	 else if (stream instanceof IntStream) {
	     roundTrip((IntStream)stream);
	     return;
	 }
	 else if  (stream instanceof DoubleStream) {
	     roundTrip((DoubleStream)stream);
	     return;
	 }
	 else if (Stream.class.isInstance(stream)) {
	     roundTrip((Stream<T>)stream);
	     return;
	 }
	 
	 throw new IllegalArgumentException(stream.getClass().getName()+" is unknown");
    }

    private void initExpectedExceptionIoException(final String exceptionMessage) {
	this.expectedException.expect(new IsClosedMatcher());
	this.expectedException.expect(Is.isA(IOException.class));
	this.expectedException.expectMessage(exceptionMessage);
    }
    
    
    private void initExpectedException(Class<? extends Throwable> cause, final String exceptionMessage) {
	this.expectedException.expect(AllOf.allOf(Is.isA(JsonMappingException.class),new IsClosedMatcher()));
	this.expectedException.expect(Is.isA(JsonMappingException.class));
	this.expectedException.expectCause(Is.isA(cause));
	this.expectedException.expectMessage(exceptionMessage);
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
    
    
    class IsClosedMatcher extends CustomMatcher<Object> {

        public IsClosedMatcher() {
            super("Check flag closed");
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            description.appendText("was ")
                .appendValue(item);
        }

        @Override
        public boolean matches(Object item) {
            
            return StreamCloseTest.this.closed.get();
        }

    }
}
