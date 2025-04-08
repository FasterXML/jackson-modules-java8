Project: jackson-modules-java8
Modules:
  jackson-module-parameter-names
  jackson-datatype-jdk8
  jackson-datatype-jsr310

------------------------------------------------------------------------
=== Releases ===
------------------------------------------------------------------------

2.19.0-rc2 (07-Apr-2025)

#336: Optimize `InstantDeserializer` `addInColonToOffsetIfMissing()`
 (contributed by David S)
#337: Negative `Duration` does not round-trip properly with
  `WRITE_DURATIONS_AS_TIMESTAMPS` enabled
 (reported by Joey M)
 (fix by Joo-Hyuk K)
#342: Lenient deserialization of `LocalDate`, `LocalDateTime`
  is not time-zone aware
 (contributed by Henning P)
#364: Deserialization of Month in ONE_BASED_MONTHS mode fails for value "12"
 (reported, fix contributed by Boleslav B)

2.18.4 (not yet released)

#291: `InstantDeserializer` fails to parse negative numeric timestamp strings
  for pre-1970 values
 (reported by @advorako)
 (fixed by Kevin M)

2.18.3 (28-Feb-2025)

#333: `ZonedDateTime` serialization with `@JsonFormat.pattern` never uses it
  while `WRITE_DATES_WITH_ZONE_ID` enabled
 (reported by @verve111)
 (fix by Joo-Hyuk K)

2.18.2 (27-Nov-2024)

#308: Can't deserialize `OffsetDateTime.MIN`: Invalid value for EpochDay
 (reported by @sszuev)
 (fix by Joo-Hyuk K)

2.18.1 (28-Oct-2024)

#319: `java.time.DateTimeException` serialization fails
 (repored by Ólafur B)

2.18.0 (26-Sep-2024)

2.17.4 (not yet released)

#124 Issue serializing and deserializing `LocalDateTime.MAX` and `LocalDateTime.MIN`
 (reported by @bachilast)
 (fix verified by Joo-Hyuk K)

2.17.3 (01-Nov-2024)
2.17.2 (05-Jul-2024)

No changes since 2.17.1

2.17.1 (04-May-2024)

#306: Only `DateTimeFormatter.ISO_OFFSET_DATE_TIME` accepted by `ZonedDateTimeKeyDeserializer`
 (fix contributed by @caluml)

2.17.0 (12-Mar-2024)

#274: Deserializing `java.time.Month` from an int causes an off-by-one
  error (`0`->`Jan`,`11`->`Dec`), because it's an enum
 (reported by Christoffer H)
 (fix contributed Emanuel T)
#294: `Optional` deserialization, serialization ignore `contentConverter`
 (reported by @richardsonwk)

2.16.2 (09-Mar-2024)

#296: NPE when serializing a `LocalDate` or `LocalDateTime` using `AsDeductionTypeSerializer`
 (reported by @mike-reynolds-savient)

2.16.1 (24-Dec-2023)

#286: Breaking change in `InstantDeserializer API between 2.15 and 2.16
 (reported by Harald K)
#288: `LocalDateTime` serialization issue with custom-configured
  `LocalDateTimeSerializer`
 (reported by @mklinkj)

2.16.0 (15-Nov-2023)

#263: Add `JavaTimeFeature.ALWAYS_ALLOW_STRINGIFIED_TIMESTAMPS` to allow parsing
  quoted numbers when using a custom pattern (DateTimeFormatter)
 (contributed by M.P. Korstanje)
#272: (datetime) `JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS`
  not respected when deserialising `Instant`s
 (fix contributed by Raman B)
#281: (datetime) Add `JavaTimeFeature.NORMALIZE_DESERIALIZED_ZONE_ID` to allow
  disabling ZoneId normalization on deserialization
 (requested by @indyana)

2.15.4 (15-Feb-2024)
2.15.3 (12-Oct-2023)
2.15.2 (30-May-2023)
2.15.1 (16-May-2023)

No changes since 2.15.0

2.15.0 (23-Apr-2023)

#255: Change `InstantSerializerBase` to generate schema format in the same
  way as `DateTimeSerializerBase`
 (contributed by Felipe R)
#259: Wrong module auto-registered when using JPMS
 (reported by Michał O)
#266: Optimize `InstantDeserializer` method `replaceZeroOffsetAsZIfNecessary()`
 (contributed by David S)
#267: Normalize zone id during ZonedDateTime deserialization
 (contribtued by Daniel S)

2.14.3 (05-May-2023)
2.14.2 (28-Jan-2023)
2.14.1 (21-Nov-2022)

No changes since 2.14.0

2.14.0 (05-Nov-2022)

#224: `DurationSerializer` ignores format pattern if nano-second
  serialization enabled
 (reported by Sam K)
#230: Change `LocalDateTimeSerializer` constructor protected from private
 (requested by trydofor@github)
#240: `LocalDateDeserializer` should consider coercionConfig settings
 (contributed by (Maciej D)
#242: Fix InstantSerializer ignoring the JsonFormat shape
 (contributed by KaseiFR@github)
#249: `YearMonthDeserializer` fails for year > 9999
 (reported by bent-lorentzen@github)
#251: Allow `Optional` deserialization for "absent" value as Java `null`
 (like other Reference types), not "empty"

2.13.4 (03-Sep-2022)
2.13.3 (14-May-2022)
2.13.2 (06-Mar-2022)
2.13.1 (19-Dec-2021)

No changes since 2.13.0

2.13.0 (30-Sep-2021)

#131: Deserializing ZonedDateTime with basic TZ offset notation (0000)
 (contributed by Øystein H)
#212: Make LocalDateDeserializer consider strict/lenient on accepting (or not)
  of "time" part
#216: Deprecate method Jdk8Module.configureAbsentsAsNulls() (to be removed from Jackson 3)

2.12.7 (26-May-2022)
2.12.6 (15-Dec-2021)
2.12.5 (27-Aug-2021)

No changes since 2.12.4

2.12.4 (06-Jul-2021)

#214: readerForUpdating(objectToUpdate).readValue(json) behaves unexpectedly
   on Optional<List>
 (reported by jc84-dev@github)

2.12.3 (12-Apr-2021)

#207: Fail to serialize `TemporalAdjuster` type with 2.12
 (reported by Gökhan Ö)

2.12.2 (03-Mar-2021)

#202: Unable to deserialize `YearMonth` when running as java9 module,
  added with `@JsonDeserialize` annotation
 (reported by walkeros@github)
#206: `@JsonKey`is ignored with parameter-names module registered
 (reported by bertwin@github.com)

2.12.1 (08-Jan-2021)

#196: `@JsonFormat` overriden features don't apply when there are no other
   options while deserializing ZonedDateTime
 (reported, fix contributed by Maciej D)

2.12.0 (29-Nov-2020)

#94: Deserialization of timestamps with UTC timezone to LocalDateTime
   doesn't yield correct time
 (reported by Antti L)
#165: Problem in serializing negative Duration values
 (reported by Joni S)
#166: Cannot deserialize `OffsetDateTime.MIN` or `OffsetDateTime.MAX` with
   `ADJUST_DATES_TO_CONTEXT_TIME_ZONE` enabled
 (reported, fix suggested by Moritz O)
#175: ObjectMapper#setTimeZone ignored by jsr-310/datetime types during serialization
  when using `@JsonFormat` annotation
 (reported by Erwan L; fix contributed by Ferenc C)
#184: `DurationDeserializer` should use `@JsonFormat.pattern` (and config override)
  to support configurable `ChronoUnit`
 (requested by Philipp D, fix contributed by Oriol B)
#189: Support use of "pattern" (`ChronoUnit`) for `DurationSerializer` too
 (contributed bvy Oriol B)
- Add Gradle Module Metadata (https://blog.gradle.org/alignment-with-gradle-module-metadata)

2.11.4 (12-Dec-2020)
2.11.3 (02-Oct-2020)
2.11.2 (02-Aug-2020)
2.11.1 (25-Jun-2020)

No changes since 2.11.0

2.11.0 (26-Apr-2020)

#58: (datetime) Should not parse `LocalDate`s from number (timestamp), or at least
  should have an option preventing
 (reported by Bill O'N, fixed by Mike [kupci@github])
#128: (datetime) Timestamp keys from `ZonedDateTime`
 (reported by Michał Ż, fixed by Vetle L-R)
#138: (datetime) Prevent deserialization of "" as `null` for `Duration`, `Instant`,
  `LocalTime`, `OffsetTime` and `YearMonth` in "strict" (non-lenient) mode
 (contributed by Mike [kupci@github])
#148: (datetime) Allow strict `LocalDate` parsing
 (requested by by Arturas G, fix contributed by Samantha W)
- (datetime) Add explicit `ZoneId` serializer to force use of `ZoneId` as Type Id, and
  not inaccessible subtype (`ZoneRegion`): this to avoid JDK9+ Module Access problem

2.10.5 (21-Jul-2020)
2.10.4 (03-May-2020)
2.10.3 (03-Mar-2020)
2.10.2 (05-Jan-2020)

No changes since 2.10.1

2.10.1 (09-Nov-2019)

#127: ZonedDateTime in map keys ignores option to write Zone IDs
 (reported by Michał Ż, fixed by Vetle L-R)

2.10.0 (26-Sep-2019)

#51: `YearKeyDeserializer` doesn't work with non-padded year values
 (reported by sladkoff@github; fix contributed by Mike [kupci@github])
#69: `ZonedDateTime` for times before the epoch do not serialize correctly
 (fixed by Mike [kupci@github])
#75: (datetime) Use `SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS` for configuring
 `Duration` serialization
 (suggested by Kezhu W)
#80: Feature request: Support case-insensitive `LocalDate` formats
   (MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES)
 (contributed by Craig P)
#82: (datetime) Typo in YearMothKeyDeserializer class name
#105: `LocalTime` should generate "time" schema instead of "date-time"
 (suggested by jaisonpjohn@github)
#114: Prevent deserialization of "" as `null` for `LocalDate`, `LocalDateTime`
  in "strict" (non-lenient) mode
 (suggested by beytun@github, implemented by Mike [kupci@github])
#121: Array representation of `MonthDay` can not be deserialized
- Add JDK9+ `module-info.class` with Moditect plugin
#126: Change auto-registration in 2.10 to provide "new" (JavaTimeModule) instead of legacy module
#129: Support `lenient` setting with `LocalDateDeserializer`
 (suggested by esHack@github)

2.9.10 (21-Sep-2019)
2.9.9 (16-May-2019)

No changes since 2.9.8

2.9.8 (15-Dec-2018)

#90 (datetime): Performance issue with malicious `BigDecimal` input,
   `InstantDeserializer`, `DurationDeserializer` (CVE-2018-1000873)
 (reported by Andriy P, fix contributed by Todd J)

2.9.7 (19-Sep-2018)

#78: Year deserialization ignores `@JsonFormat` pattern
 (reported, fixed by Adrian P)

2.9.6 (12-Jun-2018)

#65: Use `DeserializationContext.handleWeirdXxxValue()` for datetime deserializers
 (contributed by Semyon L)
#67: `ParameterNamesModule` does not deserialize with a single parameter
   constructor when using `SnakeCase` `PropertyNamingStrategy`
 (reported by Sonny G)

2.9.5 (26-Mar-2018)

#98: `OffsetDateTime` with `@JsonFormat(without=...)` doesn't seem to work
 (reported by ayush-veem@github)

2.9.4 (24-Jan-2018)

No changes since 2.9.3

2.9.3 (09-Dec-2017)

#46: Double array serialization of `LocalDate` stored as an object with
  wrapper object typing enabled
 (reported by unintended@github)
- Improve error reporting for `LocalDateTime`, `LocalTime`, `OffsetTime` for
  timestamp input (JSON integer value)

2.9.2 (14-Oct-2017)
2.9.1 (07-Sep-2017)

No changes since 2.9.0

2.9.0 (30-Jul-2017)

#3: (datatype) Add Serialization Support for Streams
 (contributed by Julien B)
#20: (datetime) Allow `LocalDate` to be serialized/deserialized as number (epoch day)
 (contributed by João C)
#21: (datetime) `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS` not respected
 (contributed by JP Moresmau)

2.8.10 (not yet released)

#33: `Jdk8Serializer.findReferenceSerializer()` leads to `StackOverflowError` in 2.8.9
 (reported by Mikko T)

2.8.9 (12-Jun-2017)

No changes since 2.8.8

2.8.8 (05-Apr-2017)

#13: (datatype) `configureAbsentsAsNulls` not working for primitive optionals
  like `OptionalInt`
 (reported by Louis-Rémi P)
#15: (datatype) Optional<Long> and OptionalLong deserialization are not consistent
  when deserializing from String
 (reported by Louis-Rémi P)
#17: (datatype) Cached `Optional` serializer does not apply annotations for POJO properties
 (reported by codicusmaximus@github)
#18: (datetime) `InstantDeserializer` is not working with offset of zero `+00:00` and `+00`
 (contributed by kevinjom@github)

2.8.7 (21-Feb-2017)
2.8.6 (12-Jan-2017)

No changes since 2.8.5

2.8.5 (14-Nov-2016)

The very first release from this repository!

(note: issue from https://github.com/FasterXML/jackson-datatype-jsr310/)

#89: If the JsonFormat pattern is all numeric, the `InstantDeserializer` will
  do the wrong thing
 (reported, contributed fix, by ubik2@github)
