package io.github.temesoft.testpojo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@SuppressWarnings("unused")
public class PojoExtendingAbstractBase extends PojoAbstractBase {

    private String name;

}
