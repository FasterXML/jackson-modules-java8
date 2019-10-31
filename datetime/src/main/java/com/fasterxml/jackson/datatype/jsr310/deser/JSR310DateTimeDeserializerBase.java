package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Feature;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

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

    /**
     * Setting that indicates the {@Link JsonFormat.Shape} specified for this deserializer
     * as a {@link JsonFormat.Shape} annotation on property or class, or due to per-type
     * "config override", or from global settings:
     * If Shape is NUMBER_INT, the input value is considered to be epoch days. If not a
     * NUMBER_INT, and the deserializer was not specified with the leniency setting of true,
     * then an exception will be thrown.
     * @see [jackson-modules-java8#58] for more info
     *
     * @since 2.11
     */
    protected final Shape _shape;

    protected JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f) {
        super(supportedType);
        _formatter = f;
        _isLenient = true;
        _shape = null;
    }

    /**
     * @since 2.11
     */
    public JSR310DateTimeDeserializerBase(Class<T> supportedType, DateTimeFormatter f, Boolean leniency) {
        super(supportedType);
        _formatter = f;
        _isLenient = !Boolean.FALSE.equals(leniency);
        _shape = null;
    }

    /**
     * @since 2.10
     */
    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base,
            DateTimeFormatter f) {
        super(base);
        _formatter = f;
        _isLenient = base._isLenient;
        _shape = base._shape;
    }
    
    /**
     * @since 2.10
     */
    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base,
            Boolean leniency) {
        super(base);
        _formatter = base._formatter;
        _isLenient = !Boolean.FALSE.equals(leniency);
        _shape = base._shape;
    }

    /**
     * @since 2.11
     */
    protected JSR310DateTimeDeserializerBase(JSR310DateTimeDeserializerBase<T> base,
                                             Shape shape) {
        super(base);
        _formatter = base._formatter;
        _shape = shape;
        _isLenient = base._isLenient;
    }

    protected abstract JSR310DateTimeDeserializerBase<T> withDateFormat(DateTimeFormatter dtf);

    /**
     * @since 2.10
     */
    protected abstract JSR310DateTimeDeserializerBase<T> withLeniency(Boolean leniency);

    /**
     * @since 2.11
     */
    protected abstract JSR310DateTimeDeserializerBase<T> withShape(Shape shape);


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
                DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
                if (acceptCaseInsensitiveValues(ctxt, format)) {
                    builder.parseCaseInsensitive();
                }
                builder.appendPattern(pattern);
                DateTimeFormatter df;
                if (locale == null) {
                    df = builder.toFormatter();
                } else {
                    df = builder.toFormatter(locale);
                }

                if (format.hasLenient() && !format.isLenient()) {
                    df = df.withResolverStyle(ResolverStyle.STRICT);
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
            //Issue #58: For LocalDate deserializers we need to configure the formatter with
            //a shape picked up from JsonFormat annotation, to decide if the value is EpochSeconds
            JsonFormat.Shape shape = format.getShape();
            if (shape != null && shape != _shape) {
                deser = deser.withShape(shape);
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

    private boolean acceptCaseInsensitiveValues(DeserializationContext ctxt, JsonFormat.Value format) 
    {
        Boolean enabled = format.getFeature( Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        if( enabled == null) {
            enabled = ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES);
        }
        return enabled;
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
"Cannot deserialize instance of %s out of %s token: not allowed because 'strict' mode set for property or type (enable 'lenient' handling to allow)",
               ClassUtil.nameOf(handledType()), p.currentToken());
    }
}
