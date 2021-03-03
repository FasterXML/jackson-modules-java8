package com.fasterxml.jackson.module.paramnames;

import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Parameter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.*;

/**
 * Introspector that uses parameter name information provided by the Java Reflection API additions in Java 8 to
 * determine the parameter name for methods and constructors.
 *
 * @author Lovro Pandzic
 * @see AnnotationIntrospector
 * @see Parameter
 */
public class ParameterNamesAnnotationIntrospector extends NopAnnotationIntrospector {
    private static final long serialVersionUID = 1L;

    private final JsonCreator.Mode creatorBinding;
    private final ParameterExtractor parameterExtractor;

    ParameterNamesAnnotationIntrospector(JsonCreator.Mode creatorBinding, ParameterExtractor parameterExtractor)
    {
        this.creatorBinding = creatorBinding;
        this.parameterExtractor = parameterExtractor;
    }

    @Override
    public String findImplicitPropertyName(AnnotatedMember m) {
        if (m instanceof AnnotatedParameter) {
            return findParameterName((AnnotatedParameter) m);
        }
        return null;
    }

    private String findParameterName(AnnotatedParameter annotatedParameter) {

        Parameter[] params;
        try {
            params = getParameters(annotatedParameter.getOwner());
        } catch (MalformedParametersException e) {
            return null;
        }

        Parameter p = params[annotatedParameter.getIndex()];
        return p.isNamePresent() ? p.getName() : null;
    }

    private Parameter[] getParameters(AnnotatedWithParams owner) {
        if (owner instanceof AnnotatedConstructor) {
            return parameterExtractor.getParameters(((AnnotatedConstructor) owner).getAnnotated());
        }
        if (owner instanceof AnnotatedMethod) {
            return parameterExtractor.getParameters(((AnnotatedMethod) owner).getAnnotated());
        }

        return null;
    }

    /*
    /**********************************************************
    /* Creator information handling
    /**********************************************************
     */

    @Override
    public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
        JsonCreator ann = _findAnnotation(a, JsonCreator.class);
        if (ann != null) {
            JsonCreator.Mode mode = ann.mode();
            // but keep in mind that there may be explicit default for this module
            if ((creatorBinding != null)
                    && (mode == JsonCreator.Mode.DEFAULT)) {
                mode = creatorBinding;
            }
            return mode;
        }
        return null;
    }
}
