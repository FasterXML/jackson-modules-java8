package com.fasterxml.jackson.datatype.jsr310.deser;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    protected JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f)
    {
        super(supportedType);
        _formatter = f;
    }

    protected abstract JsonDeserializer<T> withDateFormat(DateTimeFormatter dtf);

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
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
                return withDateFormat(df);
            }
            // any use for TimeZone?
        }
        return this;
   }
}
