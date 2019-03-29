// Generated 28-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.module.paramnames {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports com.fasterxml.jackson.module.paramnames;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
}
