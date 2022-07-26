package tools.jackson.datatype.jsr310.key;

import java.time.LocalTime;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class LocalTimeAsKeyTest extends ModuleTestBase
{
    private static final LocalTime TIME_0 = LocalTime.ofSecondOfDay(0);
    /*
     * Seconds are omitted if possible
     */
    private static final String TIME_0_STRING = "00:00";
    private static final LocalTime TIME = LocalTime.of(3, 14, 15, 920 * 1000 * 1000);
    private static final String TIME_STRING = "03:14:15.920";

    private static final TypeReference<Map<LocalTime, String>> TYPE_REF = new TypeReference<Map<LocalTime, String>>() {
    };
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(TIME_0_STRING, "test"),
                MAPPER.writeValueAsString(asMap(TIME_0, "test")));
    }

    @Test
    public void testSerialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(TIME_STRING, "test"),
                MAPPER.writeValueAsString(asMap(TIME, "test")));
    }

    @Test
    public void testDeserialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(TIME_0, "test"),
                READER.readValue(mapAsString(TIME_0_STRING, "test")));
    }

    @Test
    public void testDeserialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(TIME, "test"),
                READER.readValue(mapAsString(TIME_STRING, "test")));
    }
}
