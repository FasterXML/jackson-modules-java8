package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.introspect.AnnotatedConstructor;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.MalformedParametersException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

/**
 * @author Lovro Pandzic
 */
@RunWith(MockitoJUnitRunner.class)
public class ParameterNamesAnnotationIntrospectorTest {

    @Mock
    private ParameterExtractor parameterExtractor;

    private ParameterNamesAnnotationIntrospector introspector;

    @Before
    public void setUp() throws Exception {
        introspector = new ParameterNamesAnnotationIntrospector(JsonCreator.Mode.DEFAULT, parameterExtractor);
    }

    @Test
    public void shouldFindParameterNameFromConstructorForLegalIndex() throws Exception {

        // given
        Constructor<?> givenConstructor = ImmutableBean.class.getConstructor(String.class, Integer.class);
        Parameter[] givenParameters = givenConstructor.getParameters();
        AnnotatedConstructor owner = new AnnotatedConstructor(null, givenConstructor, null, null);
        AnnotatedParameter annotatedParameter = new AnnotatedParameter(owner, null, null, 0);
        given(parameterExtractor.getParameters(any())).willReturn(givenParameters);

        // when
        String actual = introspector.findImplicitPropertyName(annotatedParameter);

        then(actual).isEqualTo("name");
        BDDMockito.then(parameterExtractor).should().getParameters(givenConstructor);
    }

    @Test
    public void shouldFindParameterNameFromMethodForLegalIndex() throws Exception {

        // given
        Method givenMethod = ImmutableBeanWithStaticFactory.class.getMethod("of", String.class, Integer.class);
        Parameter[] givenParameters = givenMethod.getParameters();
        AnnotatedMethod owner = new AnnotatedMethod(null, givenMethod, null, null);
        AnnotatedParameter annotatedParameter = new AnnotatedParameter(owner, null, null, 0);
        given(parameterExtractor.getParameters(any())).willReturn(givenParameters);

        // when
        String actual = introspector.findImplicitPropertyName(annotatedParameter);

        // then
        then(actual).isEqualTo("name");
        BDDMockito.then(parameterExtractor).should().getParameters(givenMethod);
    }

    @Test
    public void shouldReturnNullForMalformedParametersException() throws Exception {

        // given
        Constructor<?> givenConstructor = ImmutableBean.class.getConstructor(String.class, Integer.class);
        AnnotatedConstructor owner = new AnnotatedConstructor(null, givenConstructor, null, null);
        AnnotatedParameter annotatedParameter = new AnnotatedParameter(owner, null, null, 0);
        given(parameterExtractor.getParameters(any())).willThrow(new MalformedParametersException());

        // when
        String actual = introspector.findImplicitPropertyName(annotatedParameter);

        then(actual).isNull();
        BDDMockito.then(parameterExtractor).should().getParameters(givenConstructor);
    }
}
