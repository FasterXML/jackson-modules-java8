package com.fasterxml.jackson.module.paramnames;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;

class ParameterExtractor {

    public Parameter[] getParameters(Executable executable) {
        return executable.getParameters();
    }
}
