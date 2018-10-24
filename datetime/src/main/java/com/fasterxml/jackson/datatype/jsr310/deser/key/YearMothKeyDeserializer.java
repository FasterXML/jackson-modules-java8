package com.fasterxml.jackson.datatype.jsr310.deser.key;

/**
 * @deprecated Due to typo in class name use {@link YearMonthKeyDeserializer} instead.
 */
@Deprecated // since 2.10
public class YearMothKeyDeserializer extends YearMonthKeyDeserializer {
    @SuppressWarnings("hiding")
    public static final YearMothKeyDeserializer INSTANCE = new YearMothKeyDeserializer();

    private YearMothKeyDeserializer() {
        // singleton
    }
}
