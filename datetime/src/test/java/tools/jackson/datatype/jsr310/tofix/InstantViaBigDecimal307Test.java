package tools.jackson.datatype.jsr310.tofix;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import tools.jackson.datatype.jsr310.ModuleTestBase;
import tools.jackson.datatype.jsr310.testutil.failure.JacksonTestFailureExpected;

import static org.junit.jupiter.api.Assertions.*;

// [modules-java8#307]: Loss of precision via JsonNode for BigDecimal-valued
// things (like Instant)
public class InstantViaBigDecimal307Test extends ModuleTestBase
{
    static class Wrapper307 {
        public Instant value;

        public Wrapper307(Instant v) { value = v; }
        protected Wrapper307() { }
    }

    private final Instant ISSUED_AT = Instant.ofEpochSecond(1234567890).plusNanos(123456789);

    private ObjectMapper MAPPER = newMapper();

    @Test
    public void instantViaReadValue() throws Exception {
         String serialized = MAPPER.writeValueAsString(new Wrapper307(ISSUED_AT));
         Wrapper307 deserialized = MAPPER.readValue(serialized, Wrapper307.class);
         assertEquals(ISSUED_AT, deserialized.value);
    }

    @JacksonTestFailureExpected
    @Test
    public void instantViaReadTree() throws Exception {
        String serialized = MAPPER.writeValueAsString(new Wrapper307(ISSUED_AT));
        JsonNode tree = MAPPER.readTree(serialized);
        Wrapper307 deserialized = MAPPER.treeToValue(tree, Wrapper307.class);
        assertEquals(ISSUED_AT, deserialized.value);
    }
}
