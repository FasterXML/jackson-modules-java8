package tools.jackson.datatype.jsr310.key;

import java.time.Period;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class PeriodAsKeyTest extends ModuleTestBase
{
    private static final TypeReference<Map<Period, String>> TYPE_REF = new TypeReference<Map<Period, String>>() {
    };
    private static final Period PERIOD_0 = Period.of(0, 0, 0);
    private static final String PERIOD_0_STRING = "P0D";
    private static final Period PERIOD = Period.of(3, 1, 4);
    private static final String PERIOD_STRING = "P3Y1M4D";

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(PERIOD_0_STRING, "test"),
                MAPPER.writeValueAsString(asMap(PERIOD_0, "test")));
    }

    @Test
    public void testSerialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(PERIOD_STRING, "test"),
                MAPPER.writeValueAsString(asMap(PERIOD, "test")));
    }

    @Test
    public void testDeserialization0() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(PERIOD_0, "test"),
                READER.readValue(mapAsString(PERIOD_0_STRING, "test")));
    }

    @Test
    public void testDeserialization1() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(PERIOD, "test"),
                READER.readValue(mapAsString(PERIOD_STRING, "test")));
    }
}
