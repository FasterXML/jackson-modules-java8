## Overview

This is a multi-module umbrella project for [Jackson](../../../jackson)
modules needed to support Java 8 features when core Jackson modules do not
(yet) require Java 8 runtime.

This includes:

* [Parameter names](parameter-names/)
* [Java 8 Datatypes](datatypes)
* [Java 8 Date/time](datetime)

## License

All modules are licensed under [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

## Status

[![Build Status](https://travis-ci.org/FasterXML/jackson-base-java8.svg)](https://travis-ci.org/FasterXML/jackson-base-java8)

## Usage

### Maven dependencies

```xml
dependency>
    <groupId>com.fasterxml.jackson.module</groupId>
    <artifactId>jackson-module-parameter-names</artifactId>
</dependency>
dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jdk8</artifactId>
</dependency>
dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.6.1</version>
</dependency>

```

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

