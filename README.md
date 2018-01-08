## Overview

This is a multi-module umbrella project for [Jackson](../../../jackson)
modules needed to support Java 8 features, especially with Jackson 2.x that only
requires Java 7 for running (and until 2.7 only Java 6).

For Jackson 2.x this includes 3 modules:

* [Parameter names](parameter-names/): support for detecting constructor and factory method ("creator") parameters without having to use `@JsonProperty` annotation
    * provides `com.fasterxml.jackson.module.paramnames.ParameterNamesModule`
* [Java 8 Date/time](datetime): support for Java 8 date/time types (specified in JSR-310 specification)
    * provides `com.fasterxml.jackson.datatype.jsr310.JavaTimeModule`
    * ALSO provides legacy variant `com.fasterxml.jackson.datatype.jsr310.JSR310TimeModule`
    * difference between 2 modules is that of configuration defaults: use of `JavaTimeModule` strongly recommended for new code
* [Java 8 Datatypes](datatypes): support for other new Java 8 datatypes outside of date/time: most notably `Optional`, `OptionalLong`, `OptionalDouble`
    * provides `com.fasterxml.jackson.datatype.jdk8.Jdk8Module`

all of which are built from this repository, and accessed and used as separate Jackson modules
(with separate Maven artifacts).

Jackson 3.x changes things as it requires Java 8 to work and can thereby directly supported features.
Because of this `parameter-names` and `datatypes` modules are merged into `jackson-databind`
and need not be registered; `datetime` module (`JavaTimeModule`) remains separate module due to its size
and configurability options.

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-modules-java8.svg)](https://travis-ci.org/FasterXML/jackson-modules-java8)

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

### Registering modules

```java
ObjectMapper mapper = new ObjectMapper()
   .registerModule(new ParameterNamesModule())
   .registerModule(new Jdk8Module())
   .registerModule(new JavaTimeModule()) // new module, NOT JSR310Module
;
```

or, alternatively, you can also auto-discover these modules with:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.findAndRegisterModules();
```

Either way, after registration all functionality is available for all normal Jackson operations.

*WARNING* - As of Jackson 2.x, auto-registration will only register older `JSR310Module`, and not newer
`JavaTimeModule` -- this is due to backwards compatibility. Because of this make sure to either use explicit
registration, or, if you want to use `JavaTimeModule` but also auto-registration, make sure to
register `JavaTimeModule` BEFORE calling `mapper.findAndRegisterModules()`).

## More

See [Wiki](../../wiki) for more information (javadocs).
