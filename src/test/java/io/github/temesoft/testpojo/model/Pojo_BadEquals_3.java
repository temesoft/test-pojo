package io.github.temesoft.testpojo.model;

import java.util.Objects;

@SuppressWarnings("EqualsGetClass")
public class Pojo_BadEquals_3 {
    private String value;

    @Override
    public boolean equals(final Object o) {
        return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}