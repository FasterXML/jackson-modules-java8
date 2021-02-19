package com.fasterxml.jackson.module.paramnames;

import java.util.*;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonKey;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import static org.junit.Assert.assertEquals;

// [modules-java8#206]
public class MapKeyNames206Test
    extends ModuleTestBase
{
    @JsonPropertyOrder({"id", "name"})
    static class Team {
        @JsonKey
        final String id;

        final String name;

        public Team(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "[id="+id+",name="+name+"]";
        }
    }

    private final static Team TEAM = new Team("a", "Team A");
    private final static String EXP_MAP_JSON = a2q(
            "{'a':{'id':'a','name':'Team A'}}");

    @Test
    public void testMapKeysVanilla() throws Exception
    {
        final ObjectMapper mapper = JsonMapper.builder().build();
        final Map<Team, Team> TEAM_MAP = Collections.singletonMap(TEAM, TEAM);
        assertEquals(EXP_MAP_JSON, mapper.writeValueAsString(TEAM_MAP));
    }
    
    @Test
    public void testMapKeysParamNames() throws Exception
    {
        final ObjectMapper mapper = newMapper();
        final Map<Team, Team> TEAM_MAP = Collections.singletonMap(TEAM, TEAM);
        assertEquals(EXP_MAP_JSON, mapper.writeValueAsString(TEAM_MAP));
    }
}
