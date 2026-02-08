package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

public class TestPojoToStringException extends RuntimeException {
    public TestPojoToStringException(final Method toStringMethod, final String message) {
        super("ToString method assertion error:"
                + "\n\tError: " + message
                + "\n\tMethod: " + toStringMethod);
    }
}