Here are people who have contributed to the development of Jackson JSON processor
Java 8 module
(version numbers in brackets indicate release in which the problem was fixed)

Nick Williams (beamerblvd@github): author of Java 8 date/time module
Tatu Saloranta (cowtowncoder@github): author (other modules)

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
  * Contributed fix for #69: `ZonedDateTime` for times before the epoch do not
    serialize correctly
   (2.10.0)
  * Constributed fix for #114: Prevent deserialization of "" as `null` for `LocalDate`,
    `LocalDateTime` in "strict" (non-lenient) mode
   (2.10.0)
