/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

/**
 * Base class that indicates that all JSR310 datatypes are deserialized from scalar JSON types.
 *
 * @author Nick Williams
 * @since 2.2
 */
abstract class JSR310DeserializerBase<T> extends StdScalarDeserializer<T>
{
    /**
     * Flag that indicates what leniency setting is enabled for this deserializer (either
     * due {@link JsonFormat} annotation on property or class, or due to per-type
     * "config override", or from global settings): leniency/strictness has effect
     * on accepting some non-default input value representations (such as integer values
     * for dates).
     *<p>
     * Note that global default setting is for leniency to be enabled, for Jackson 2.x,
     * and has to be explicitly change to force strict handling: this is to keep backwards
     * compatibility with earlier versions.
     */
    protected final boolean _isLenient;

    protected JSR310DeserializerBase(Class<T> supportedType) {
        super(supportedType);
        _isLenient = true;
    }

    protected JSR310DeserializerBase(Class<T> supportedType,
                                     Boolean leniency) {
        super(supportedType);
        _isLenient = !Boolean.FALSE.equals(leniency);
    }

    protected JSR310DeserializerBase(JSR310DeserializerBase<T> base) {
        super(base);
        _isLenient = base._isLenient;
    }

    protected JSR310DeserializerBase(JSR310DeserializerBase<T> base, Boolean leniency) {
        super(base);
        _isLenient = !Boolean.FALSE.equals(leniency);
    }

    protected abstract JSR310DeserializerBase<T> withLeniency(Boolean leniency);

    /**
     * @return {@code true} if lenient handling is enabled; {code false} if not (strict mode)
     */
    protected boolean isLenient() {
        return _isLenient;
    }
    
    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context,
            TypeDeserializer typeDeserializer)
        throws IOException
    {
        return typeDeserializer.deserializeTypedFromAny(parser, context);
    }

    protected <BOGUS> BOGUS _reportWrongToken(DeserializationContext context,
            JsonToken exp, String unit) throws IOException
    {
        context.reportWrongTokenException((JsonDeserializer<?>)this, exp,
                "Expected %s for '%s' of %s value",
                        exp.name(), unit, ClassUtil.getClassDescription(handledType()));
        return null;
    }

    protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context,
            JsonToken... expTypes) throws IOException
    {
        // 20-Apr-2016, tatu: No multiple-expected-types handler yet, construct message here
        return context.reportInputMismatch(handledType(),
                "Unexpected token (%s), expected one of %s for %s value",
                parser.currentToken(),
                Arrays.asList(expTypes).toString(),
                ClassUtil.getClassDescription(handledType()));
    }

    @SuppressWarnings("unchecked")
    protected <R> R _handleDateTimeException(DeserializationContext context,
              DateTimeException e0, String value) throws JsonMappingException
    {
        try {
            return (R) context.handleWeirdStringValue(handledType(), value,
                    "Failed to deserialize %s: (%s) %s",
                    ClassUtil.getClassDescription(handledType()), e0.getClass().getName(), e0.getMessage());
        } catch (JsonMappingException e) {
            e.initCause(e0);
            throw e;
        } catch (IOException e) {
            if (null == e.getCause()) {
                e.initCause(e0);
            }
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }

    // @since 3.0
    @SuppressWarnings("unchecked")
    protected <R> R _handleDateTimeFormatException(DeserializationContext context,
              DateTimeException e0, DateTimeFormatter format, String value) throws JsonMappingException
    {
        final String formatterDesc = (format == null) ? "[default format]" : format.toString();
        try {
            return (R) context.handleWeirdStringValue(handledType(), value,
                    "Failed to deserialize %s (with format '%s'): (%s) %s",
                    ClassUtil.getClassDescription(handledType()),
                    formatterDesc, e0.getClass().getName(), e0.getMessage());
        } catch (JsonMappingException e) {
            e.initCause(e0);
            throw e;
        } catch (IOException e) {
            if (null == e.getCause()) {
                e.initCause(e0);
            }
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    protected <R> R _handleUnexpectedToken(DeserializationContext ctxt,
              JsonParser parser, String message, Object... args) throws JsonMappingException {
        try {
            return (R) ctxt.handleUnexpectedToken(getValueType(ctxt), parser.currentToken(),
                    parser, message, args);
        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            throw JsonMappingException.fromUnexpectedIOE(e);
        }
    }

    protected <R> R _handleUnexpectedToken(DeserializationContext context,
              JsonParser parser, JsonToken... expTypes) throws JsonMappingException {
        return _handleUnexpectedToken(context, parser,
                "Unexpected token (%s), expected one of %s for %s value",
                parser.currentToken(),
                Arrays.asList(expTypes),
                ClassUtil.getClassDescription(handledType()));
    }

    @SuppressWarnings("unchecked")
    protected T _failForNotLenient(JsonParser p, DeserializationContext ctxt,
                                   JsonToken expToken) throws IOException
    {
        return (T) ctxt.handleUnexpectedToken(getValueType(ctxt), expToken, p,
                "Cannot deserialize instance of %s out of %s token: not allowed because 'strict' mode set for property or type (enable 'lenient' handling to allow)",
                ClassUtil.nameOf(handledType()), p.currentToken());
    }

    /*
    public Object handleUnexpectedToken(Class<?> instClass, JsonToken t,
            JsonParser p, String msg, Object... msgArgs)
     */
    
    /**
     * Helper method used to peel off spurious wrappings of DateTimeException
     *
     * @param e DateTimeException to peel
     *
     * @return DateTimeException that does not have another DateTimeException as its cause.
     */
    protected DateTimeException _peelDTE(DateTimeException e) {
        while (true) {
            Throwable t = e.getCause();
            if (t != null && t instanceof DateTimeException) {
                e = (DateTimeException) t;
                continue;
            }
            break;
        }
        return e;
    }
}
