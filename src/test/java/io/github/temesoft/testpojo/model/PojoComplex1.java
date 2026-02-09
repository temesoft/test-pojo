package io.github.temesoft.testpojo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@SuppressWarnings("unused")
public class PojoComplex1 {
    private String key;
    private Double value;
    private boolean ready;
    private List<String> tokens;
    private Map<String, Object> headers;

    public static String getSomething() {
        return "testing";
    }
}