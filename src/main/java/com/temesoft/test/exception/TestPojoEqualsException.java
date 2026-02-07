package com.temesoft.test.exception;

import java.lang.reflect.Method;

public class TestPojoEqualsException extends RuntimeException {
    public TestPojoEqualsException(final Method equalsMethod, final String message) {
        super("Equals method assertion error:"
                + "\n\tError: " + message
                + "\n\tMethod: " + equalsMethod);
    }
}