package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Using this annotation to prevent {@link ParameterNamesAnnotationIntrospector} parse parameter name.
 * When it's on method parameter, only annotated parameter ignored. All parameters will be ignored when it's on a method.
 *
 * @author Brozen Lau
 */
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface JsonParamNameIgnore {

    boolean value() default true;

}
