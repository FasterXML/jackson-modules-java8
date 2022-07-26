package tools.jackson.datatype.jsr310.ser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class TestLocalDateSerializationWithCustomFormatter {
    private final DateTimeFormatter formatter;

    public TestLocalDateSerializationWithCustomFormatter(DateTimeFormatter formatter) {
        this.formatter = formatter;
    }

    @Test
    public void testSerialization() throws Exception {
        LocalDate date = LocalDate.now();
        assertThat(serializeWith(date, formatter), containsString(date.format(formatter)));
    }

    private String serializeWith(LocalDate date, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addSerializer(new LocalDateSerializer(f)))
                .build();
        return mapper.writeValueAsString(date);
    }

    @Test
    public void testDeserialization() throws Exception {
        LocalDate date = LocalDate.now();
        assertThat(deserializeWith(date.format(formatter), formatter), equalTo(date));
    }

    private LocalDate deserializeWith(String json, DateTimeFormatter f) throws Exception {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new SimpleModule()
                        .addDeserializer(LocalDate.class, new LocalDateDeserializer(f)))
                .build();
        return mapper.readValue("\"" + json + "\"", LocalDate.class);
    }

    @Parameters
    public static Collection<Object[]> customFormatters() {
        Collection<Object[]> formatters = new ArrayList<>();
        formatters.add(new Object[]{DateTimeFormatter.BASIC_ISO_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_LOCAL_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_ORDINAL_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ISO_WEEK_DATE});
        formatters.add(new Object[]{DateTimeFormatter.ofPattern("MM/dd/yyyy")});
        return formatters;
    }
}
