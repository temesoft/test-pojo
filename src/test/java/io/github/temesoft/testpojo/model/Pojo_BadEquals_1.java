package io.github.temesoft.testpojo.model;

import java.util.Objects;

public class Pojo_BadEquals_1 {
    private String value;

    @Override
    public boolean equals(final Object o) {
        return o == null;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}