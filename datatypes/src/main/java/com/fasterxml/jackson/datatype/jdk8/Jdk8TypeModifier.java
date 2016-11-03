package com.fasterxml.jackson.datatype.jdk8;

import java.lang.reflect.Type;
import java.util.*;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeModifier;

/**
 * We need to ensure `Optional` is a `ReferenceType`
 */
public class Jdk8TypeModifier extends TypeModifier
{
    @Override
    public JavaType modifyType(JavaType type, Type jdkType, TypeBindings bindings, TypeFactory typeFactory)
    {
        if (type.isReferenceType() || type.isContainerType()) {
            return type;
        }
        final Class<?> raw = type.getRawClass();

        JavaType refType;

        if (raw == Optional.class) {
            // 19-Oct-2015, tatu: Looks like we may be missing type information occasionally,
            //    perhaps due to raw types.
            refType = type.containedTypeOrUnknown(0);
        } else if (raw == OptionalInt.class) {
            refType = typeFactory.constructType(Integer.TYPE);
        } else if (raw == OptionalLong.class) {
            refType = typeFactory.constructType(Long.TYPE);
        } else if (raw == OptionalDouble.class) {
            refType = typeFactory.constructType(Double.TYPE);
        } else {
            return type;
        }
        return ReferenceType.upgradeFrom(type, refType);
    }
}
