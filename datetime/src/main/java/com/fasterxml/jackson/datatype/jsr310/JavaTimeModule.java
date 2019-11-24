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
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.deser.std.StdValueInstantiator;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedClassResolver;
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
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.key.ZonedDateTimeKeySerializer;

/**
 * Class that registers capability of serializing {@code java.time} objects with the Jackson core.
 *
 * <pre>
 * ObjectMapper mapper = new ObjectMapper();
 * mapper.registerModule(new JavaTimeModule());
 * </pre>
 *<p>
 * Note that as of 2.x, if auto-registering modules, this package will register
 * legacy version, {@link JSR310Module}, and NOT this module. 3.x will change the default.
 * Legacy version has the same functionality, but slightly different default configuration:
 * see {@link com.fasterxml.jackson.datatype.jsr310.JSR310Module} for details.
 *<p>
 * Most {@code java.time} types are serialized as numbers (integers or decimals as appropriate) if the
 * {@link com.fasterxml.jackson.databind.SerializationFeature#WRITE_DATES_AS_TIMESTAMPS} feature is enabled
 * (or, for {@link Duration}, {@link com.fasterxml.jackson.databind.SerializationFeature#WRITE_DURATIONS_AS_TIMESTAMPS}),
 * and otherwise are serialized in standard
 * <a href="http://en.wikipedia.org/wiki/ISO_8601" target="_blank">ISO-8601</a> string representation.
 * ISO-8601 specifies formats for representing offset dates and times, zoned dates and times,
 * local dates and times, periods, durations, zones, and more. All {@code java.time} types
 * have built-in translation to and from ISO-8601 formats.
 * <p>
 * Granularity of timestamps is controlled through the companion features
 * {@link com.fasterxml.jackson.databind.SerializationFeature#WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS} and
 * {@link com.fasterxml.jackson.databind.DeserializationFeature#READ_DATE_TIMESTAMPS_AS_NANOSECONDS}. For serialization, timestamps are
 * written as fractional numbers (decimals), where the number is seconds and the decimal is fractional seconds, if
 * {@code WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS} is enabled (it is by default), with resolution as fine as nanoseconds depending on the
 * underlying JDK implementation. If {@code WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS} is disabled, timestamps are written as a whole number of
 * milliseconds. At deserialization time, decimal numbers are always read as fractional second timestamps with up-to-nanosecond resolution,
 * since the meaning of the decimal is unambiguous. The more ambiguous integer types are read as fractional seconds without a decimal point
 * if {@code READ_DATE_TIMESTAMPS_AS_NANOSECONDS} is enabled (it is by default), and otherwise they are read as milliseconds.
 * <p>
 * Some exceptions to this standard serialization/deserialization rule:
 * <ul>
 * <li>{@link Period}, which always results in an ISO-8601 format because Periods must be represented in years, months, and/or days.</li>
 * <li>{@link Year}, which only contains a year and cannot be represented with a timestamp.</li>
 * <li>{@link YearMonth}, which only contains a year and a month and cannot be represented with a timestamp.</li>
 * <li>{@link MonthDay}, which only contains a month and a day and cannot be represented with a timestamp.</li>
 * <li>{@link ZoneId} and {@link ZoneOffset}, which do not actually store dates and times but are supported with this module nonetheless.</li>
 * <li>{@link LocalDate}, {@link LocalTime}, {@link LocalDateTime}, and {@link OffsetTime}, which cannot portably be converted to timestamps
 * and are instead represented as arrays when WRITE_DATES_AS_TIMESTAMPS is enabled.</li>
 * </ul>
 *
 * @author Nick Williams
 * @author Zoltan Kiss
 *
 * @since 2.6
 *
 * @see com.fasterxml.jackson.datatype.jsr310.ser.key.Jsr310NullKeySerializer
 */
@SuppressWarnings("javadoc")
public final class JavaTimeModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public JavaTimeModule()
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

        /* 27-Jun-2015, tatu: This is the real difference from the old
         *  {@link JSR310Module}: default is to produce ISO-8601 compatible
         *  serialization with timezone offset only, not timezone id.
         *  But this is configurable.
         */
        addSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.INSTANCE);
        
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
                // because we will (for now) be getting a subtype, but in future may want to downgrade
                // to the common base type. Even more, serializer may purposefully force use of base type.
                // So... in practice it really should always work, in the end. :)
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
                            ac = AnnotatedClassResolver.resolve(config,
                                    config.constructType(ZoneId.class), config);
                        }
                        if (!inst.canCreateFromString()) {
                            AnnotatedMethod factory = _findFactory(ac, "of", String.class);
                            if (factory != null) {
                                inst.configureFromStringCreator(factory);
                            }
                            // otherwise... should we indicate an error?
                        }
                        // return ZoneIdInstantiator.construct(config, beanDesc, defaultInstantiator);
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
