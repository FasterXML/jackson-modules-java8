package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

/**
 * @deprecated Since 3.0 functionality included in core databind directly
 */
@Deprecated // since 3.0
public class Jdk8Module extends Module
{
    protected boolean _cfgHandleAbsentAsNull = false;

    @Override
    public void setupModule(SetupContext context) {
        ;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    public Jdk8Module configureAbsentsAsNulls(boolean state) {
        _cfgHandleAbsentAsNull = state;
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
