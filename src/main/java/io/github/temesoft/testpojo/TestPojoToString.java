package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoToStringException;
import io.github.temesoft.testpojo.report.TestPojoReportService;
import io.github.temesoft.testpojo.report.TestPojoReportServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.Predicate;

import static io.github.temesoft.testpojo.TestPojoUtils.isMethodExcluded;

/**
 * Internal utility class responsible for testing the correctness and consistency of
 * {@code toString()} method implementations in a given class.
 * <p>
 * This class verifies that {@code toString()} methods conform to the general contract
 * defined in {@link Object#toString()}, specifically testing for consistency - that
 * multiple invocations of {@code toString()} on the same unchanged object return equal
 * string representations. It uses reflection to discover and invoke the {@code toString()}
 * method with randomly generated test data from the Instancio library.
 * </p>
 * <p>
 * This class is package-private and intended for internal use only within the test-pojo framework.
 * Abstract classes are skipped as they cannot be instantiated.
 * </p>
 */
final class TestPojoToString {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPojoToString.class);

    private final TestPojoReportService reportService = new TestPojoReportServiceImpl();

    private final Class<?> clazz;
    private final Collection<String> excludeMethods;
    private final Predicate<Class<?>> classPredicate;
    private final Predicate<Method> methodPredicate;

    /**
     * Constructs a new {@code TestPojoToString} for testing the {@code toString()} method
     * of the specified class.
     *
     * @param clazz           the class whose {@code toString()} method will be tested, must not be null
     * @param excludeMethods  collection of method names to exclude from testing, may be empty but not null
     * @param classPredicate  criteria used to filter classes during processing.
     * @param methodPredicate criteria used to filter methods during processing.
     */
    TestPojoToString(final Class<?> clazz,
                     final Collection<String> excludeMethods,
                     final Predicate<Class<?>> classPredicate,
                     final Predicate<Method> methodPredicate) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
        this.classPredicate = classPredicate;
        this.methodPredicate = methodPredicate;
    }

    /**
     * Tests the {@code toString()} method of the target class for consistency.
     * <p>
     * This method performs the following steps:
     * </p>
     * <ol>
     *   <li>Skips testing if the target class is abstract</li>
     *   <li>Creates a random instance of the class using Instancio</li>
     *   <li>Locates the {@code toString()} method</li>
     *   <li>Invokes {@code toString()} twice on the same unchanged object</li>
     *   <li>Verifies that both invocations return equal string values</li>
     * </ol>
     *
     * <strong>Method signature requirements:</strong>
     * <p>
     * Only methods matching the exact {@code toString()} signature are tested:
     * </p>
     * <ul>
     *   <li>Method name must be "toString"</li>
     *   <li>Return type must be {@link String}</li>
     *   <li>Must have zero parameters</li>
     *   <li>Must not be in the excluded methods collection</li>
     * </ul>
     *
     * <strong>Consistency validation:</strong>
     * <p>
     * The test verifies that calling {@code toString()} multiple times on the same object
     * without any state changes produces equal results, as determined by
     * {@link String#equals(Object)}. This ensures that:
     * </p>
     * <ul>
     *   <li>The {@code toString()} implementation is deterministic</li>
     *   <li>The method does not modify object state (no side effects)</li>
     *   <li>The method does not depend on external mutable state or random values</li>
     *   <li>The string representation is stable for unchanged objects</li>
     * </ul>
     *
     * @throws TestPojoToStringException if the {@code toString()} method violates the
     *                                   consistency contract by returning different values when invoked multiple times
     *                                   on the same unchanged object. This typically indicates that the implementation
     *                                   includes random values, timestamps, or other non-deterministic elements.
     * @throws RuntimeException          if reflection operations fail due to {@link IllegalAccessException}
     *                                   or {@link InvocationTargetException}, wrapping the underlying exception with a
     *                                   descriptive message
     */
    void testClass() {
        if (classPredicate != null && !classPredicate.test(clazz)) {
            LOGGER.trace("Skipping class based on predicate: {}", clazz.getName());
            return;
        }
        if (Modifier.isInterface(clazz.getModifiers())) {
            LOGGER.trace("Skipping interface class: {}", clazz.getName());
            return;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            LOGGER.trace("Skipping abstract class: {}", clazz.getName());
            return;
        }
        LOGGER.debug("Running toString() test for: {}", clazz.getName());
        final Object objectRandom = TestPojoUtils.createObject(clazz);
        final Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            if (method.getName().equals("toString")
                    && method.getReturnType().equals(String.class)
                    && method.getParameterCount() == 0
                    && checkMethodPredicate(method)
                    && !clazz.isEnum()
                    && !isMethodExcluded(method, excludeMethods)) {
                final String message = String.format("Method: %s", method);
                LOGGER.trace(message);
                reportService.addReportEntry(TestPojoReportService.TestType.ToString, clazz, message);
                try {
                    final String response = (String) method.invoke(objectRandom);
                    final String responseRepeat = (String) method.invoke(objectRandom);
                    if (!responseRepeat.equals(response)) {
                        throw new TestPojoToStringException(
                                method,
                                "Same unchanged object should return same toString() value every time"
                        );
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Illegal access exception", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Invocation target exception", e);
                }
            }
        }
    }

    /**
     * Evaluates whether a given method matches the configured filtering criteria.
     * If no {@code methodPredicate} has been defined (is {@code null}), this method
     * defaults to {@code true}, effectively including all methods.
     *
     * @param method the {@link java.lang.reflect.Method} to evaluate against the predicate
     * @return {@code true} if the method matches the predicate or if no predicate is set;
     * {@code false} otherwise
     */
    private boolean checkMethodPredicate(final Method method) {
        final boolean result = (methodPredicate == null || methodPredicate.test(method));
        if (!result) {
            LOGGER.trace("Skipping method based on predicate: {}", method);
        }
        return result;
    }
}
