Datatype module to make Jackson recognize Java 8 Date & Time API data types (JSR-310).

## Summary

Most [JSR-310](https://jcp.org/en/jsr/detail?id=310) types are serialized as numbers (integers or decimals as appropriate) if the
[`SerializationFeature#WRITE_DATES_AS_TIMESTAMPS`](http://fasterxml.github.com/jackson-databind/javadoc/2.2.0/com/fasterxml/jackson/databind/SerializationFeature.html#WRITE_DATES_AS_TIMESTAMPS)
feature is enabled, and otherwise are serialized in standard [ISO-8601](http://en.wikipedia.org/wiki/ISO_8601)
string representation. ISO-8601 specifies formats for representing offset dates and times, zoned dates and times,
local dates and times, periods, durations, zones, and more. All JSR-310 types have built-in translation to and from
ISO-8601 formats.

For string representation default formats for deserialization are:
|  Java type | Format  |
|------------|---------|
|[`Instant`](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html)|[`DateTimeFormatter.ISO_INSTANT`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_INSTANT)|
|[`OffsetDateTime`](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html)|[`DateTimeFormatter.ISO_OFFSET_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_OFFSET_DATE_TIME)|
|[`ZonedDateTime`](https://docs.oracle.com/javase/8/docs/api/java/time/ZonedDateTime.html)|[`DateTimeFormatter.ISO_ZONED_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_ZONED_DATE_TIME)|
|[`LocalDateTime`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html)|[`DateTimeFormatter.ISO_LOCAL_DATE_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE_TIME)|
|[`LocalDate`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html)|[`DateTimeFormatter.ISO_LOCAL_DATE`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_DATE)|
|[`LocalTime`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html)|[`DateTimeFormatter.ISO_LOCAL_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_LOCAL_TIME)|
|[`OffsetTime`](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetTime.html)|[`DateTimeFormatter.ISO_OFFSET_TIME`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ISO_OFFSET_TIME)|
|[`YearMonth`](https://docs.oracle.com/javase/8/docs/api/java/time/YearMonth.html)|[`DateTimeFormatter.ofPattern("uuuu-MM")`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html#ofPattern-java.lang.String-)|

Granularity of timestamps is controlled through the companion features
[`SerializationFeature#WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS`](http://fasterxml.github.com/jackson-databind/javadoc/2.2.0/com/fasterxml/jackson/databind/SerializationFeature.html#WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
and
[`DeserializationFeature#READ_DATE_TIMESTAMPS_AS_NANOSECONDS`](http://fasterxml.github.com/jackson-databind/javadoc/2.2.0/com/fasterxml/jackson/databind/DeserializationFeature.html#READ_DATE_TIMESTAMPS_AS_NANOSECONDS).
For serialization, timestamps are written as fractional numbers (decimals), where the number is seconds and the decimal
is fractional seconds, if `WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS` is enabled (it is by default), with resolution as fine
as nanoseconds depending on the underlying JDK implementation. If `WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS` is disabled,
timestamps are written as a whole number of milliseconds. At deserialization time, decimal numbers are always read as
fractional second timestamps with up-to-nanosecond resolution, since the meaning of the decimal is unambiguous. The
more ambiguous integer types are read as fractional seconds without a decimal point if
`READ_DATE_TIMESTAMPS_AS_NANOSECONDS` is enabled (it is by default), and otherwise they are read as milliseconds.

For TimeZone handling, `ADJUST_DATES_TO_CONTEXT_TIME_ZONE` (default: true) specifies whether the context provided by `java.time.TimeZone` 
'SerializedProvider#getTimeZone()' should be used to adjust Date/Time values on deserialization, even if the value itself
contains timezone information. If disabled, it will only be used if the value itself does not contain any TimeZone information.

Finally, there are two features that apply to array handling. `UNWRAP_SINGLE_VALUE_ARRAYS` (default: false) allows auto-conversion from single-element arrays to non-JSON-array 
values. If the JSON value contains more than one element in the array, deserialization will still fail. `ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT` (default: false) determines whether empty Array value ("[ ]" in JSON) is accepted 
as null value for regular POJOs ("beans") with data-binding
    
Some exceptions to this standard serialization/deserialization rule:
* [`Instant`](https://docs.oracle.com/javase/8/docs/api/java/time/Instant.html), which always has the UTC time zone when `ADJUST_DATES_TO_CONTEXT_TIME_ZONE` is enabled.
* [`Period`](https://docs.oracle.com/javase/8/docs/api/java/time/Period.html), which always results in an ISO-8601 format
because Periods must be represented in years, months, and/or days.
* [`Year`](https://docs.oracle.com/javase/8/docs/api/java/time/Year.html), which only contains a year and cannot be
represented with a timestamp.
* [`YearMonth`](https://docs.oracle.com/javase/8/docs/api/java/time/YearMonth.html), which only contains a year and a month
and cannot be represented with a timestamp.
* [`MonthDay`](https://docs.oracle.com/javase/8/docs/api/java/time/MonthDay.html), which only contains a month and a day and
cannot be represented with a timestamp.
* [`ZoneId`](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneId.html) and
[`ZoneOffset`](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneOffset.html), which do not actually store dates and
times but are supported with this module nonetheless.
* [`LocalDate`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDate.html),
[`LocalTime`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html),
[`LocalDateTime`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalDateTime.html), and
[`OffsetTime`](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetTime.html), which cannot portably be converted to
timestamps and are instead represented as arrays when `WRITE_DATES_AS_TIMESTAMPS` is enabled.

## Usage

### Registering module

Starting with Jackson 2.2, `Module`s can be automatically discovered using the Java 6 Service Provider Interface (SPI) feature.
You can activate this by instructing an `ObjectMapper` to find and register all `Module`s:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.findAndRegisterModules();
```

You should use this feature with caution as it has performance implications. You should generally create one constant
`ObjectMapper` instance for your entire application codebase to share, or otherwise use one of `ObjectMapper`'s
`findModules` methods and cache the result.

If you prefer to selectively register this module, this is done as follows, without the call to
`findAndRegisterModules()`:

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());
```

After either of these, functionality is available for all normal Jackson operations.

## More

See [Wiki](../../../wiki) for more information
(JavaDocs, downloads).

Also: there is [JDK 1.7 backport](https://github.com/joschi/jackson-datatype-threetenbp) datatype module!
