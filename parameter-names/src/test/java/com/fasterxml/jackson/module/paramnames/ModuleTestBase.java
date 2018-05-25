package com.fasterxml.jackson.module.paramnames;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ModuleTestBase
{
    protected static ObjectMapper newMapper() {
        return new ObjectMapper()
                .registerModule(new ParameterNamesModule());
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
}
