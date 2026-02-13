package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import io.github.temesoft.testpojo.report.TestPojoReportService;
import io.github.temesoft.testpojo.report.TestPojoReportServiceImpl;
import org.instancio.Instancio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Internal utility class responsible for testing all accessible methods of a given class
 * by invoking them with randomly generated parameters.
 * <p>
 * This class creates a random instance of the target class and systematically invokes all
 * accessible public methods with automatically generated parameter values. The primary goal
 * is to verify that methods can be invoked without throwing unexpected exceptions when called
 * with valid random data. This helps identify potential runtime issues, null pointer exceptions,
 * or other method implementation problems.
 * </p>
 * <p>
 * This class is package-private and intended for internal use only within the test-pojo framework.
 * Abstract classes are skipped as they cannot be instantiated.
 * </p>
 */
final class TestPojoRandom {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPojoRandom.class);

    private final TestPojoReportService reportService = new TestPojoReportServiceImpl();

    private final Class<?> clazz;
    private final Collection<String> excludeMethods;
    private final Predicate<Class<?>> classPredicate;
    private final Predicate<Method> methodPredicate;

    /**
     * Constructs a new {@code TestPojoRandom} for testing the methods of the specified class
     * with random parameter values.
     *
     * @param clazz           the class whose methods will be tested, must not be null
     * @param excludeMethods  collection of method names to exclude from testing, may be empty but not null
     * @param classPredicate  criteria used to filter classes during processing.
     * @param methodPredicate criteria used to filter methods during processing.
     */
    TestPojoRandom(final Class<?> clazz,
                   final Collection<String> excludeMethods,
                   final Predicate<Class<?>> classPredicate,
                   final Predicate<Method> methodPredicate) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
        this.classPredicate = classPredicate;
        this.methodPredicate = methodPredicate;
    }

    /**
     * Tests all accessible public methods of the target class by invoking them with randomly
     * generated parameter values.
     * <p>
     * This method performs the following steps:
     * </p>
     * <ol>
     *   <li>Skips testing if the target class is abstract</li>
     *   <li>Creates a random instance of the class using Instancio</li>
     *   <li>Iterates through all public methods of the class</li>
     *   <li>For each eligible method, generates random parameter values</li>
     *   <li>Invokes the method with the generated parameters</li>
     * </ol>
     *
     * <strong>Methods excluded from testing:</strong>
     * <ul>
     *   <li>Methods in the {@code excludeMethods} collection</li>
     *   <li>Methods that are not accessible from the current context</li>
     *   <li>Core {@link Object} methods</li>
     *   <li>Core {@link Throwable} methods</li>
     * </ul>
     *
     * <strong>Parameter generation:</strong>
     * <ul>
     *   <li>Analyzes each parameter type</li>
     *   <li>Generates appropriate test data using Instancio</li>
     *   <li>Handles special cases like {@link Collection}, and {@link Map} types, interfaces and
     *   abstract class arguments</li>
     *   <li>Invokes the constructor with the generated arguments</li>
     *   <li>Handles parameterized types by extracting and applying generic type arguments</li>
     * </ul>
     *
     * @throws TestPojoRawUseException if a for example {@link Collection} or {@link Map} parameter is encountered
     *                                 without generic type parameters (raw type usage), as this prevents proper test data generation
     * @throws RuntimeException        if any method invocation fails, wrapping the underlying exception
     *                                 with a descriptive message. This typically indicates a bug in the method implementation
     *                                 or an issue with the generated test data.
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
        LOGGER.debug("Running random instantiation test for: {}", clazz.getName());

        final Object object = TestPojoUtils.createObject(clazz);
        final Method[] methods = object.getClass().getMethods();
        int methodsRan = 0;
        for (final Method method : methods) {
            if (!TestPojoUtils.isMethodExcluded(method, excludeMethods)
                    && !method.getDeclaringClass().equals(Object.class)
                    && !method.getDeclaringClass().equals(Throwable.class)
                    && !method.toString().contains(".build()")
                    && TestPojoUtils.canAccess(method, object)
                    && checkMethodPredicate(method)
                    && !(clazz.isEnum() && method.toString().contains(".valueOf(java.lang.String)"))
                    && !(clazz.isEnum() && method.toString().contains(" java.lang.Enum."))) {
                methodsRan++;
                String message = String.format("Method: %s", method);
                LOGGER.trace(message);
                reportService.addReportEntry(TestPojoReportService.TestType.Random, clazz, message);
                final List<Object> invokeParameters = new ArrayList<>();
                final Parameter[] parameters = method.getParameters();
                for (final Parameter parameter : parameters) {
                    final Object parameterValue;
                    if (Collection.class.isAssignableFrom(parameter.getType())
                            || Map.class.isAssignableFrom(parameter.getType())) {
                        final Type genericType = parameter.getParameterizedType();
                        if (genericType instanceof ParameterizedType) {
                            final ParameterizedType pType = (ParameterizedType) genericType;
                            final Type[] typeArguments = pType.getActualTypeArguments();
                            final Class<?>[] typeParameters = TestPojoUtils.typesToClasses(typeArguments);
                            final List<Class<?>> listOfParamClasses = new ArrayList<>();
                            for (int i = 0; i < typeParameters.length; i++) {
                                final Class<?> typeParameter = typeParameters[i];
                                if (i < typeParameters.length - 1) {
                                    listOfParamClasses.add(typeParameter);
                                } else {
                                    listOfParamClasses.add(TestPojoUtils.getGenericTypeToken().getClass());
                                }
                            }
                            parameterValue = Instancio.of(parameter.getType())
                                    .withTypeParameters(listOfParamClasses.toArray(new Class[0]))
                                    .create();
                        } else {
                            throw new TestPojoRawUseException(method, parameter.getType());
                        }
                    } else {
                        parameterValue = TestPojoUtils.createObject(parameter.getType());
                    }
                    invokeParameters.add(parameterValue);
                }
                try {
                    if (!invokeParameters.isEmpty()) {
                        message = String.format("\tArguments: %s", invokeParameters);
                        LOGGER.trace("{}", message);
                        reportService.addReportEntry(TestPojoReportService.TestType.Random, clazz, message);
                    }
                    final Object response = method.invoke(object, invokeParameters.toArray(new Object[0]));
                    if (!method.getReturnType().equals(void.class)) {
                        message = String.format("\tReturn: %s", response);
                        LOGGER.trace("{}", message);
                        reportService.addReportEntry(TestPojoReportService.TestType.Random, clazz, message);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Method invocation exception: " + e.getMessage(), e);
                }
            }
        }
        final String message = String.format("Methods tested: %s", methodsRan);
        LOGGER.trace(message);
        reportService.addReportEntry(TestPojoReportService.TestType.Random, clazz, message);
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
        LOGGER.trace("Skipping method based on predicate: {}", method);
        return result;
    }
}
