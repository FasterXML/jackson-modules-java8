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
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

/**
 * Base class that indicates that all JSR310 datatypes are deserialized from scalar JSON types.
 *
 * @author Nick Williams
 * @since 2.2
 */
abstract class JSR310DeserializerBase<T> extends StdScalarDeserializer<T>
{
    private static final long serialVersionUID = 1L;
    
    protected JSR310DeserializerBase(Class<T> supportedType)
    {
        super(supportedType);
    }

    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context,
            TypeDeserializer typeDeserializer)
        throws IOException
    {
        return typeDeserializer.deserializeTypedFromAny(parser, context);
    }

    protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context,
            JsonToken exp, String unit) throws IOException
    {
        throw context.wrongTokenException(parser, exp,
                String.format("Expected %s for '%s' of %s value",
                        exp.name(), unit, handledType().getName()));
    }

    protected <BOGUS> BOGUS _reportWrongToken(JsonParser parser, DeserializationContext context,
            JsonToken... expTypes) throws IOException
    {
        // 20-Apr-2016, tatu: No multiple-expected-types handler yet, construct message
        //    here
        String msg = String.format("Unexpected token (%s), expected one of %s for %s value",
                parser.getCurrentToken(),
                Arrays.asList(expTypes).toString(),
                handledType().getName());
        throw JsonMappingException.from(parser, msg);
    }
    
    protected <BOGUS> BOGUS _rethrowDateTimeException(JsonParser p, DeserializationContext context,
            DateTimeException e0, String value) throws JsonMappingException
    {
        JsonMappingException e;
        if (e0 instanceof DateTimeParseException) {
            e = context.weirdStringException(value, handledType(), e0.getMessage());
            e.initCause(e0);
        } else {
            e = JsonMappingException.from(p,
                String.format("Failed to deserialize %s: (%s) %s",
                        handledType().getName(), e0.getClass().getName(), e0.getMessage()), e0);
        }
        throw e;
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
