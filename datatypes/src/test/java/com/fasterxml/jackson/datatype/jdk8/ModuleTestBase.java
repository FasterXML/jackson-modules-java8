package com.fasterxml.jackson.datatype.jdk8;

import java.util.Arrays;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public abstract class ModuleTestBase extends junit.framework.TestCase
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

    /*
    /**********************************************************************
    /* Helper methods, setup
    /**********************************************************************
     */

    static ObjectMapper mapperWithModule() {
        return mapperBuilderWithModule().build();
    }

    static JsonMapper.Builder mapperBuilderWithModule() {
        return JsonMapper.builder()
                .addModule(new Jdk8Module());
    }

    @SuppressWarnings("deprecation")
    static ObjectMapper mapperWithModule(boolean absentsAsNulls) {
        ObjectMapper mapper = new ObjectMapper();
        Jdk8Module module = new Jdk8Module();
        module.configureAbsentsAsNulls(absentsAsNulls);
        mapper.registerModule(module);
        return mapper;
    }

    /*
    /**********************************************************************
    /* Helper methods, other
    /**********************************************************************
     */

    protected void verifyException(Throwable e, String... matches) {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        fail("Expected an exception with one of substrings (" + Arrays.asList(matches) + "): got one with message \""
                + msg + "\"");
    }

    protected String q(String str) {
        return '"' + str + '"';
    }

    protected String a2q(String json) {
        return json.replace("'", "\"");
    }
}
