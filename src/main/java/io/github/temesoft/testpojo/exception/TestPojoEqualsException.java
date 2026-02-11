package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

/**
 * Exception thrown when an {@code equals()} method test fails in the test-pojo framework.
 * <p>
 * This exception is thrown by {@link io.github.temesoft.testpojo.TestPojoEqualsAndHashCode}
 * when an {@code equals()} method implementation violates one of the standard contracts
 * defined in {@link Object#equals(Object)}. The exception provides detailed information
 * about which method failed and the specific contract violation that occurred.
 * </p>
 * <p>
 * The exception message includes:
 * </p>
 * <ul>
 *   <li>A descriptive error message explaining which contract was violated</li>
 *   <li>The full signature of the {@code equals()} method that failed</li>
 * </ul>
 *
 * <h3>Common causes:</h3>
 * <ul>
 *   <li>Returning {@code true} when compared with {@code null}</li>
 *   <li>Returning {@code true} when compared with an object of a different type</li>
 *   <li>Two randomly generated objects incorrectly comparing as equal</li>
 *   <li>An object not comparing as equal to itself (violating reflexivity)</li>
 * </ul>
 */
public class TestPojoEqualsException extends RuntimeException {

    /**
     * Constructs a new {@code TestPojoEqualsException} with detailed information about
     * the {@code equals()} method failure.
     * <p>
     * The exception message is formatted with the following structure:
     * </p>
     * <pre>
     * Equals method assertion error:
     *     Error: [error message]
     *     Method: [equals() method signature]
     * </pre>
     *
     * @param equalsMethod the {@code equals()} method that failed during testing, must not be {@code null}
     * @param message      a descriptive error message explaining the contract violation, must not be {@code null}
     */
    public TestPojoEqualsException(final Method equalsMethod, final String message) {
        super(
                "Equals method assertion error:"
                        + "\n\tError: " + message
                        + "\n\tMethod: " + equalsMethod
        );
    }
}