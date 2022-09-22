package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

public class Jdk8Module extends Module
{
    public final static boolean DEFAULT_READ_ABSENT_AS_NULL = false;

    /**
     * Configuration setting that determines whether `Optional.empty()` is
     * considered "same as null" for serialization purposes; that is, to be
     * filtered same as nulls are.
     * If enabled, absent values are treated like nulls; if disabled, they are not.
     * In either case, absent values are always considered "empty".
     *<p>
     * Default value is `false` for backwards compatibility (2.5 and prior
     * only had this behavior).
     *<p>
     * Note that this setting MUST be changed BEFORE registering the module:
     * changes after registration will have no effect.
     *<p>
     * Note that in most cases it makes more sense to just use `NON_ABSENT` inclusion
     * criteria for filtering out absent optionals; this setting is mostly useful for
     * legacy use cases that predate version 2.6.
     */
    protected boolean _cfgWriteAbsentAsNull = false;

    /**
     * See {@link #configureReadAbsentAsNull} for details of this configuration
     * setting.
     *
     * @since 2.14
     */
    protected boolean _cfgReadAbsentAsNull = DEFAULT_READ_ABSENT_AS_NULL;

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new Jdk8Serializers());
        context.addDeserializers(new Jdk8Deserializers(_cfgReadAbsentAsNull));
        // And to fully support Optionals, need to modify type info:
        context.addTypeModifier(new Jdk8TypeModifier());

        // Allow enabling "treat Optional.empty() like Java nulls"
        if (_cfgWriteAbsentAsNull) {
            context.addBeanSerializerModifier(new Jdk8BeanSerializerModifier());
        }
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    /**
     * Configuration method that may be used to change configuration setting
     * {@code _cfgHandleAbsentAsNull}: enabling means that `Optional.empty()` values
     * are handled like Java nulls (wrt filtering on serialization); disabling that
     * they are only treated as "empty" values, but not like native Java nulls.
     * Recommended setting for this value is `false`. For compatibility with older versions
     * of other "optional" values (like Guava optionals), it can be set to 'true'. The
     * default is `false` for backwards compatibility.
     *<p>
     * Note that in most cases it makes more sense to just use `NON_ABSENT` inclusion
     * criteria for filtering out absent optionals; this setting is mostly useful for
     * legacy use cases that predate version 2.6.
     *
     * @return This module instance, useful for chaining calls
     *
     * @since 2.6
     * 
     * @deprecated Since 2.13, no replacement, will be removed from Jackson 3.0
     *    (when optional types will be part of core databind)
     */
    @Deprecated
    public Jdk8Module configureAbsentsAsNulls(boolean state) {
        _cfgWriteAbsentAsNull = state;
        return this;
    }

    /**
     * Method for configuring handling of "absent" {@link java.util.Optional}
     * values; absent meaning case where no value is found to pass via
     * Creator method (constructor, factory method).
     * If enabled (set to {@code true}) it will be deserialized as
     * {@code null}; if disabled it will be read as "empty" {@code Optional}
     * (same as if encountering actual JSON {@code null} value).
     *<p>
     * Default is {@code false} for backwards compatibility (retains behavior
     * pre-2.14); for Jackson 3.0 default will likely be changed.
     *
     * @since 2.14
     */
    public Jdk8Module configureReadAbsentAsNull(boolean state) {
        _cfgReadAbsentAsNull = state;
        return this;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public String getModuleName() {
        return "Jdk8Module";
    }
}
