package com.fasterxml.jackson.datatype.jsr310.deser;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializer for Java 8 temporal {@link MonthDay}s.
 */
public class MonthDayDeserializer extends JSR310DateTimeDeserializerBase<MonthDay>
{
    private static final long serialVersionUID = 1L;

    public static final MonthDayDeserializer INSTANCE = new MonthDayDeserializer(null);

    public MonthDayDeserializer(DateTimeFormatter formatter) {
        super(MonthDay.class, formatter);
    }

    @Override
    protected JsonDeserializer<MonthDay> withDateFormat(DateTimeFormatter dtf) {
        return new MonthDayDeserializer(dtf);
    }
    
    @Override
    public MonthDay deserialize(JsonParser parser, DeserializationContext context) throws IOException
    {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getValueAsString().trim();
            try {
                if (_formatter == null) {
                    return MonthDay.parse(string);
                }
                return MonthDay.parse(string, _formatter);
            } catch (DateTimeException e) {
                _rethrowDateTimeException(parser, context, e, string);
            }
        }
        if (parser.hasToken(JsonToken.VALUE_EMBEDDED_OBJECT)) {
            return (MonthDay) parser.getEmbeddedObject();
        }
        throw context.mappingException("Unexpected token (%s), expected VALUE_STRING or VALUE_NUMBER_INT",
                parser.getCurrentToken());
    }
}
