package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Constructor;

/**
 * Exception thrown when a constructor test failure is detected by the test-pojo framework.
 */
public class TestPojoConstructorException extends RuntimeException {
    public TestPojoConstructorException(final Constructor<?> constructor, final String message) {
        super(
                "Constructor assertion error:"
                        + "\n\tError: " + message
                        + "\n\tConstructor: " + constructor
        );
    }
}