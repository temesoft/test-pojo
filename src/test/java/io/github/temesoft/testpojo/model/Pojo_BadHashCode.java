package io.github.temesoft.testpojo.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Pojo_BadHashCode {
    private String value;

    @Override
    public boolean equals(final Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        final Pojo_BadHashCode that = (Pojo_BadHashCode) object;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return 123456;
    }
}
