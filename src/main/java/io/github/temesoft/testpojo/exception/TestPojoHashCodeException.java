package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

/**
 * Exception thrown when a {@code hashCode()} method test fails in the test-pojo framework.
 * <p>
 * This exception is thrown by {@link io.github.temesoft.testpojo.TestPojoEqualsAndHashCode}
 * when a {@code hashCode()} method implementation violates expected consistency requirements.
 * Specifically, it's thrown when two objects with different random data produce the same hash
 * code, which while theoretically possible due to hash collisions, should be extremely rare
 * with properly implemented hash functions and random test data.
 * </p>
 * <p>
 * The exception message includes:
 * </p>
 * <ul>
 *   <li>A descriptive error message explaining the hash code collision</li>
 *   <li>The full signature of the {@code hashCode()} method that failed</li>
 * </ul>
 *
 * <h3>Common causes:</h3>
 * <ul>
 *   <li>Poor {@code hashCode()} implementation that doesn't incorporate all relevant fields</li>
 *   <li>Constant hash code value being returned</li>
 *   <li>Hash function that produces many collisions</li>
 *   <li>Legitimate but rare hash collision (very uncommon with random data)</li>
 * </ul>
 */
public class TestPojoHashCodeException extends RuntimeException {

    /**
     * Constructs a new {@code TestPojoHashCodeException} with detailed information about
     * the {@code hashCode()} method failure.
     * <p>
     * The exception message is formatted with the following structure:
     * </p>
     * <pre>
     * HashCode method assertion error:
     *     Error: [error message]
     *     Method: [hashCode() method signature]
     * </pre>
     *
     * @param hashCodeMethod the {@code hashCode()} method that failed during testing, must not be {@code null}
     * @param message        a descriptive error message explaining the failure, must not be {@code null}
     */
    public TestPojoHashCodeException(final Method hashCodeMethod, final String message) {
        super(
                "HashCode method assertion error:"
                        + "\n\tError: " + message
                        + "\n\tMethod: " + hashCodeMethod
        );
    }
}