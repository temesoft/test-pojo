package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.report.TestPojoReportService;
import io.github.temesoft.testpojo.report.TestPojoReportServiceImpl;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Main entry point for testing POJOs (Plain Old Java Objects).
 * <p>
 * This class provides a fluent API for testing various aspects of POJOs including:
 * </p>
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
 * <strong>Testing a single class:</strong>
 * <pre>{@code
 * TestPojo.processClass(MyPojo.class)
 *     .testSettersGetters()
 *     .testEqualsAndHashCode()
 *     .testToString();
 * }</pre>
 *
 * <strong>Testing multiple classes:</strong>
 * <pre>{@code
 * TestPojo.processClass(User.class, Product.class, Order.class)
 *     .testAll(); // includes setters/getters, equals/hashCode, constructor, and random instantiation
 * }</pre>
 *
 * <strong>Testing an entire package:</strong>
 * <pre>{@code
 * TestPojo.processPackage("com.mycompany.model")
 *     .excludeMethodsContaining("getClass")
 *     .testSettersGetters()
 *     .testEqualsAndHashCode();
 * }</pre>
 */
public class TestPojo {

    private final TestPojoReportService reportService = new TestPojoReportServiceImpl();

    private final Class<?>[] clazz;
    private final String packageName;

    private Collection<String> excludeMethods;
    private Collection<Class<?>> excludeClasses = Collections.emptyList();
    private Predicate<Method> methodPredicate;
    private Predicate<Class<?>> classPredicate;
    private Predicate<Constructor<?>> constructorPredicate;

    private TestPojo(final Class<?>[] clazz, final String packageName) {
        this.clazz = clazz;
        this.packageName = packageName;
        reportService.reset();
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
     * Processes a specific package to initiate testing, automatically excluding the calling class.
     * <p>
     * This method identifies the classes within the specified {@code packageName} for testing.
     * To prevent infinite recursion or redundant testing, it uses a {@link StackWalker}
     * to automatically identify and add the calling class to the {@code excludeClasses} list.
     * </p>
     *
     * @param packageName    the fully qualified name of the package to be scanned for test classes
     * @param excludeClasses optional varargs of classes that should be skipped during processing
     * @return a {@link TestPojo} initialized with the package name and a distinct list of
     * excluded classes, including the caller
     */
    public static TestPojo processPackage(final String packageName, final Class<?>... excludeClasses) {
        final TestPojo result = new TestPojo(null, packageName);
        final Class<?> caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames -> frames.skip(1)
                        .findFirst()
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .orElse(null));
        final List<Class<?>> excludes = new ArrayList<>();
        if (caller != null) {
            excludes.add(caller);
        }
        if (excludeClasses != null) {
            excludes.addAll(List.of(excludeClasses));
        }
        result.excludeClasses = excludes.stream().distinct().collect(Collectors.toList());
        return result;
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
     * Excludes a single class from testing.
     * <p>
     * Classes specified through this method will be completely skipped during the test execution.
     * This is useful when certain classes should not be tested, such as abstract classes,
     * interfaces, or classes with special instantiation requirements.
     * </p>
     * <p>
     * Note: Calling this method replaces any previously configured class exclusions.
     * </p>
     *
     * @param classesToExclude the class to exclude from testing, must not be {@code null}
     * @return this {@code TestPojo} instance for method chaining
     */
    public TestPojo excludeClasses(final Class<?>... classesToExclude) {
        excludeClasses = List.of(classesToExclude);
        return this;
    }

    /**
     * Excludes multiple classes from testing.
     * <p>
     * Classes specified through this method will be completely skipped during the test execution.
     * This is useful when certain classes should not be tested, such as abstract classes,
     * interfaces, or classes with special instantiation requirements.
     * </p>
     * <p>
     * Note: Calling this method replaces any previously configured class exclusions.
     * </p>
     *
     * @param classesToExclude collection of classes to exclude from testing, must not be {@code null}
     * @return this {@code TestPojo} instance for method chaining
     */
    public TestPojo excludeClasses(final Collection<Class<?>> classesToExclude) {
        excludeClasses = classesToExclude;
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
        processClasses(aClass -> new TestPojoRandom(aClass, excludeMethods, classPredicate, methodPredicate)
                .testClass());
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
        processClasses(aClass -> new TestPojoSetterGetter(aClass, excludeMethods, classPredicate, methodPredicate)
                .testClass());
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
        processClasses(aClass -> new TestPojoEqualsAndHashCode(aClass, excludeMethods, classPredicate, methodPredicate)
                .testClass());
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
        processClasses(aClass -> new TestPojoToString(aClass, excludeMethods, classPredicate, methodPredicate)
                .testClass());
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
        processClasses(aClass -> new TestPojoConstructor(aClass, classPredicate, constructorPredicate).testClass());
        return this;
    }

    /**
     * Prints the test execution report to standard output (console).
     * <p>
     * The report includes information about all classes tested and any findings
     * from each test type that was executed.
     * </p>
     */
    public void printReport() {
        reportService.saveReport();
    }

    /**
     * Retrieves the test execution report as a string.
     * <p>
     * The report includes information about all classes tested and any findings
     * from each test type that was executed. The report is formatted with proper
     * indentation for readability.
     * </p>
     *
     * @return the complete test report as a string, never {@code null}
     */
    public String getReport() {
        return reportService.getReport();
    }

    /**
     * Saves the test execution report to a file.
     * <p>
     * The report is written to the specified path using UTF-8 encoding. If a file
     * already exists at the path, it will be overwritten.
     * </p>
     *
     * @param path the file path where the report should be saved, must not be {@code null}
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void saveReport(final Path path) throws IOException {
        reportService.saveReport(path);
    }

    /**
     * Sets the criteria used to filter methods during processing.
     *
     * @param methodPredicate the {@link Predicate} used to evaluate which {@link java.lang.reflect.Method}
     *                        objects should be included
     * @return the current {@link TestPojo} instance for chaining
     */
    public TestPojo filterMethods(final Predicate<Method> methodPredicate) {
        this.methodPredicate = methodPredicate;
        return this;
    }

    /**
     * Sets the criteria used to filter classes during processing.
     *
     * @param classPredicate the {@link Predicate} used to evaluate which {@link java.lang.Class}
     *                       objects should be included
     * @return the current {@link TestPojo} instance for chaining
     */
    public TestPojo filterClasses(final Predicate<Class<?>> classPredicate) {
        this.classPredicate = classPredicate;
        return this;
    }

    /**
     * Sets the criteria used to filter constructor during processing.
     *
     * @param constructorPredicate the {@link Predicate} used to evaluate which {@link java.lang.reflect.Constructor}
     *                             objects should be included
     * @return the current {@link TestPojo} instance for chaining
     */
    public TestPojo filterConstructors(final Predicate<Constructor<?>> constructorPredicate) {
        this.constructorPredicate = constructorPredicate;
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
    private void processClasses(final Consumer<Class<?>> testFunction) {
        if (clazz != null) {
            for (final Class<?> aClass : clazz) {
                if (!excludeClasses.contains(aClass)) {
                    testFunction.accept(aClass);
                }
            }
        } else if (packageName != null) {
            final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader(packageName);
            classes.stream()
                    .filter(aClass -> !excludeClasses.contains(aClass))
                    .forEach(testFunction);
        }
    }
}
