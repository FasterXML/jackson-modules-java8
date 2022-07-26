package tools.jackson.datatype.jsr310.key;

import java.time.ZoneId;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class ZoneIdAsKeyTest extends ModuleTestBase
{
    private static final ZoneId ZONE_0 = ZoneId.of("UTC");
    private static final String ZONE_0_STRING = "UTC";
    private static final ZoneId ZONE_1 = ZoneId.of("+06:00");
    private static final String ZONE_1_STRING = "+06:00";
    private static final ZoneId ZONE_2 = ZoneId.of("Europe/London");
    private static final String ZONE_2_STRING = "Europe/London";

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(new TypeReference<Map<ZoneId, String>>() { });

    @Test
    public void testSerialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(ZONE_0_STRING, "test"),
                MAPPER.writeValueAsString(asMap(ZONE_0, "test")));
    }

    @Test
    public void testSerialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(ZONE_1_STRING, "test"),
                MAPPER.writeValueAsString(asMap(ZONE_1, "test")));
    }

    @Test
    public void testSerialization2() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(ZONE_2_STRING, "test"),
                MAPPER.writeValueAsString(asMap(ZONE_2, "test")));
    }

    @Test
    public void testDeserialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(ZONE_0, "test"),
                READER.readValue(mapAsString(ZONE_0_STRING, "test")));
    }

    @Test
    public void testDeserialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(ZONE_1, "test"),
                READER.readValue(mapAsString(ZONE_1_STRING, "test")));
    }

    @Test
    public void testDeserialization2() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(ZONE_2, "test"),
                READER.readValue(mapAsString(ZONE_2_STRING, "test")));
    }
}
