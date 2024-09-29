package tools.jackson.datatype.jsr310.misc;

import java.time.DateTimeException;

import org.junit.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.Assert.assertEquals;

public class DateTimeExceptionTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = newMapper();

    // [modules-java#319]: should not fail to ser/deser DateTimeException
    @Test
    public void testDateTimeExceptionRoundtrip() throws Exception
    {
        String json = MAPPER.writeValueAsString(new DateTimeException("Test!"));
        DateTimeException result = MAPPER.readValue(json, DateTimeException.class);
        assertEquals("Test!", result.getMessage());
    }
}
