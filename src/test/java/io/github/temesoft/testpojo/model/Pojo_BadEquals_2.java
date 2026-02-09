package io.github.temesoft.testpojo.model;

import java.util.Objects;

@SuppressWarnings("EqualsGetClass")
public class Pojo_BadEquals_2 {
    private String value;

    @Override
    public boolean equals(final Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return true;
        final Pojo_BadEquals_2 that = (Pojo_BadEquals_2) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}