package io.github.temesoft.testpojo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
@Builder
@SuppressWarnings("unused")
public class PojoComplex1 {
    private String key;
    private Double value;
    private boolean ready;
    private List<String> tokens;
    private Map<String, Object> headers;
    private Pojo1 pojo1;
    private Map<String, Pojo1> mapOfPojo;

    public static String getSomething() {
        return "testing";
    }
}