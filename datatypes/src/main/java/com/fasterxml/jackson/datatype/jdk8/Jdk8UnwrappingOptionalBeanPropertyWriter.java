package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class Jdk8UnwrappingOptionalBeanPropertyWriter extends UnwrappingBeanPropertyWriter
{
    private static final long serialVersionUID = 1L;

    /**
     * @since 2.9
     */
    protected final Object _empty;

    public Jdk8UnwrappingOptionalBeanPropertyWriter(BeanPropertyWriter base,
            NameTransformer transformer, Object empty) {
        super(base, transformer);
        _empty = empty;
    }

    protected Jdk8UnwrappingOptionalBeanPropertyWriter(Jdk8UnwrappingOptionalBeanPropertyWriter base,
            NameTransformer transformer, SerializedString name) {
        super(base, transformer, name);
        _empty = base._empty;
    }

    @Override
    protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName)
    {
        return new Jdk8UnwrappingOptionalBeanPropertyWriter(this, transformer, newName);
    }

    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        if (_nullSerializer == null) {
            Object value = get(bean);
            if (value == null || value.equals(_empty)) {
                return;
            }
        }
        super.serializeAsField(bean, gen, prov);
    }
}
