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
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;

/**
 * Base class that indicates that all JSR310 datatypes are deserialized from scalar JSON types.
 *
 * @author Nick Williams
 * @since 2.2
 */
abstract class JSR310DeserializerBase<T> extends StdScalarDeserializer<T>
{
    private static final long serialVersionUID = 1L;

    /**
     * Flag that indicates what leniency setting is enabled for this deserializer (either
     * due {@link com.fasterxml.jackson.annotation.JsonFormat.Shape} annotation on property or class, or due to per-type
     * "config override", or from global settings): leniency/strictness has effect
     * on accepting some non-default input value representations (such as integer values
     * for dates).
     *<p>
     * Note that global default setting is for leniency to be enabled, for Jackson 2.x,
     * and has to be explicitly change to force strict handling: this is to keep backwards
     * compatibility with earlier versions.
     *<p>
     * Note that with 2.12 and later coercion settings are moving to {@code CoercionConfig},
     * instead of simple yes/no leniency setting.
     *
     * @since 2.11
     */
    protected final boolean _isLenient;

    /**
     * @since 2.11
     */
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

    /**
     * @since 2.11
     */
    protected abstract JSR310DeserializerBase<T> withLeniency(Boolean leniency);

    /**
     * @return {@code true} if lenient handling is enabled; {code false} if not (strict mode)
     *
     * @since 2.11
     */
    protected boolean isLenient() {
        return _isLenient;
    }

    /**
     * Replacement for {@code isLenient()} for specific case of deserialization
     * from empty or blank String.
     *
     * @since 2.12
     */
    @SuppressWarnings("unchecked")
    protected T _fromEmptyString(JsonParser p, DeserializationContext ctxt,
            String str)
        throws IOException
    {
        final CoercionAction act = _checkFromStringCoercion(ctxt, str);
        switch (act) { // note: Fail handled above
        case AsEmpty:
            return (T) getEmptyValue(ctxt);
        case TryConvert:
        case AsNull:
        default:
        }
        // 22-Oct-2020, tatu: Although we should probably just accept this,
        //   for backwards compatibility let's for now allow override by
        //   "Strict" checks
        if (!_isLenient) {
            return _failForNotLenient(p, ctxt, JsonToken.VALUE_STRING);            
        }
        
        return null;
    }

    // Presumably all types here are Date/Time oriented ones?
    @Override
    public LogicalType logicalType() { return LogicalType.DateTime; }
    
    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context,
            TypeDeserializer typeDeserializer)
        throws IOException
    {
        return typeDeserializer.deserializeTypedFromAny(parser, context);
    }

    // @since 2.12
    protected boolean _isValidTimestampString(String str) {
        // 30-Sep-2020, tatu: Need to support "numbers as Strings" for data formats
        //    that only have String values for scalars (CSV, Properties, XML)
        // NOTE: we do allow negative values, but has to fit in 64-bits:
        return _isIntNumber(str) && NumberInput.inLongRange(str, (str.charAt(0) == '-'));
    }

    protected <BOGUS> BOGUS _reportWrongToken(DeserializationContext context,
            JsonToken exp, String unit) throws IOException
    {
        context.reportWrongTokenException((JsonDeserializer<?>)this, exp,
                "Expected %s for '%s' of %s value",
                        exp.name(), unit, handledType().getName());
        return null;
    }

    protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context,
            JsonToken... expTypes) throws IOException
    {
        // 20-Apr-2016, tatu: No multiple-expected-types handler yet, construct message
        //    here
        return context.reportInputMismatch(handledType(),
                "Unexpected token (%s), expected one of %s for %s value",
                parser.getCurrentToken(),
                Arrays.asList(expTypes).toString(),
                handledType().getName());
    }

    @SuppressWarnings("unchecked")
    protected <R> R _handleDateTimeException(DeserializationContext context,
              DateTimeException e0, String value) throws JsonMappingException
    {
        try {
            return (R) context.handleWeirdStringValue(handledType(), value,
                    "Failed to deserialize %s: (%s) %s",
                    handledType().getName(), e0.getClass().getName(), e0.getMessage());

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
    protected <R> R _handleUnexpectedToken(DeserializationContext context,
              JsonParser parser, String message, Object... args) throws JsonMappingException {
        try {
            return (R) context.handleUnexpectedToken(handledType(), parser.getCurrentToken(),
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
                handledType().getName());
    }

    @SuppressWarnings("unchecked")
    protected T _failForNotLenient(JsonParser p, DeserializationContext ctxt,
            JsonToken expToken) throws IOException
    {
        return (T) ctxt.handleUnexpectedToken(handledType(), expToken, p,
                "Cannot deserialize instance of %s out of %s token: not allowed because 'strict' mode set for property or type (enable 'lenient' handling to allow)",
                ClassUtil.nameOf(handledType()), p.currentToken());
    }

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
