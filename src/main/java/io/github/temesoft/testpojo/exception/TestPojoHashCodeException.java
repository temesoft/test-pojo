package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

/**
 * Exception thrown when a hashCode() test failure is detected by the test-pojo framework.
 */
public class TestPojoHashCodeException extends RuntimeException {
    public TestPojoHashCodeException(final Method hashCodeMethod, final String message) {
        super(
                "HashCode method assertion error:"
                        + "\n\tError: " + message
                        + "\n\tMethod: " + hashCodeMethod
        );
    }
}