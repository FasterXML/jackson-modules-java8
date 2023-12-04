package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeFeature;


public class MonthEnumDeserializerModifier extends BeanDeserializerModifier {
    private final boolean _oneBaseMonths;

    public MonthEnumDeserializerModifier() {
        this(false);
    }

    public MonthEnumDeserializerModifier(boolean oneBaseMonths) {
        _oneBaseMonths = oneBaseMonths;
    }

    public MonthEnumDeserializerModifier withFeatures(JacksonFeatureSet<JavaTimeFeature> features) {
        boolean enabled = features.isEnabled(JavaTimeFeature.ONE_BASED_MONTHS);
        if (enabled == _oneBaseMonths) {
            return this;
        }
        return new MonthEnumDeserializerModifier(enabled);
    }

    @Override
    public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config, JavaType type, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if(type.getRawClass() != Month.class) {
            return deserializer;
        }
        return new JsonDeserializer<Enum>() {
            @Override
            public Enum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                Month zeroBaseMonth = (Month) deserializer.deserialize(p, ctxt);
                if (!_oneBaseMonths || zeroBaseMonth == null) {
                    return zeroBaseMonth;
                }
                if (_oneBaseMonths) {
                    if (zeroBaseMonth == Month.JANUARY) {
                        // throw exception?
                    } else {
                        return zeroBaseMonth.minus(1);
                    }
                }
                return zeroBaseMonth;
            }
        };
    }

}
