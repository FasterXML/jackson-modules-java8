package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

@SuppressWarnings("serial")
public abstract class BaseScalarOptionarDeserializer<T>
    extends StdScalarDeserializer<T>
{
    protected final T _empty;
    
    protected BaseScalarOptionarDeserializer(Class<T> cls, T empty) {
        super(cls);
        _empty = empty;
    }
    
    @Override
    public T getNullValue(DeserializationContext ctxt) {
        return _empty;
    }
}
