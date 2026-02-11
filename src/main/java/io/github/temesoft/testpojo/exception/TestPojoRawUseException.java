package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Exception thrown when raw use of a parameterized type is detected by the test-pojo framework.
 * <p>
 * This exception is thrown when a method or constructor parameter uses a parameterized type
 * (such as {@link java.util.Collection}, {@link java.util.List}, or {@link java.util.Map})
 * without specifying generic type parameters. Raw types prevent the test framework from
 * generating properly typed test data, as it cannot determine what element types should be
 * used for collections or what key/value types should be used for maps.
 * </p>
 * <p>
 * The exception message includes:
 * </p>
 * <ul>
 *   <li>Information about the raw type that was detected</li>
 *   <li>The method or constructor signature where the raw type was found</li>
 * </ul>
 *
 * <strong>Examples of raw type usage (bad):</strong>
 * <pre>{@code
 * public void setItems(List items) { ... }           // Raw List
 * public void setMap(Map map) { ... }                // Raw Map
 * public MyClass(Collection collection) { ... }      // Raw Collection in constructor
 * }</pre>
 *
 * <strong>Examples of properly parameterized types (good):</strong>
 * <pre>{@code
 * public void setItems(List<String> items) { ... }        // Parameterized List
 * public void setMap(Map<String, Integer> map) { ... }    // Parameterized Map
 * public MyClass(Collection<User> collection) { ... }     // Parameterized Collection
 * }</pre>
 */
public class TestPojoRawUseException extends RuntimeException {

    /**
     * Constructs a new {@code TestPojoRawUseException} for a method with raw type usage.
     * <p>
     * The exception message is formatted with the following structure:
     * </p>
     * <pre>
     * Raw use assertion error:
     *     Error: Raw use of parameterized class: [class name]
     *     Method: [method signature]
     * </pre>
     *
     * @param method             the method containing the raw type parameter, must not be {@code null}
     * @param parameterizedClass the parameterized class that was used without type parameters
     *                           (e.g., List.class, Map.class), must not be {@code null}
     */
    public TestPojoRawUseException(final Method method,
                                   final Class<?> parameterizedClass) {
        super(
                "Raw use assertion error:"
                        + "\n\tError: Raw use of parameterized class: " + parameterizedClass.getName()
                        + "\n\tMethod: " + method
        );
    }

    /**
     * Constructs a new {@code TestPojoRawUseException} for a constructor with raw type usage.
     * <p>
     * The exception message is formatted with the following structure:
     * </p>
     * <pre>
     * Raw use assertion error:
     *     Error: Raw use of parameterized class: [class name]
     *     Constructor: [constructor signature]
     * </pre>
     *
     * @param constructor        the constructor containing the raw type parameter, must not be {@code null}
     * @param parameterizedClass the parameterized class that was used without type parameters
     *                           (e.g., List.class, Map.class), must not be {@code null}
     */
    public TestPojoRawUseException(final Constructor<?> constructor,
                                   final Class<?> parameterizedClass) {
        super(
                "Raw use assertion error:"
                        + "\n\tError: Raw use of parameterized class: " + parameterizedClass.getName()
                        + "\n\tConstructor: " + constructor
        );
    }
}