package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

/**
 * Exception thrown when a setter/getter method pair test fails in the test-pojo framework.
 * <p>
 * This exception is thrown by {@link io.github.temesoft.testpojo.TestPojoSetterGetter}
 * when a value set via a setter method cannot be correctly retrieved via its corresponding
 * getter method. This indicates that either the setter is not properly storing the value,
 * the getter is not properly retrieving it, or there's a transformation/validation issue
 * between the two methods.
 * </p>
 * <p>
 * The exception message includes:
 * </p>
 * <ul>
 *   <li>Description of the mismatch</li>
 *   <li>The setter method signature</li>
 *   <li>The getter method signature</li>
 *   <li>The expected value (what was set)</li>
 *   <li>The actual value (what was retrieved)</li>
 * </ul>
 *
 * <strong>Common causes:</strong>
 * <ul>
 *   <li>Setter assigns to wrong field</li>
 *   <li>Getter returns wrong field</li>
 *   <li>Value transformation in setter or getter that breaks equality</li>
 *   <li>Setter or getter has side effects that modify the value</li>
 *   <li>Getter returns null when a non-null value was set</li>
 *   <li>Incorrect {@code equals()} implementation on the field type</li>
 * </ul>
 *
 * <strong>Example of problematic code:</strong>
 * <pre>{@code
 * private String name;
 * private String description;
 *
 * public void setName(String name) {
 *     this.description = name;  // Wrong field!
 * }
 *
 * public String getName() {
 *     return name;  // Returns null
 * }
 * }</pre>
 */
public class TestPojoSetterGetterException extends RuntimeException {

    /**
     * Constructs a new {@code TestPojoSetterGetterException} with detailed information about
     * the setter/getter pair failure.
     * <p>
     * The exception message is formatted with the following structure:
     * </p>
     * <pre>
     * Setter/Getter assertion error:
     *     Error: Getter return value does not correspond to Setter argument used
     *     Setter method: [setter signature]
     *     Getter method: [getter signature]
     *     Expected result: [value that was set]
     *     Actual result: [value that was retrieved]
     * </pre>
     *
     * @param setterMethod   the setter method that was invoked, must not be {@code null}
     * @param getterMethod   the getter method that was invoked, must not be {@code null}
     * @param expectedResult the value that was passed to the setter, may be {@code null}
     * @param actualResult   the value that was returned by the getter, may be {@code null}
     */
    public TestPojoSetterGetterException(final Method setterMethod,
                                         final Method getterMethod,
                                         final Object expectedResult,
                                         final Object actualResult) {
        super(
                "Setter/Getter assertion error:"
                        + "\n\tError: Getter return value does not correspond to Setter argument used"
                        + "\n\tSetter method: " + setterMethod
                        + "\n\tGetter method: " + getterMethod
                        + "\n\tExpected result: " + expectedResult
                        + "\n\tActual result: " + actualResult
        );
    }
}
