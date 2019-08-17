package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

@SuppressWarnings("serial")
public abstract class JSR310DateTimeDeserializerBase<T>
    extends JSR310DeserializerBase<T>
    implements ContextualDeserializer
{
    protected final DateTimeFormatter _formatter;

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
     *
     * @since 2.10
     */
    protected final boolean _isLenient;

    protected JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f) {
        super(supportedType);
        _formatter = f;
        _isLenient = true;
    }

    /**
     * @since 2.10
     */
    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base,
            DateTimeFormatter f) {
        super(base);
        _formatter = f;
        _isLenient = base._isLenient;
    }
    
    /**
     * @since 2.10
     */
    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base,
            Boolean leniency) {
        super(base);
        _formatter = base._formatter;
        _isLenient = !Boolean.FALSE.equals(leniency);
    }

    protected abstract JSR310DateTimeDeserializerBase<T> withDateFormat(DateTimeFormatter dtf);

    /**
     * @since 2.10
     */
    protected abstract JSR310DateTimeDeserializerBase<T> withLeniency(Boolean leniency);

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        JSR310DateTimeDeserializerBase<?> deser = this;
        if (format != null) {
            if (format.hasPattern()) {
                final String pattern = format.getPattern();
                final Locale locale = format.hasLocale() ? format.getLocale() : ctxt.getLocale();
                DateTimeFormatter df;
                if (locale == null) {
                    df = DateTimeFormatter.ofPattern(pattern);
                } else {
                    df = DateTimeFormatter.ofPattern(pattern, locale);
                }
                //Issue #69: For instant serializers/deserializers we need to configure the formatter with
                //a time zone picked up from JsonFormat annotation, otherwise serialization might not work
                if (format.hasTimeZone()) {
                    df = df.withZone(format.getTimeZone().toZoneId());
                }
                deser = deser.withDateFormat(df);
            }
            // 17-Aug-2019, tatu: For 2.10 let's start considering leniency/strictness too
            if (format.hasLenient()) {
                Boolean leniency = format.getLenient();
                if (leniency != null) {
                    deser = deser.withLeniency(leniency);
                }
            }
            // any use for TimeZone?
        }
        return deser;
    }

    /**
     * @return {@code true} if lenient handling is enabled; {code false} if not (strict mode)
     *
     * @since 2.10
     */
    protected boolean isLenient() {
        return _isLenient;
    }
    
    protected void _throwNoNumericTimestampNeedTimeZone(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        ctxt.reportInputMismatch(handledType(),
"raw timestamp (%d) not allowed for `%s`: need additional information such as an offset or time-zone (see class Javadocs)",
p.getNumberValue(), handledType().getName());
    }

    @SuppressWarnings("unchecked")
    protected T _failForNotLenient(JsonParser p, DeserializationContext ctxt,
            JsonToken expToken) throws IOException
    {
       return (T) ctxt.handleUnexpectedToken(handledType(), expToken, p,
               "not allowed because 'strict' mode set for property or type (enabled 'lenient' handling to allow)"); 
    }
}
