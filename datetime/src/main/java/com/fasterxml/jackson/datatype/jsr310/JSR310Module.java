/*
 * Copyright 2013 FasterXML.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.fasterxml.jackson.datatype.jsr310;

import java.time.*;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.DurationDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.JSR310StringParsableDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.MonthDayDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.OffsetTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.YearMonthDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.key.*;
import com.fasterxml.jackson.datatype.jsr310.ser.DurationSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.MonthDaySerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearMonthSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.YearSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZoneIdSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.key.ZonedDateTimeKeySerializer;

/**
 * Older version of {@link JavaTimeModule} which was the default choice up to
 * Jackson 2.5, but was obsoleted in 2.6 by {@link JavaTimeModule}.
 * Functionality does not differ between the two modules (at least in 2.6),
 * so Javadocs for {@link JavaTimeModule} may be consulted for functionality
 * available.
 * The default settings do, however, such that
 *<ul>
 * <li>New {@link JavaTimeModule} uses same standard settings to default to
 *    serialization that does NOT use Timezone Ids, and instead only uses ISO-8601
 *    compliant Timezone offsets. Behavior may be changed using
 *    {@link com.fasterxml.jackson.databind.SerializationFeature#WRITE_DATES_WITH_ZONE_ID}
 *  </li>
 *  <li>Old {@link JSR310Module} defaults to serialization WITH Timezone Ids (to support
 *    round-trippability of values when using JSR-310 types and Jackson)
 *   </li>
 * </ul>
 * Note that it is, then, possible to upgrade to {@link JavaTimeModule} by simply
 * reconfiguring it by enabling
 * {@link com.fasterxml.jackson.databind.SerializationFeature#WRITE_DATES_WITH_ZONE_ID}.
 * This class is only retained to keep strict source and binary compatibility.
 *<p>
 * @author Nick Williams
 * @since 2.2.0
 * @see com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer
 * @see com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
 *
 * @deprecated Replaced by {@link JavaTimeModule} since Jackson 2.7, see above for
 *   details on differences in the default configuration.
 */
@Deprecated // since 2.5
public final class JSR310Module extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public JSR310Module()
    {
        super(PackageVersion.VERSION);

        // First deserializers

        // // Instant variants:
        addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        addDeserializer(OffsetDateTime.class, InstantDeserializer.OFFSET_DATE_TIME);
        addDeserializer(ZonedDateTime.class, InstantDeserializer.ZONED_DATE_TIME);
        
        // // Other deserializers
        addDeserializer(Duration.class, DurationDeserializer.INSTANCE);
        addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        addDeserializer(LocalDate.class, LocalDateDeserializer.INSTANCE);
        addDeserializer(LocalTime.class, LocalTimeDeserializer.INSTANCE);
        addDeserializer(MonthDay.class, MonthDayDeserializer.INSTANCE);
        addDeserializer(OffsetTime.class, OffsetTimeDeserializer.INSTANCE);
        addDeserializer(Period.class, JSR310StringParsableDeserializer.PERIOD);
        addDeserializer(Year.class, YearDeserializer.INSTANCE);
        addDeserializer(YearMonth.class, YearMonthDeserializer.INSTANCE);
        addDeserializer(ZoneId.class, JSR310StringParsableDeserializer.ZONE_ID);
        addDeserializer(ZoneOffset.class, JSR310StringParsableDeserializer.ZONE_OFFSET);

        // then serializers:
        addSerializer(Duration.class, DurationSerializer.INSTANCE);
        addSerializer(Instant.class, InstantSerializer.INSTANCE);
        addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        addSerializer(LocalDate.class, LocalDateSerializer.INSTANCE);
        addSerializer(LocalTime.class, LocalTimeSerializer.INSTANCE);
        addSerializer(MonthDay.class, MonthDaySerializer.INSTANCE);
        addSerializer(OffsetDateTime.class, OffsetDateTimeSerializer.INSTANCE);
        addSerializer(OffsetTime.class, OffsetTimeSerializer.INSTANCE);
        addSerializer(Period.class, new ToStringSerializer(Period.class));
        addSerializer(Year.class, YearSerializer.INSTANCE);
        addSerializer(YearMonth.class, YearMonthSerializer.INSTANCE);

        /* 27-Jun-2015, tatu: This is the real difference to the new
         *  {@link JavaTimeModule}: default is to include timezone id, not just offset
         */
        addSerializer(ZonedDateTime.class, _zonedWithZoneId());
        // since 2.11: need to override Type Id handling
        // (actual concrete type is `ZoneRegion`, but that's not visible)
        addSerializer(ZoneId.class, new ZoneIdSerializer());

        addSerializer(ZoneOffset.class, new ToStringSerializer(ZoneOffset.class));

        // key serializers
        addKeySerializer(ZonedDateTime.class, ZonedDateTimeKeySerializer.INSTANCE);

        // key deserializers
        addKeyDeserializer(Duration.class, DurationKeyDeserializer.INSTANCE);
        addKeyDeserializer(Instant.class, InstantKeyDeserializer.INSTANCE);
        addKeyDeserializer(LocalDateTime.class, LocalDateTimeKeyDeserializer.INSTANCE);
        addKeyDeserializer(LocalDate.class, LocalDateKeyDeserializer.INSTANCE);
        addKeyDeserializer(LocalTime.class, LocalTimeKeyDeserializer.INSTANCE);
        addKeyDeserializer(MonthDay.class, MonthDayKeyDeserializer.INSTANCE);
        addKeyDeserializer(OffsetDateTime.class, OffsetDateTimeKeyDeserializer.INSTANCE);
        addKeyDeserializer(OffsetTime.class, OffsetTimeKeyDeserializer.INSTANCE);
        addKeyDeserializer(Period.class, PeriodKeyDeserializer.INSTANCE);
        addKeyDeserializer(Year.class, YearKeyDeserializer.INSTANCE);
        addKeyDeserializer(YearMonth.class, YearMonthKeyDeserializer.INSTANCE);
        addKeyDeserializer(ZonedDateTime.class, ZonedDateTimeKeyDeserializer.INSTANCE);
        addKeyDeserializer(ZoneId.class, ZoneIdKeyDeserializer.INSTANCE);
        addKeyDeserializer(ZoneOffset.class, ZoneOffsetKeyDeserializer.INSTANCE);
    }

    private static JsonSerializer<ZonedDateTime> _zonedWithZoneId() {
        return com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeWithZoneIdSerializer.INSTANCE;
    }
    
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addValueInstantiators(new ValueInstantiators.Base() {
            @Override
            public ValueInstantiator findValueInstantiator(DeserializationConfig config,
                    BeanDescription beanDesc, ValueInstantiator defaultInstantiator)
            {
                JavaType type = beanDesc.getType();
                Class<?> raw = type.getRawClass();
                // 15-May-2015, tatu: In theory not safe, but in practice we do need to do "fuzzy" matching
                //    because we will (for now) be getting a subtype, but in future may want to downgrade
                //    to the common base type. Even more, serializer may purposefully force use of base type.
                //    So... in practice it really should always work, in the end. :)
                if (ZoneId.class.isAssignableFrom(raw)) {
                    // let's assume we should be getting "empty" StdValueInstantiator here:
                    if (defaultInstantiator instanceof StdValueInstantiator) {
                        StdValueInstantiator inst = (StdValueInstantiator) defaultInstantiator;
                        // one further complication: we need ZoneId info, not sub-class
                        AnnotatedClass ac;
                        if (raw == ZoneId.class) {
                            ac = beanDesc.getClassInfo();
                        } else {
                            // we don't need Annotations, so constructing directly is fine here
                            // even if it's not generally recommended
                            ac = AnnotatedClass.construct(config.constructType(ZoneId.class), config);
                        }
                        if (!inst.canCreateFromString()) {
                            AnnotatedMethod factory = _findFactory(ac, "of", String.class);
                            if (factory != null) {
                                inst.configureFromStringCreator(factory);
                            }
                            // otherwise... should we indicate an error?
                        }
                        //return ZoneIdInstantiator.construct(config, beanDesc, defaultInstantiator);
                    }
                }
                return defaultInstantiator;
            }
        });
    }

    protected AnnotatedMethod _findFactory(AnnotatedClass cls, String name, Class<?>... argTypes)
    {
        final int argCount = argTypes.length;
        for (AnnotatedMethod method : cls.getFactoryMethods()) {
            if (!name.equals(method.getName())
                || (method.getParameterCount() != argCount)) {
                continue;
            }
            for (int i = 0; i < argCount; ++i) {
                Class<?> argType = method.getParameter(i).getRawType();
                if (!argType.isAssignableFrom(argTypes[i])) {
                    continue;
                }
            }
            return method;
        }
        return null;
    }
}
