package com.fasterxml.jackson.datatype.jsr310.ser;

import java.io.IOException;
import java.time.Month;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @since 2.17
 */
public class OneBasedMonthSerializer extends JsonSerializer<Month> {
    private final JsonSerializer<Object> _defaultSerializer;

    @SuppressWarnings("unchecked")
    public OneBasedMonthSerializer(JsonSerializer<?> defaultSerializer) 
    {
        _defaultSerializer = (JsonSerializer<Object>) defaultSerializer;
    }

    @Override
    public void serialize(Month value, JsonGenerator gen, SerializerProvider ctxt)
        throws IOException
    {
        // 15-Jan-2024, tatu: [modules-java8#274] This is not really sufficient
        //   (see `jackson-databind` `EnumSerializer` for full logic), but has to
        //   do for now. May need to add `@JsonFormat.shape` handling in future.
        if (ctxt.isEnabled(SerializationFeature.WRITE_ENUMS_USING_INDEX)) {
            gen.writeNumber(value.ordinal() + 1);
            return;
        }
        _defaultSerializer.serialize(value, gen, ctxt);
    }
}
