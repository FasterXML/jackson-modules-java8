package tools.jackson.datatype.jsr310.failing;

import java.time.Duration;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.*;
import tools.jackson.datatype.jsr310.ModuleTestBase;

import static org.junit.Assert.assertEquals;

public class DurationSer224Test extends ModuleTestBase
{
    // [datetime#224]
    static class MyDto224 {
      @JsonFormat(pattern = "MINUTES"
              // Work-around from issue:
//              , without = JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS
      )
      @JsonProperty("mins")
      private final Duration duration;
    
      public MyDto224(Duration d) { duration = d; }
    
      public Duration getDuration() { return duration; }
    }

    private final ObjectMapper MAPPER = newMapper();

    // [datetime#224]
    @Test
    public void testDurationFormatOverride() throws Exception
    {
        final String json = MAPPER.writeValueAsString(new MyDto224(Duration.ofHours(2)));
        assertEquals(a2q("{'mins':120}"), json);
    }
}
