package tools.jackson.datatype.jsr310.key;

import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class OffsetTimeAsKeyTest extends ModuleTestBase
{
    private static final TypeReference<Map<OffsetTime, String>> TYPE_REF = new TypeReference<Map<OffsetTime, String>>() {
    };
    private static final OffsetTime TIME_0 = OffsetTime.of(0, 0, 0, 0, ZoneOffset.UTC);
    private static final String TIME_0_STRING = "00:00Z";
    private static final OffsetTime TIME_1 = OffsetTime.of(3, 14, 15, 920 * 1000 * 1000, ZoneOffset.UTC);
    private static final String TIME_1_STRING = "03:14:15.920Z";
    private static final OffsetTime TIME_2 = OffsetTime.of(3, 14, 15, 920 * 1000 * 1000, ZoneOffset.ofHours(6));
    private static final String TIME_2_STRING = "03:14:15.920+06:00";

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization0() throws Exception {
        Assert.assertEquals(mapAsString(TIME_0_STRING, "test"),
                MAPPER.writeValueAsString(asMap(TIME_0, "test")));
    }

    @Test
    public void testSerialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(TIME_1_STRING, "test"),
                MAPPER.writeValueAsString(asMap(TIME_1, "test")));
    }

    @Test
    public void testSerialization2() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(TIME_2_STRING, "test"),
                MAPPER.writeValueAsString(asMap(TIME_2, "test")));
    }

    @Test
    public void testDeserialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(TIME_0, "test"),
                READER.readValue(mapAsString(TIME_0_STRING, "test")));
    }

    @Test
    public void testDeserialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(TIME_1, "test"),
                READER.readValue(mapAsString(TIME_1_STRING, "test")));
    }

    @Test
    public void testDeserialization2() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(TIME_2, "test"),
                READER.readValue(mapAsString(TIME_2_STRING, "test")));
    }
}
