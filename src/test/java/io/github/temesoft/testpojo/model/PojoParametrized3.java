package io.github.temesoft.testpojo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@SuppressWarnings("unused")
public class PojoParametrized3<T> extends PojoParametrized1<String> {

    private T name;

}
