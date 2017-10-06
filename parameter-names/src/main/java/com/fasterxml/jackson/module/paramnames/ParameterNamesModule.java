package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * @deprecated Since 3.0 functionality included in core databind directly
 */
@Deprecated // since 3.0
public class ParameterNamesModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public ParameterNamesModule(JsonCreator.Mode creatorBinding) {
        super(PackageVersion.VERSION);
    }

    public ParameterNamesModule() {
        super(PackageVersion.VERSION);
    }
}
