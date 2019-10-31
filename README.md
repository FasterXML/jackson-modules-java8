## Overview

This is a multi-module umbrella project for [Jackson](../../../jackson)
modules needed to support Java 8 features, especially with Jackson 2.x that only
requires Java 7 for running (and until 2.7 only Java 6).

### Jackson 2.x

When used with Jackson 2.x, Java 8 support is provided via 3 separate modules:

* [Parameter names](parameter-names/): support for detecting constructor and factory method ("creator") parameters without having to use `@JsonProperty` annotation
    * provides `com.fasterxml.jackson.module.paramnames.ParameterNamesModule`
* [Java 8 Date/time](datetime/): support for Java 8 date/time types (specified in JSR-310 specification)
    * provides `com.fasterxml.jackson.datatype.jsr310.JavaTimeModule`
    * ALSO provides legacy variant `com.fasterxml.jackson.datatype.jsr310.JSR310TimeModule`
    * difference between 2 modules is that of configuration defaults: use of `JavaTimeModule` strongly recommended for new code
* [Java 8 Datatypes](datatypes/): support for other new Java 8 datatypes outside of date/time: most notably `Optional`, `OptionalLong`, `OptionalDouble`
    * provides `com.fasterxml.jackson.datatype.jdk8.Jdk8Module`

all of which are built from this repository, and accessed and used as separate Jackson modules
(with separate Maven artifacts).

### Jackson 3.0

Jackson 3.0 changes things as it requires Java 8 to work and can thereby directly supported features.

Because of this `parameter-names` and `datatypes` modules are merged into `jackson-databind`
and need not be registered; `datetime` module (`JavaTimeModule`) remains separate module due to its size
and configurability options.

So you will only need to separately add "Java 8 Date/time" module (see above for description)

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-modules-java8.svg)](https://travis-ci.org/FasterXML/jackson-modules-java8)
[![Tidelift](https://tidelift.com/badges/package/maven/com.fasterxml.jackson.datatype:jackson-datatype-jsr310)](https://tidelift.com/subscription/pkg/maven-com-fasterxml-jackson-datatype-jackson-datatype-jsr310?utm_source=maven-com-fasterxml-jackson-datatype-jackson-datatype-jsr310&utm_medium=referral&utm_campaign=readme)

## Usage

### Maven dependencies

To include modules, you use some or all of:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-parameter-names</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jdk8</artifactId>
</dependency>
<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

and either include versions directly, OR, preferably, import
[Jackson BOM](../../../jackson-bom) that will specify consistent version set.

Note that the parent project -- `jackson-modules-java8` -- is ONLY used as parent pom by
individual "child" modules, and DOES NOT have dependencies on them. This means that you should not depend on it
as that will not include child modules.

### Registering modules

The most common mechanism (and one recommended by Jackson team) is to explicitly register modules you want.
This is done by code like:

```java
// Up to Jackson 2.9: (but not with 3.0)
ObjectMapper mapper = new ObjectMapper()
   .registerModule(new ParameterNamesModule())
   .registerModule(new Jdk8Module())
   .registerModule(new JavaTimeModule()); // new module, NOT JSR310Module

// with 3.0 (or with 2.10 as alternative)
ObjectMapper mapper = JsonMapper.builder() // or different mapper for other format
   .addModule(new ParameterNamesModule())
   .addModule(new Jdk8Module())
   .addModule(new JavaTimeModule())
   // and possibly other configuration, modules, then:
   .build();
```

Alternatively, you can also auto-discover these modules with:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.findAndRegisterModules();
```
Regardless of registration mechanism, after registration all functionality is available for all normal Jackson operations.

#### Notes on Registration

But do note that you should only either explicit OR automatic registration: DO NOT combine explicit
and auto-registration. If you use both, only one of registrations will have effect.
And selection of which one varies by module and settings:

* If `MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS` is defined, the FIRST registration succeeds, rest ignored
    * Duplicates are detected using id provided by `Module.getTypeId()`; duplicate-detection requires that Module provides same for all instances (true for Modules provided by this repo)
* Otherwise all registrations are processed by the LAST one has effect as it has precedence over earlier registrations.


Also note that before Jackson 2.10, auto-registration will only register older `JSR310Module`, and not newer
`JavaTimeModule` -- this is due to backwards compatibility. This is changed in Jackson 2.10.

If you want "the other" version of the module but also use auto-registration, make sure to
register "other" module explicitly AFTER calling `mapper.findAndRegisterModules()`.
Call after works because `getTypeId()` provided by modules differs so they are not considered duplicates.

## More

See [Wiki](../../wiki) for more information (javadocs).
