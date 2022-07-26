package tools.jackson.datatype.jsr310.key;

import java.time.YearMonth;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Assert;
import org.junit.Test;

public class YearMonthAsKeyTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(new TypeReference<Map<YearMonth, String>>() {
    });

    @Test
    public void testSerialization() throws Exception {
        Assert.assertEquals("Value is incorrect", mapAsString("3141-05", "test"),
                MAPPER.writeValueAsString(asMap(YearMonth.of(3141, 5), "test")));
    }

    @Test
    public void testDeserialization() throws Exception {
        Assert.assertEquals("Value is incorrect", asMap(YearMonth.of(3141, 5), "test"),
                READER.readValue(mapAsString("3141-05", "test")));
    }
}
