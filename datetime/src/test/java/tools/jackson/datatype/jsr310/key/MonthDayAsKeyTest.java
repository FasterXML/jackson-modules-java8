package tools.jackson.datatype.jsr310.key;

import java.time.MonthDay;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class MonthDayAsKeyTest extends ModuleTestBase
{
    private static final MonthDay MONTH_DAY = MonthDay.of(3, 14);
    private static final String MONTH_DAY_STRING = "--03-14";

    private static final TypeReference<Map<MonthDay, String>> TYPE_REF = new TypeReference<Map<MonthDay, String>>() {
    };
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(TYPE_REF);

    @Test
    public void testSerialization() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString(MONTH_DAY_STRING, "test"),
                MAPPER.writeValueAsString(asMap(MONTH_DAY, "test")));
    }

    @Test
    public void testDeserialization() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(MONTH_DAY, "test"),
                READER.readValue(mapAsString(MONTH_DAY_STRING, "test")));
    }
}
