package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoEqualsException;
import io.github.temesoft.testpojo.exception.TestPojoHashCodeException;
import io.github.temesoft.testpojo.report.TestPojoReportService;
import io.github.temesoft.testpojo.report.TestPojoReportService.TestType;
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
 * Internal utility class responsible for testing the correctness of {@code equals()} and
 * {@code hashCode()} method implementations in a given class.
 * <p>
 * This class verifies that {@code equals()} and {@code hashCode()} methods conform to the
 * general contracts defined in {@link Object#equals(Object)} and {@link Object#hashCode()}.
 * It uses reflection to discover and invoke these methods with various test scenarios using
 * randomly generated test data from the Instancio library.
 * </p>
 * <p>
 * This class is package-private and intended for internal use only within the test-pojo framework.
 * Abstract classes are skipped as they cannot be instantiated.
 * </p>
 */
final class TestPojoEqualsAndHashCode {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPojoEqualsAndHashCode.class);

    private final TestPojoReportService reportService = new TestPojoReportServiceImpl();

    private final Class<?> clazz;
    private final Collection<String> excludeMethods;
    private final Predicate<Class<?>> classPredicate;
    private final Predicate<Method> methodPredicate;

    /**
     * Constructs a new {@code TestPojoEqualsAndHashCode} for testing the {@code equals()} and
     * {@code hashCode()} methods of the specified class.
     *
     * @param clazz           the class whose {@code equals()} and {@code hashCode()} methods will be tested,
     *                        must not be null
     * @param excludeMethods  collection of method names to exclude from testing, may be empty but not null
     * @param classPredicate  criteria used to filter classes during processing.
     * @param methodPredicate criteria used to filter methods during processing.
     */
    TestPojoEqualsAndHashCode(final Class<?> clazz,
                              final Collection<String> excludeMethods,
                              final Predicate<Class<?>> classPredicate,
                              final Predicate<Method> methodPredicate) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
        this.classPredicate = classPredicate;
        this.methodPredicate = methodPredicate;
    }

    /**
     * Tests the {@code equals()} and {@code hashCode()} methods of the target class for correctness.
     * <p>
     * This method skips testing if the target class is abstract. For concrete classes, it performs
     * the following validations:
     * </p>
     *
     * <strong>equals() method tests:</strong>
     * <ul>
     *   <li><strong>Null check:</strong> {@code equals(null)} must return {@code false}</li>
     *   <li><strong>Type check:</strong> {@code equals(differentType)} must return {@code false}</li>
     *   <li><strong>Different instances:</strong> Two objects with different random data must not be equal</li>
     *   <li><strong>Reflexivity:</strong> An object must equal itself ({@code obj.equals(obj)} returns {@code true})</li>
     * </ul>
     *
     * <strong>hashCode() method tests:</strong>
     * <ul>
     *   <li><strong>Consistency:</strong> Two objects with different random data should produce different hash codes
     *       (though hash code collisions are theoretically possible, they should be rare for randomly generated data)</li>
     * </ul>
     *
     * <p>
     * Only methods matching the exact signatures are tested:
     * </p>
     * <ul>
     *   <li>{@code public boolean equals(Object obj)}</li>
     *   <li>{@code public int hashCode()}</li>
     * </ul>
     *
     * @throws TestPojoEqualsException   if any {@code equals()} contract violation is detected, including:
     *                                   <ul>
     *                                     <li>Returning {@code true} when compared with {@code null}</li>
     *                                     <li>Returning {@code true} when compared with an object of a different type</li>
     *                                     <li>Returning {@code true} when comparing two randomly generated distinct objects</li>
     *                                     <li>Returning {@code false} when an object is compared with itself</li>
     *                                   </ul>
     * @throws TestPojoHashCodeException if two objects with different random data produce the same hash code
     * @throws RuntimeException          if reflection operations fail due to {@link IllegalAccessException}
     *                                   or {@link InvocationTargetException}
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
        LOGGER.debug("Running equals() and hashCode() test for: {}", clazz.getName());
        final Object objectRandom1 = TestPojoUtils.createObject(clazz);
        final Object objectRandom2 = TestPojoUtils.createObject(clazz);
        final Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            if (method.getName().equals("equals")
                    && method.getReturnType().equals(boolean.class)
                    && method.getParameterCount() == 1
                    && method.getParameters()[0].getType().equals(Object.class)
                    && checkMethodPredicate(method)
                    && !clazz.isEnum()
                    && !isMethodExcluded(method, excludeMethods)) {
                try {
                    String message = String.format("Method: %s", method);
                    LOGGER.trace(message);
                    reportService.addReportEntry(TestType.EqualsAndHashCode, clazz, message);
                    boolean response = (boolean) method.invoke(objectRandom1, (Object) null);
                    if (response) {
                        throw new TestPojoEqualsException(
                                method,
                                "Equals should not return true when null is passed as argument"
                        );
                    }
                    response = (boolean) method.invoke(objectRandom1, this);
                    if (response) {
                        throw new TestPojoEqualsException(
                                method,
                                "Equals should not return true when object of different type is passed as argument"
                        );
                    }
                    response = (boolean) method.invoke(objectRandom1, objectRandom2);
                    if (response) {
                        throw new TestPojoEqualsException(
                                method,
                                "Two objects with random attributes should not equal"
                        );
                    }
                    response = (boolean) method.invoke(objectRandom1, objectRandom1);
                    if (!response) {
                        throw new TestPojoEqualsException(
                                method,
                                "Same object should be equal"
                        );
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Illegal access exception", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Invocation target exception", e);
                }
            } else if (method.getName().equals("hashCode")
                    && method.getReturnType().equals(int.class)
                    && method.getParameterCount() == 0
                    && checkMethodPredicate(method)
                    && !clazz.isEnum()
                    && !isMethodExcluded(method, excludeMethods)) {
                String message = String.format("Method: %s", method);
                LOGGER.trace(message);
                reportService.addReportEntry(TestType.EqualsAndHashCode, clazz, message);
                try {
                    final int response1 = (int) method.invoke(objectRandom1);
                    final int response2 = (int) method.invoke(objectRandom2);
                    if (response1 == response2) {
                        throw new TestPojoHashCodeException(
                                method,
                                "Two objects with different attributes should return different hashCode value"
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
