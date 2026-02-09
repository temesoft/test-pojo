package io.github.temesoft.testpojo.model;

public class Pojo1 {
    private String key;
    private Double value;
    private boolean ready;

    public String printValue() {
        return key + "=" + value;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(final boolean ready) {
        this.ready = ready;
    }

    public void setValue(final Double value) {
        this.value = value;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public Double getValue() {
        return value;
    }
}