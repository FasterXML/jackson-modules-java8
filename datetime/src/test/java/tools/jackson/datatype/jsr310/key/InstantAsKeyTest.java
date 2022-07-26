package tools.jackson.datatype.jsr310.key;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class InstantAsKeyTest extends ModuleTestBase
{
    private static final Instant INSTANT_0 = Instant.ofEpochMilli(0);
    private static final String INSTANT_0_STRING = "1970-01-01T00:00:00Z";
    private static final Instant INSTANT = Instant.ofEpochSecond(1426325213l, 590000000l);
    private static final String INSTANT_STRING = "2015-03-14T09:26:53.590Z";

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(new TypeReference<Map<Instant, String>>() { });

    @Test
    public void testSerialization0() throws Exception {
        String value = MAPPER.writeValueAsString(asMap(INSTANT_0, "test"));
        Assert.assertEquals("Value is incorrect", mapAsString(INSTANT_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        String value = MAPPER.writeValueAsString(asMap(INSTANT, "test"));
        assertEquals("Value is incorrect", mapAsString(INSTANT_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<Instant, String> value = READER.readValue(mapAsString(INSTANT_0_STRING, "test"));
        Map<Instant, String> EXP = asMap(INSTANT_0, "test");
        assertEquals("Value is incorrect", EXP, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<Instant, String> value = READER.readValue(mapAsString(INSTANT_STRING, "test"));
        Map<Instant, String> EXP = asMap(INSTANT, "test");
        assertEquals("Value is incorrect", EXP, value);
    }
}
