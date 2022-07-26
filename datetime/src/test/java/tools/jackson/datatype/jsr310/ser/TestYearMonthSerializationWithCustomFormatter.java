package tools.jackson.datatype.jsr310.ser;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.jsr310.deser.YearMonthDeserializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

@RunWith(Parameterized.class)
public class TestYearMonthSerializationWithCustomFormatter {
    private final DateTimeFormatter formatter;

    public TestYearMonthSerializationWithCustomFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Test
    public void testSerialization() throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertThat(serializeWith(dateTime, formatter), containsString(dateTime.format(formatter)));
    }

    private String serializeWith(YearMonth dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new YearMonthSerializer(f)))
                .build();
        return mapper.writeValueAsString(dateTime);
    }

    @Test
    public void testDeserialization() throws Exception {
        YearMonth dateTime = YearMonth.now();
        assertThat(deserializeWith(dateTime.format(formatter), formatter), equalTo(dateTime));
    }

    private YearMonth deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(YearMonth.class, new YearMonthDeserializer(f)))
                .build();
        return mapper.readValue("\"" + json + "\"", YearMonth.class);
    }

    @Parameters
    public static Collection<Object[]> customFormatters() {
        Collection<Object[]> formatters = new ArrayList<>();
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("uuuu-MM")});
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("uu-M")});
        return formatters;
    }
}
