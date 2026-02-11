package io.github.temesoft.testpojo.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@SuppressWarnings("unused")
public class PojoParametrized2<T extends Number> {

    private T value;
    private List<T> values;
    private Map<String, T> valuesMap1;
    private Map<T, String> valuesMap2;

    public String processParameter1(final T param) {
        return param.toString();
    }

    public int processParameter2(List<T> param) {
        return param.size();
    }

    public int processParameter3(Map<String, T> param) {
        return param.size();
    }

    public int processParameter4(Map<T, String> param) {
        return param.size();
    }

    public static <T> T getParameter(final T param, final Class<T> clazz) {
        if (!param.getClass().equals(clazz)) {
            throw new RuntimeException("Problem with param and class provided");
        }
        return param;
    }
}
