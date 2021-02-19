package com.fasterxml.jackson.module.paramnames;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class ModuleTestBase
{
    protected static ObjectMapper newMapper() {
        return new ObjectMapper()
                .registerModule(new ParameterNamesModule());
    }

    protected static JsonMapper.Builder mapperBuilder() {
        return JsonMapper.builder()
                .addModule(new ParameterNamesModule());
    }

    protected static String q(String value) {
        return "\"" + value + "\"";
    }

    protected static String a2q(String json) {
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
}
