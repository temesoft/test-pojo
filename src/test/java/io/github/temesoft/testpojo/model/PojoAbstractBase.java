package io.github.temesoft.testpojo.model;

import lombok.Data;

import java.util.UUID;

@Data
@SuppressWarnings("unused")
public abstract class PojoAbstractBase {
    private UUID id;
}
