package com.fasterxml.jackson.module.paramnames;

/**
 * @author Lovro Pandzic
 */
class ImmutableBean
{
    private final String name;
    private final Integer value;

    // needed because names are implicit (as of Jackson 2.4), not explicit
    // 14-Oct-2020, tatu: As of 2.12, no longer needed (was fixed earlier)
//    @JsonCreator
    public ImmutableBean(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ImmutableBean that = (ImmutableBean) o;
        return name.equals(that.name) && value.equals(that.value);
    }

    @Override
    public String toString() {
        return "ImmutableBean{name='" + name + "', value=" + value +'}';
    }
}
