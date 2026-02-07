package com.temesoft.test.exception;

import java.lang.reflect.Constructor;

public class TestPojoConstructorException extends RuntimeException {
    public TestPojoConstructorException(final Constructor<?> constructor, final String message) {
        super("Constructor assertion error:"
                + "\n\tError: " + message
                + "\n\tConstructor: " + constructor);
    }
}