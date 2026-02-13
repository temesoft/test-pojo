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
public class PojoComplex {
    private String key;
    private Double value;
    private boolean ready;
    private List<String> tokens;
    private Map<String, Object> headers;
    private Pojo1 pojo1;
    private Map<String, Pojo1> mapOfPojo;
    private List<WidgetsEnum> widgetsEnumList;
    private InnerSimpleEnum innerSimpleEnum;
    private InnerComplexEnum innerComplexEnum;
    private Map<InnerComplexEnum, String> innerComplexEnumStringMap;
    private SomeInterface someItem1;
    private PojoExtendingAbstractBase someItem2;

    public static String getSomething() {
        return "testing";
    }

    enum InnerSimpleEnum {
        V1, V2, V3
    }

    enum InnerComplexEnum {
        V1("V 1"),
        V2("V 2"),
        V3("V 3");
        private final String description;

        InnerComplexEnum(final String description) {
            this.description = description;
        }
    }
}