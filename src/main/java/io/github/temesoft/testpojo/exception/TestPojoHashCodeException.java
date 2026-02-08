package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

public class TestPojoHashCodeException extends RuntimeException {
    public TestPojoHashCodeException(final Method hashCodeMethod, final String message) {
        super("HashCode method assertion error:"
                + "\n\tError: " + message
                + "\n\tMethod: " + hashCodeMethod);
    }
}