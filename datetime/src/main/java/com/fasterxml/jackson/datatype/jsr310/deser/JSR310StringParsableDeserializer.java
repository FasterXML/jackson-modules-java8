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
import java.time.Period;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

/**
 * Deserializer for all Java 8 temporal {@link java.time} types that cannot be represented
 * with numbers and that have parse functions that can take {@link String}s,
 * and where format is not configurable.
 *
 * @author Nick Williams
 * @author Tatu Saloranta
 *
 * @since 2.2
 */
public class JSR310StringParsableDeserializer
    extends JSR310DeserializerBase<Object>
        implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    protected final static int TYPE_PERIOD = 1;
    protected final static int TYPE_ZONE_ID = 2;
    protected final static int TYPE_ZONE_OFFSET = 3;

    public static final JsonDeserializer<Period> PERIOD =
        createDeserializer(Period.class, TYPE_PERIOD);

    public static final JsonDeserializer<ZoneId> ZONE_ID =
        createDeserializer(ZoneId.class, TYPE_ZONE_ID);

    public static final JsonDeserializer<ZoneOffset> ZONE_OFFSET =
        createDeserializer(ZoneOffset.class, TYPE_ZONE_OFFSET);

    protected final int _typeSelector;

    @SuppressWarnings("unchecked")
    protected JSR310StringParsableDeserializer(Class<?> supportedType, int typeSelector)
    {
        super((Class<Object>)supportedType);
        _typeSelector = typeSelector;
    }

    /**
     * Since 2.11
     */
    protected JSR310StringParsableDeserializer(JSR310StringParsableDeserializer base, Boolean leniency) {
        super(base, leniency);
        _typeSelector = base._typeSelector;
    }

    @SuppressWarnings("unchecked")
    protected static <T> JsonDeserializer<T> createDeserializer(Class<T> type, int typeId) {
        return (JsonDeserializer<T>) new JSR310StringParsableDeserializer(type, typeId);
    }

    @Override
    protected JSR310StringParsableDeserializer withLeniency(Boolean leniency) {
        if (_isLenient == !Boolean.FALSE.equals(leniency)) {
            return this;
        }
        // TODO: or should this be casting as above in createDeserializer? But then in createContext, we need to
        // call the withLeniency method in this class. (See if we can follow InstantDeser convention here?)
        return new JSR310StringParsableDeserializer(this, leniency);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        JSR310StringParsableDeserializer deser = this;
        if (format != null) {
            if (format.hasLenient()) {
                Boolean leniency = format.getLenient();
                if (leniency != null) {
                    deser = this.withLeniency(leniency);
                }
            }
        }
        return deser;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return _fromString(p, ctxt, p.getText());
        }
        // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
        if (p.isExpectedStartObjectToken()) {
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        }
        if (p.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            // 20-Apr-2016, tatu: Related to [databind#1208], can try supporting embedded
            //    values quite easily
            return p.getEmbeddedObject();
        }
        if (p.isExpectedStartArrayToken()) {
            return _deserializeFromArray(p, ctxt);
        }
        
        throw ctxt.wrongTokenException(p, handledType(), JsonToken.VALUE_STRING, null);
    }

    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context,
            TypeDeserializer deserializer)
        throws IOException
    {
        // This is a nasty kludge right here, working around issues like
        // [datatype-jsr310#24]. But should work better than not having the work-around.
        JsonToken t = parser.getCurrentToken();
        if ((t != null) && t.isScalarValue()) {
            return deserialize(parser, context);
        }
        return deserializer.deserializeTypedFromAny(parser, context);
    }

    protected Object _fromString(JsonParser p, DeserializationContext ctxt,
            String string)  throws IOException
    {
        string = string.trim();
        if (string.length() == 0) {
            CoercionAction act = ctxt.findCoercionAction(logicalType(), _valueClass,
                    CoercionInputShape.EmptyString);
            if (act == CoercionAction.Fail) {
                ctxt.reportInputMismatch(this,
"Cannot coerce empty String (\"\") to %s (but could if enabling coercion using `CoercionConfig`)",
_coercedTypeDesc());
            }
            // 21-Jun-2020, tatu: As of 2.12, leniency considered legacy setting,
            //    but still supported.
            if (!isLenient()) {
                return _failForNotLenient(p, ctxt, JsonToken.VALUE_STRING);
            }
            if (act == CoercionAction.AsEmpty) {
                return getEmptyValue(ctxt);
            }
            // None of the types has specific null value
            return null;
        }
        try {
            switch (_typeSelector) {
            case TYPE_PERIOD:
                return Period.parse(string);
            case TYPE_ZONE_ID:
                return ZoneId.of(string);
            case TYPE_ZONE_OFFSET:
                return ZoneOffset.of(string);
            }
        } catch (DateTimeException e) {
            return _handleDateTimeException(ctxt, e, string);
        }
        VersionUtil.throwInternal();
        return null;
    }
}
