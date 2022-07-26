package tools.jackson.datatype.jsr310.key;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Map;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import org.junit.Test;

public class LocalDateAsKeyTest extends ModuleTestBase
{
    private static final LocalDate DATE = LocalDate.of(2015, 3, 14);
    private static final String DATE_STRING = "2015-03-14";

    private final ObjectMapper MAPPER = newMapper();
    private final ObjectReader READER = MAPPER.readerFor(new TypeReference<Map<LocalDate, String>>() { });

    @Test
    public void testSerialization() throws Exception {
        assertEquals("Incorrect value", mapAsString(DATE_STRING, "test"),
                MAPPER.writeValueAsString(asMap(DATE, "test")));
    }

    @Test
    public void testDeserialization() throws Exception {
        assertEquals("Incorrect value", asMap(DATE, "test"),
                READER.readValue(mapAsString(DATE_STRING, "test")));
    }
}
