package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class Jdk8OptionalBeanPropertyWriter extends BeanPropertyWriter
{
    private static final long serialVersionUID = 1L;

    /**
     * @since 2.9
     */
    protected final Object _empty;
    
    /**
     * @since 2.9
     */
    protected Jdk8OptionalBeanPropertyWriter(BeanPropertyWriter base, Object empty) {
        super(base);
        _empty = empty;
    }

    protected Jdk8OptionalBeanPropertyWriter(Jdk8OptionalBeanPropertyWriter base, PropertyName newName) {
        super(base, newName);
        _empty = base._empty;
    }

    @Override
    protected BeanPropertyWriter _new(PropertyName newName) {
        return new Jdk8OptionalBeanPropertyWriter(this, newName);
    }

    @Override
    public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper) {
        return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, unwrapper, _empty);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator g, SerializerProvider prov) throws Exception
    {
        if (_nullSerializer == null) {
            Object value = get(bean);
            if (value == null || value.equals(_empty)) {
                return;
            }
        }
        super.serializeAsField(bean, g, prov);
    }

}
