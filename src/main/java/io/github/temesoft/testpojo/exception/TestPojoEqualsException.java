package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

/**
 * Exception thrown when an equals() test failure is detected by the test-pojo framework.
 */
public class TestPojoEqualsException extends RuntimeException {
    public TestPojoEqualsException(final Method equalsMethod, final String message) {
        super(
                "Equals method assertion error:"
                        + "\n\tError: " + message
                        + "\n\tMethod: " + equalsMethod
        );
    }
}