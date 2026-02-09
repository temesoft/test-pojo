package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Exception thrown when a raw use of parameterized class is detected by the test-pojo framework.
 */
public class TestPojoRawUseException extends RuntimeException {
    public TestPojoRawUseException(final Method method,
                                   final Class<?> parameterizedClass) {
        super(
                "Raw use assertion error:"
                        + "\n\tError: Raw use of parameterized class: " + parameterizedClass.getName()
                        + "\n\tMethod: " + method
        );
    }

    public TestPojoRawUseException(final Constructor<?> constructor,
                                   final Class<?> parameterizedClass) {
        super(
                "Raw use assertion error:"
                        + "\n\tError: Raw use of parameterized class: " + parameterizedClass.getName()
                        + "\n\tConstructor: " + constructor
        );
    }
}