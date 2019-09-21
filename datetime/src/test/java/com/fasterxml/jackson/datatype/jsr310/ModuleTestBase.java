package com.fasterxml.jackson.datatype.jsr310;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public class ModuleTestBase
{
    public static class NoCheckSubTypeValidator
        extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(MapperConfig<?> config, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    // 14-Mar-2016, tatu: Serialization of trailing zeroes may change [datatype-jsr310#67]
    //   Note, tho, that "0.0" itself is special case; need to avoid scientific notation:
    final protected static String NO_NANOSECS_SER = "0.0";
    final protected static String NO_NANOSECS_SUFFIX = ".000000000";

    protected static ObjectMapper newMapper() {
        return mapperBuilder().build();
    }

    protected static JsonMapper.Builder mapperBuilder() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule());
    }
    
    protected String quote(String value) {
        return "\"" + value + "\"";
    }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }

    protected void verifyException(Throwable e, String... matches)
    {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        throw new Error("Expected an exception with one of substrings ("+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }

    protected static <T> Map<T, String> asMap(T key, String value) {
        return Collections.singletonMap(key, value);
    }

    protected static String mapAsString(String key, String value) {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }
}
