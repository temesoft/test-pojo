package io.github.temesoft.testpojo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
@Data
@Builder
@SuppressWarnings({"unused", "ComparableAndComparator"})
public class PojoComplex implements
        Comparable<PojoComplex>,
        Serializable,
        Comparator<PojoComplex>,
        Predicate<PojoComplex>,
        Function<PojoComplex, String> {

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

    public String processItems1(final Iterator<String> items) {
        return items.toString();
    }

    public String processItems2(final Iterator<Object> items) {
        return items.toString();
    }

    public String processItems3(final Iterator<?> items) {
        return items.toString();
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

    @Override
    public int compareTo(final PojoComplex o) {
        return this.key.compareTo(o.key);
    }

    @Override
    public int compare(final PojoComplex o1, final PojoComplex o2) {
        return o1.key.compareTo(o2.key);
    }

    @Override
    public boolean test(final PojoComplex pojoComplex) {
        return pojoComplex.key != null;
    }

    @Override
    public String apply(final PojoComplex pojoComplex) {
        return pojoComplex.toString();
    }
}