package tools.jackson.datatype.jsr310.failing;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.Assert.assertEquals;

public class OffsetDateTimeDeser279Test extends ModuleTestBase
{
    // For [modules-java8#279]
    static class Wrapper279 {
        OffsetDateTime date;

        public Wrapper279(OffsetDateTime d) { date = d; }
        protected Wrapper279() { }

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        public OffsetDateTime getDate() {
            return date;
        }
        public void setDate(OffsetDateTime date) {
            this.date = date;
        }
    }

    private ObjectMapper MAPPER = newMapper();

    // For [modules-java8#279]
    @Test
    public void testWrapperWithPattern279() throws Exception
    {
        final OffsetDateTime date = OffsetDateTime.now(ZoneId.of("UTC"))
                .truncatedTo(ChronoUnit.SECONDS);
        final Wrapper279 input = new Wrapper279(date);
        final String json = MAPPER.writeValueAsString(input);

        Wrapper279 result = MAPPER.readValue(json, Wrapper279.class);
        assertEquals(input.date, result.date);
    }
}