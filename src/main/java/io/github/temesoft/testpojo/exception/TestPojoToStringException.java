package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

/**
 * Exception thrown when a {@code toString()} method test fails in the test-pojo framework.
 * <p>
 * This exception is thrown by {@link io.github.temesoft.testpojo.TestPojoToString}
 * when a {@code toString()} method violates the consistency contract. Specifically, it's
 * thrown when calling {@code toString()} multiple times on the same unchanged object
 * produces different string representations. A proper {@code toString()} implementation
 * should be deterministic and return the same result when the object state hasn't changed.
 * </p>
 * <p>
 * The exception message includes:
 * </p>
 * <ul>
 *   <li>A descriptive error message about the consistency violation</li>
 *   <li>The full signature of the {@code toString()} method that failed</li>
 * </ul>
 *
 * <strong>Common causes:</strong>
 * <ul>
 *   <li>Including timestamps or current time in the string representation</li>
 *   <li>Including random values or UUIDs generated on each call</li>
 *   <li>Including thread IDs or other non-deterministic data</li>
 *   <li>Side effects that modify object state during toString() execution</li>
 *   <li>Dependency on external mutable state</li>
 * </ul>
 *
 * <strong>Example of problematic code:</strong>
 * <pre>{@code
 * public String toString() {
 *     return "User{name='" + name +
 *            "', timestamp=" + System.currentTimeMillis() + "}";  // Non-deterministic!
 * }
 * }</pre>
 *
 * <strong>Example of correct code:</strong>
 * <pre>{@code
 * public String toString() {
 *     return "User{name='" + name +
 *            "', email='" + email + "'}";  // Only uses object's fields
 * }
 * }</pre>
 */
public class TestPojoToStringException extends RuntimeException {

    /**
     * Constructs a new {@code TestPojoToStringException} with detailed information about
     * the {@code toString()} consistency failure.
     * <p>
     * The exception message is formatted with the following structure:
     * </p>
     * <pre>
     * ToString method assertion error:
     *     Error: [error message]
     *     Method: [toString() method signature]
     * </pre>
     *
     * @param toStringMethod the {@code toString()} method that failed during testing, must not be {@code null}
     * @param message        a descriptive error message explaining the consistency violation, must not be {@code null}
     */
    public TestPojoToStringException(final Method toStringMethod, final String message) {
        super(
                "ToString method assertion error:"
                        + "\n\tError: " + message
                        + "\n\tMethod: " + toStringMethod
        );
    }
}