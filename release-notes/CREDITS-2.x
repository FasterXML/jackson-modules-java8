Here are people who have contributed to the development of Jackson JSON processor
Java 8 module
(version numbers in brackets indicate release in which the problem was fixed)

Nick Williams (beamerblvd@github): author of Java 8 date/time module
Tatu Saloranta (cowtowncoder@github): author (other modules)
Michael O'Keeffe (kupci@github): co-author (since 2.10)

Louis-Rémi Paquet (lrpg@github)
  #15: Optional<Long> and OptionalLong deserialization not consistent when
     deserializing from String
   (2.8.8)

* Reported #15: Optional<Long> and OptionalLong deserialization
  not consistent when deserializing from String
   (2.8.8)

Mikko Tiihonen (gmokki@github)
* Reported #33: `Jdk8Serializer.findReferenceSerializer()` leads to `StackOverflowError`
  in 2.8.9
   (2.8.10)

João Cabrita (kewne@github)
  #20: Allow `LocalDate` to be serialized/deserialized as number (epoch day)
   (2.9.0)

KP Moresmau (JPMoresmau@github)
  #21: `DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS` not respected
   (2.9.0)

Julien Bouyoud (jBouyoud@github)
  #3: (datatype) Add Serialization Support for Streams
   (2.9.0)

Semyon Levin (remal@github)
  #65: Use `DeserializationContext.handleWeirdXxxValue()` for datetime deserializers
   (2.9.6)

Sonny Gill (sonnygill@github)
  #67: `ParameterNamesModule` does not deserialize with a single parameter
    constructor when using `SnakeCase` `PropertyNamingStrategy`
   (2.9.6)

Kezhu Wang (kezhuw@github)
  #75: Use `SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS` for configuring
    `Duration` serialization
   (2.10.0)

Adrian Palanques (devdevx@github)
  * Reported #78: (datetime) Year deserialization ignores `@JsonFormat` pattern
   (2.9.7)

Craig Pardey (pards@github)
  * Contributed #80: Feature request: Support case-insensitive `LocalDate` formats
    (MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES)
   (2.10.0)

Andriy Plokhotnyuk (plokhotnyuk@github)
  * Reported #90 (datetime): Performance issue with malicious `BigDecimal` input,
   `InstantDeserializer`, `DurationDeserializer`
   (2.9.8)

Todd Jonker (toddjonker@github)
  * Contributed fix for #90 (see above)
   (2.9.8)

Michael O'Keeffe (kupci@github)
  * Contributed fix for #51: `YearKeyDeserializer` doesn't work with non-padded
    year values
   (2.10.0)
  * Contributed fix for #58: Should not parse `LocalDate`s from number (timestamp),
   (2.11.0)
  * Contributed fix for #69: `ZonedDateTime` for times before the epoch do not
    serialize correctly
   (2.10.0)
  * Constributed fix for #114: Prevent deserialization of "" as `null` for `LocalDate`,
    `LocalDateTime` in "strict" (non-lenient) mode
   (2.10.0)
  * Implemented #138: Prevent deserialization of "" as `null` for `Duration`, `Instant`,
    `LocalTime`, `OffsetTime` and `YearMonth` in "strict" (non-lenient) mode
   (2.11.0)

Michał Żmuda (zmumi@github)
  * Reported #127: ZonedDateTime in map keys ignores option to write Zone IDs
   (2.10.1)
  * Reported #128: Timestamp keys from `ZonedDateTime`
   (2.11.0)

Vetle Leinonen-Roeim (vetler@github)
  * Contributed fix for #127: ZonedDateTime in map keys ignores option to write Zone IDs
   (2.10.1)
  * Contributed fix for #128: Timestamp keys from `ZonedDateTime`
   (2.11.0)

Bill O'Neil (billoneil@github)
  * Reported #58: Should not parse `LocalDate`s from number (timestamp),
   (2.11.0)

Arturas Gusevas (agusevas@github)
  * Requested #148: Allow strict `LocalDate` parsing
   (2.11.0)

Samantha Williamson (samwill@github)
  * Contributed fix to #148: Allow strict `LocalDate` parsing
   (2.11.0)

Antti S. Lankila (alankila@github)
  * Reported #94: Deserialization of timestamps with UTC timezone to LocalDateTime
    doesn't yield correct time
   (2.12.0)

Joni Syri (jpsyri@github)
  * Reported #165: Problem in serializing negative Duration values
   (2.12.0)

Moritz Orth (morth@github.com)
  * Reported and suggested fix for #166: Cannot deserialize OffsetDateTime.MIN and
   OffsetDateTime.MAX with ADJUST_DATES_TO_CONTEXT_TIME_ZONE enabled
   (2.12.0)

Erwan Leroux (ErwanLeroux@github)
  * Reported #175: ObjectMapper#setTimeZone ignored by jsr-310/datetime
    types during serialization when using `@JsonFormat` annotation
   (2.12.0)

Ferenc Csaky (ferenc-csaky@github)
  * Contributed fix to #175: ObjectMapper#setTimeZone ignored by jsr-310/datetime
    types during serialization when using `@JsonFormat` annotation
   (2.12.0)

Philipp Dargel (chisui@github)
  * Requested #184: `DurationDeserializer` should use `@JsonFormat.pattern` (and
    config override) to support configurable `ChronoUnit`
   (2.12.0)

Oriol Barcelona (obarcelonap@github)
  * Contributed fix for #184: `DurationDeserializer` should use `@JsonFormat.pattern`
    (and config override) to support configurable `ChronoUnit`
   (2.12.0)
  * Contributed fix for #189: Support use of "pattern" (`ChronoUnit`) for
    `DurationSerializer` too
   (2.12.0)

Gökhan Öner (gokhanoner@github)
  * Reported #207: Fail to serialize `TemporalAdjuster` type with 2.12
   (2.12.3)

Øystein B. Huseby (oeystein@github)
  * Contributed #131: Deserializing ZonedDateTime with basic TZ offset notation (0000)
   (2.13.0)

Sam Kruglov (Sam-Kruglov@github)
  * Reported #224: `DurationSerializer` ignores format pattern if nano-second
    serialization enabled
  (2.14.0)

Maciej Dębowski (maciekdeb@github)
  * Contributed #240: `LocalDateDeserializer` should consider coercionConfig settings
   (2.14.0)
