## Overview

This is a multi-module umbrella project for [Jackson](../../../jackson)
modules needed to support Java 8 features when core Jackson modules do not
(yet) require Java 8 runtime (note: Jackson 3.0 will likely increase baseline
and allow including some or all of this functionality)

This includes 3 modules:

* [Parameter names](parameter-names/): support for detecting constructor and factory method ("creator") parameters without having to use `@JsonProperty` annotation
* [Java 8 Date/time](datetime): support for Java 8 date/time types (specified in JSR-310 specification)
* [Java 8 Datatypes](datatypes): support for other new Java 8 datatypes outside of date/time: most notably `Optional`, `OptionalLong`, `OptionalDouble`

all of which are built from this repository, and accessed and used as separate Jackson modules
(with separate Maven artifacts).

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-base-java8.svg)](https://travis-ci.org/FasterXML/jackson-base-java8)

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
   .registerModule(new JavaTimeModule())
;
```

or, alternatively, you can also auto-discover these modules with:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.findAndRegisterModules();
```

Either way, after registration all functionality is available for all normal Jackson operations.

## More

See [Wiki](../../wiki) for more information (javadocs).

