package com.fasterxml.jackson.datatype.jsr310;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestLocalDateTimeKeySerialization
    extends ModuleTestBase
{
    private static final TypeReference<Map<LocalDateTime, String>> TYPE_REF = new TypeReference<Map<LocalDateTime, String>>() {
    };
    private static final LocalDateTime DATE_TIME_0 = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
    /*
     * Current serializer is LocalDateTime.toString(), which omits seconds if it can
     */
    private static final String DATE_TIME_0_STRING = "1970-01-01T00:00";
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2015, 3, 14, 9, 26, 53, 590 * 1000 * 1000);
    private static final String DATE_TIME_STRING = "2015-03-14T09:26:53.590";

    private final ObjectMapper om = ObjectMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

    @Test
    public void testSerialization0() throws Exception {
        Map<LocalDateTime, String> map = new HashMap<>();
        map.put(DATE_TIME_0, "test");

        String value = om.writeValueAsString(map);

        assertEquals("Value is incorrect", map(DATE_TIME_0_STRING, "test"), value);
    }

    @Test
    public void testSerialization1() throws Exception {
        Map<LocalDateTime, String> map = new HashMap<>();
        map.put(DATE_TIME, "test");
        String value = om.writeValueAsString(map);
        assertEquals("Value is incorrect", map(DATE_TIME_STRING, "test"), value);
    }

    @Test
    public void testDeserialization0() throws Exception {
        Map<LocalDateTime, String> value = om.readValue(
                map(DATE_TIME_0_STRING, "test"),
                TYPE_REF);
        Map<LocalDateTime, String> map = new HashMap<>();
        map.put(DATE_TIME_0, "test");
        assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDeserialization1() throws Exception {
        Map<LocalDateTime, String> value = om.readValue(
                map(DATE_TIME_STRING, "test"),
                TYPE_REF);
        Map<LocalDateTime, String> map = new HashMap<>();
        map.put(DATE_TIME, "test");
        assertEquals("Value is incorrect", map, value);
    }

    @Test
    public void testDateTimeExceptionIsHandled() throws Throwable
    {
        LocalDateTime now = LocalDateTime.now();
        DeserializationProblemHandler handler = new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdKey(DeserializationContext ctxt, Class<?> targetType,
                   String valueToConvert, String failureMsg) throws IOException {
                if (LocalDateTime.class == targetType) {
                    if ("now".equals(valueToConvert)) {
                        return now;
                    }
                }
                return NOT_HANDLED;
            }
        };
        ObjectMapper mapper = newMapperBuilder()
                .addHandler(handler)
                .build();
        
        Map<LocalDateTime, String> value = mapper.readValue(
                map("now", "test"),
                TYPE_REF);
        Map<LocalDateTime, String> map = new HashMap<>();
        map.put(now, "test");
        assertEquals(map, value);
    }
 
    private String map(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }
}
