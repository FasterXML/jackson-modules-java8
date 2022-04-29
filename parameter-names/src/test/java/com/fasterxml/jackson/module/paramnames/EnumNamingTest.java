package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EnumNamingTest extends ModuleTestBase
{
    enum SurprisingEnum32 {
        @JsonProperty("customValue")
        ENUM_NAME;
    }

    // [modules-java8#234]
    static enum MeetingStatus {
        UNKNOWN(0),
        INITIALIZING(1),
        PROGRESSING(2),
        TERMINATED(3),
        ;

        public final int status;

        @JsonValue
        public int getStatus() {
            return status;
        }

        MeetingStatus(int status) {
            this.status = status;
        }

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public static MeetingStatus parse(Integer status) {
            if (status == null) {
                return UNKNOWN;
            }
            int s = status.intValue();
            for (MeetingStatus value : values()) {
                if (s == value.status) {
                    return value;
                }
            }
            return UNKNOWN;
        }

    }

    static class Meeting {
        public MeetingStatus status;
    }

    private final ObjectMapper MAPPER = newMapper();

    @Test
    public void testCustomEnumName() throws Exception
    {
        final String EXP = "\"customValue\"";
        
        // First, verify default handling

        String json = MAPPER
            .writeValueAsString(SurprisingEnum32.ENUM_NAME);
        assertEquals(EXP, json);

        // and then with parameter names module
        final ObjectMapper mapperWithNames = new ObjectMapper()
            .registerModule(new ParameterNamesModule());
        json = mapperWithNames.writeValueAsString(SurprisingEnum32.ENUM_NAME);
        assertEquals(EXP, json);

        // plus read back:
        SurprisingEnum32 value = mapperWithNames.readValue(json, SurprisingEnum32.class);
        assertEquals(SurprisingEnum32.ENUM_NAME, value);
    }

    // [modules-java8#234]
    @Test
    public void testEnumWithCreator() throws Exception
    {
        // it's ok when serializing
        Meeting meetingSrc = new Meeting();
        meetingSrc.status = MeetingStatus.PROGRESSING;

        String json = MAPPER.writeValueAsString(meetingSrc);

        // but throws exception when deserializing
        Meeting result = MAPPER.readValue(json, Meeting.class);
        assertEquals(MeetingStatus.PROGRESSING, result.status);
    }
}
