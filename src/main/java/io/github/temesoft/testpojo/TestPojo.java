package io.github.temesoft.testpojo;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Main entry point for testing POJOs (Plain Old Java Objects).
 * <p>
 * This class provides a fluent API for testing various aspects of POJOs including:
 * <ul>
 *   <li>Getters and setters</li>
 *   <li>equals() and hashCode() contracts</li>
 *   <li>toString() consistency</li>
 *   <li>Constructor invocation</li>
 *   <li>Random instantiation</li>
 * </ul>
 *
 * <h2>Usage Examples:</h2>
 *
 * <h3>Testing a single class:</h3>
 * <pre>{@code
 * TestPojo.processClass(MyPojo.class)
 *     .testSettersGetters()
 *     .testEqualsAndHashCode()
 *     .testToString();
 * }</pre>
 *
 * <h3>Testing multiple classes:</h3>
 * <pre>{@code
 * TestPojo.processClass(User.class, Product.class, Order.class)
 *     .testAll(); // includes setters/getters, equals/hashCode, constructor, and random instantiation
 * }</pre>
 *
 * <h3>Testing an entire package:</h3>
 * <pre>{@code
 * TestPojo.processPackage("com.mycompany.model")
 *     .excludeMethodsContaining("getClass")
 *     .testSettersGetters()
 *     .testEqualsAndHashCode();
 * }</pre>
 */
public class TestPojo {

    final Class<?>[] clazz;
    final String packageName;
    private Collection<String> excludeMethods;

    private TestPojo(final Class<?>[] clazz, final String packageName) {
        this.clazz = clazz;
        this.packageName = packageName;
    }

    /**
     * Creates a TestPojo instance for testing specific class(es).
     *
     * @param clazz one or more classes to test
     * @return a new TestPojo instance
     * @throws IllegalArgumentException if no classes are provided
     */
    public static TestPojo processClass(final Class<?>... clazz) {
        return new TestPojo(clazz, null);
    }

    /**
     * Creates a TestPojo instance for testing all classes in a package.
     *
     * @param packageName the fully qualified package name (e.g., "com.example.model")
     * @return a new TestPojo instance
     * @throws IllegalArgumentException if packageName is null or empty
     */
    public static TestPojo processPackage(final String packageName) {
        return new TestPojo(null, packageName);
    }

    /**
     * Excludes methods containing any of the specified strings from testing.
     * <p>
     * The exclusion is performed using a substring match on the method's full signature.
     * </p>
     *
     * @param excludeMethods collection of strings to match against method signatures
     * @return this TestPojo instance for method chaining
     */
    public TestPojo excludeMethodsContaining(final Collection<String> excludeMethods) {
        this.excludeMethods = excludeMethods;
        return this;
    }

    /**
     * Excludes methods containing any of the specified strings from testing.
     *
     * @param excludeMethodsContaining varargs of strings to match against method signatures
     * @return this TestPojo instance for method chaining
     */
    public TestPojo excludeMethodsContaining(final String... excludeMethodsContaining) {
        this.excludeMethods = List.of(excludeMethodsContaining);
        return this;
    }

    /**
     * Excludes methods containing the specified string from testing.
     *
     * @param excludeMethod string to match against method signatures
     * @return this TestPojo instance for method chaining
     */
    public TestPojo excludeMethodContaining(final String excludeMethod) {
        this.excludeMethods = List.of(excludeMethod);
        return this;
    }

    /**
     * Convenience method to run all available tests.
     * <p>
     * This is equivalent to calling:
     * <pre>{@code
     *   .testRandom()
     *   .testConstructor()
     *   .testSettersGetters()
     *   .testEqualsAndHashCode()
     *   .testToString();
     * }</pre>
     *
     * @return this TestPojo instance for method chaining
     */
    public TestPojo testAll() {
        return this.testRandom()
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString()
                .testConstructor();
    }

    /**
     * Tests that classes can be instantiated with random field values and verify execution of all
     * public methods (including ones taking arguments)
     * <p>
     * This test uses Instancio to create instances with random data for all fields.
     * </p>
     *
     * @return this TestPojo instance for method chaining
     */
    public TestPojo testRandom() {
        processClasses(aClass -> new TestPojoRandom(aClass, excludeMethods).testClass());
        return this;
    }

    /**
     * Tests all getter and setter method pairs.
     * <p>
     * For each field, this test:
     * <ol>
     *   <li>Finds the corresponding setter method</li>
     *   <li>Sets a random value using the setter</li>
     *   <li>Retrieves the value using the getter</li>
     *   <li>Verifies the retrieved value equals the set value</li>
     * </ol>
     *
     * @return this TestPojo instance for method chaining
     */
    public TestPojo testSettersGetters() {
        processClasses(aClass -> new TestPojoSetterGetter(aClass, excludeMethods).testClass());
        return this;
    }

    /**
     * Tests equals() and hashCode() contract compliance.
     * <p>
     * For equals(), verifies:
     * <ul>
     *   <li>null comparison returns false</li>
     *   <li>Comparison with different type returns false</li>
     *   <li>Two different objects are not equal</li>
     *   <li>Object equals itself (reflexivity)</li>
     * </ul>
     * For hashCode(), verifies:
     * <ul>
     *   <li>Two different objects have different hash codes</li>
     * </ul>
     * Note: Hash collisions are theoretically possible but extremely rare with random data.
     *
     * @return this TestPojo instance for method chaining
     */
    public TestPojo testEqualsAndHashCode() {
        processClasses(aClass -> new TestPojoEqualsAndHashCode(aClass, excludeMethods).testClass());
        return this;
    }

    /**
     * Tests toString() method consistency.
     * <p>
     * Verifies that calling toString() multiple times on the same unchanged object
     * returns the same result each time.
     * </p>
     *
     * @return this TestPojo instance for method chaining
     */
    public TestPojo testToString() {
        processClasses(aClass -> new TestPojoToString(aClass, excludeMethods).testClass());
        return this;
    }

    /**
     * Tests all public constructors.
     * For each public constructor, this test:
     * <ol>
     *   <li>Generates random values for all parameters</li>
     *   <li>Invokes the constructor</li>
     *   <li>Verifies no exceptions are thrown</li>
     * </ol>
     *
     * @return this TestPojo instance for method chaining
     */
    public TestPojo testConstructor() {
        processClasses(aClass -> new TestPojoConstructor(aClass, excludeMethods).testClass());
        return this;
    }

    /**
     * Private helper method to process classes with the given test function.
     * <p>
     * This method reduces code duplication by centralizing the logic for
     * iterating over classes (either explicitly provided or discovered via package scanning).
     * </p>
     *
     * @param testFunction the function to apply to each class
     */
    private void processClasses(java.util.function.Consumer<Class<?>> testFunction) {
        if (clazz != null) {
            for (final Class<?> aClass : clazz) {
                testFunction.accept(aClass);
            }
        } else if (packageName != null) {
            final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader(packageName);
            classes.forEach(testFunction);
        }
    }
}
