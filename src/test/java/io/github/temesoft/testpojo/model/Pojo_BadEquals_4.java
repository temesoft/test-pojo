package io.github.temesoft.testpojo.model;

import java.util.Objects;

@SuppressWarnings("EqualsGetClass")
public class Pojo_BadEquals_4 {
    private String value;

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Pojo_BadEquals_4 that = (Pojo_BadEquals_4) o;
        if (!value.equals(that.value)) {
            return false;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}