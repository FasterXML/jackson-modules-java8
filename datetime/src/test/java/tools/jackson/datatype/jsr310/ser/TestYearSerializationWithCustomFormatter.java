package tools.jackson.datatype.jsr310.ser;

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.jsr310.deser.YearDeserializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(Parameterized.class)
public class TestYearSerializationWithCustomFormatter {
    private final DateTimeFormatter formatter;

    public TestYearSerializationWithCustomFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Test
    public void testSerialization() throws Exception {
        Year year = Year.now();
        String expected = "\"" + year.format(formatter) + "\"";
        assertThat(serializeWith(year, formatter), equalTo(expected));
    }

    private String serializeWith(Year dateTime, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new YearSerializer(f)))
                .build();
        return mapper.writeValueAsString(dateTime);
    }

    @Test
    public void testDeserialization() throws Exception {
        Year dateTime = Year.now();
        assertThat(deserializeWith(dateTime.format(formatter), formatter), equalTo(dateTime));
    }

    private Year deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(Year.class, new YearDeserializer(f)))
                .build();
        return mapper.readValue("\"" + json + "\"", Year.class);
    }

    @Parameters
    public static Collection<Object[]> customFormatters() {
        Collection<Object[]> formatters = new ArrayList<>();
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("yyyy")});
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("yy")});
        return formatters;
    }
}
