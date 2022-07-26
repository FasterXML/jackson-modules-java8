package tools.jackson.datatype.jsr310.misc;

import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

import org.junit.Assert;
import org.junit.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.jsr310.ModuleTestBase;

public class UnsupportedTypesTest extends ModuleTestBase
{
    // [modules-java8#207]
    static class TAWrapper {
        public TemporalAdjuster a;

        public TAWrapper(TemporalAdjuster a) {
            this.a = a;
        }
    }

    // [modules-java#207]: should not fail on `TemporalAdjuster`
    @Test
    public void testTemporalAdjusterSerialization() throws Exception
    {
        ObjectMapper mapper = newMapper();

        // Not 100% sure how this happens, actually; should fail on empty "POJO"?
        Assert.assertEquals(a2q("{'a':{}}"),
                mapper.writeValueAsString(new TAWrapper(TemporalAdjusters.firstDayOfMonth())));
    }
}
