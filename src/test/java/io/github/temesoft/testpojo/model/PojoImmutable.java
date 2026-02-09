package io.github.temesoft.testpojo.model;

public class PojoImmutable {
    private final String key;
    private final Double value;
    private final boolean ready;

    public PojoImmutable(final String key, final Double value, final boolean ready) {
        this.key = key;
        this.value = value;
        this.ready = ready;
    }

    public String printValue() {
        return key + "=" + value;
    }

    public boolean isReady() {
        return ready;
    }

    public String getKey() {
        return key;
    }

    public Double getValue() {
        return value;
    }
}