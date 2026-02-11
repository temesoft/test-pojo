package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import io.github.temesoft.testpojo.exception.TestPojoSetterGetterException;
import io.github.temesoft.testpojo.report.TestPojoReportService;
import io.github.temesoft.testpojo.report.TestPojoReportServiceImpl;
import org.instancio.Instancio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static io.github.temesoft.testpojo.TestPojoUtils.getGetterMethodWithNameAndReturnType;
import static io.github.temesoft.testpojo.TestPojoUtils.getSetterMethodWithNameAndArgumentType;
import static io.github.temesoft.testpojo.TestPojoUtils.isMethodExcluded;

/**
 * Internal utility class responsible for testing the correctness of setter and getter method
 * pairs in a given class.
 * <p>
 * This class verifies that setter methods properly store values and that corresponding getter
 * methods retrieve the same values that were set. It uses reflection to discover fields and
 * their associated accessor methods, then tests each pair with randomly generated data using
 * the Instancio library. This helps identify issues such as incorrect field assignments,
 * transformation bugs, or mismatched getter/setter pairs.
 * </p>
 * <p>
 * This class is package-private and intended for internal use only within the test-pojo framework.
 * Abstract classes are skipped as they cannot be instantiated.
 * </p>
 */
final class TestPojoSetterGetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPojoSetterGetter.class);

    private final TestPojoReportService reportService = new TestPojoReportServiceImpl();

    private final Class<?> clazz;
    private final Collection<String> excludeMethods;
    private final Predicate<Class<?>> classPredicate;
    private final Predicate<Method> methodPredicate;

    /**
     * Constructs a new {@code TestPojoSetterGetter} for testing the setter and getter methods
     * of the specified class.
     *
     * @param clazz           the class whose setter and getter methods will be tested, must not be null
     * @param excludeMethods  collection of method names to exclude from testing, may be empty but not null
     * @param classPredicate  criteria used to filter classes during processing.
     * @param methodPredicate criteria used to filter methods during processing.
     */
    TestPojoSetterGetter(final Class<?> clazz,
                         final Collection<String> excludeMethods,
                         final Predicate<Class<?>> classPredicate,
                         final Predicate<Method> methodPredicate) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
        this.classPredicate = classPredicate;
        this.methodPredicate = methodPredicate;
    }

    /**
     * Tests all setter and getter method pairs of the target class by verifying that values
     * set through setters can be correctly retrieved through getters.
     * <p>
     * This method performs the following steps:
     * </p>
     * <ol>
     *   <li>Skips testing if the target class is abstract</li>
     *   <li>Creates a random instance of the class using Instancio</li>
     *   <li>Iterates through all fields of the class (including inherited fields)</li>
     *   <li>For each field, attempts to find matching setter and getter methods</li>
     *   <li>Generates a random value appropriate for the field type</li>
     *   <li>Invokes the setter with the generated value</li>
     *   <li>Invokes the getter and verifies it returns the same value</li>
     * </ol>
     *
     * <strong>Setter method discovery (tried in order):</strong>
     * <ul>
     *   <li>{@code setFieldName(Type)} - standard JavaBeans convention</li>
     *   <li>{@code set_fieldName(Type)} - underscore variant</li>
     *   <li>{@code fieldName(Type)} - builder-style setter</li>
     * </ul>
     *
     * <strong>Getter method discovery (tried in order):</strong>
     * <ul>
     *   <li>{@code getFieldName()} - standard JavaBeans convention</li>
     *   <li>{@code get_fieldName()} - underscore variant</li>
     *   <li>{@code isFieldName()} - boolean property convention</li>
     *   <li>{@code fieldName()} - direct field name accessor</li>
     * </ul>
     *
     * <strong>Value generation:</strong>
     * <ul>
     *   <li>For {@link Collection} and {@link Map} fields with generic type information:
     *       creates instances with proper type parameters using Instancio</li>
     *   <li>For other field types: creates random instances using Instancio</li>
     *   <li>Handles parameterized types by extracting and applying generic type arguments</li>
     * </ul>
     *
     * <strong>Validation:</strong>
     * <p>
     * After setting a value, the getter is invoked and the result is compared with the original
     * value using {@link Object#equals(Object)}. The test fails if:
     * </p>
     * <ul>
     *   <li>The getter returns {@code null}</li>
     *   <li>The getter returns a value that is not equal to the value that was set</li>
     * </ul>
     *
     * <p>
     * Only fields that have both accessible setter and getter methods (neither excluded) are tested.
     * Fields without matching accessor methods are silently skipped.
     * </p>
     *
     * @throws TestPojoSetterGetterException if a setter/getter pair does not work correctly,
     *                                       specifically when the value retrieved by the getter does not equal the value set
     *                                       by the setter, or when the getter returns {@code null}
     * @throws TestPojoRawUseException       if a {@link Collection} or {@link Map} field is encountered
     *                                       without generic type parameters (raw type usage), as this prevents proper test data generation
     * @throws RuntimeException              if reflection operations fail due to {@link IllegalAccessException}
     *                                       or {@link InvocationTargetException}, wrapping the underlying exception with a descriptive message
     */
    void testClass() {
        if (classPredicate != null && !classPredicate.test(clazz)) {
            LOGGER.trace("Skipping class based on predicate: {}", clazz.getName());
            return;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            LOGGER.trace("Skipping abstract class: {}", clazz.getName());
            return;
        }
        LOGGER.debug("Running setter/getter test for: {}", clazz.getName());
        final Object object = Instancio.create(clazz);
        final Method[] methods = clazz.getMethods();
        final List<Field> fields = TestPojoUtils.getAllFields(clazz);
        for (final Field field : fields) {
            Method setterMethodFound = getSetterMethodWithNameAndArgumentType(
                    methods,
                    "set" + field.getName(),
                    field.getType(),
                    object
            );
            if (setterMethodFound == null) {
                setterMethodFound = getSetterMethodWithNameAndArgumentType(
                        methods,
                        "set_" + field.getName(),
                        field.getType(),
                        object
                );
            }
            if (setterMethodFound == null) {
                setterMethodFound = getSetterMethodWithNameAndArgumentType(
                        methods,
                        field.getName(),
                        field.getType(),
                        object
                );
            }

            if (!TestPojoUtils.isMethodExcluded(setterMethodFound, excludeMethods)) {
                Method getterMethodFound = getGetterMethodWithNameAndReturnType(
                        methods,
                        "get" + field.getName(),
                        field.getType(),
                        object
                );
                if (getterMethodFound == null) {
                    getterMethodFound = getGetterMethodWithNameAndReturnType(
                            methods,
                            "get_" + field.getName(),
                            field.getType(),
                            object
                    );
                }
                if (getterMethodFound == null) {
                    getterMethodFound = getGetterMethodWithNameAndReturnType(
                            methods,
                            "is" + field.getName(),
                            field.getType(),
                            object
                    );
                }
                if (getterMethodFound == null) {
                    getterMethodFound = getGetterMethodWithNameAndReturnType(
                            methods,
                            field.getName(),
                            field.getType(),
                            object
                    );
                }

                if (setterMethodFound != null
                        && getterMethodFound != null
                        && checkMethodPredicate(setterMethodFound, getterMethodFound)
                        && !isMethodExcluded(setterMethodFound, excludeMethods)
                        && !isMethodExcluded(getterMethodFound, excludeMethods)) {
                    String message = String.format("Using setter method: %s", setterMethodFound);
                    LOGGER.trace(message);
                    reportService.addReportEntry(TestPojoReportService.TestType.SetterGetter, clazz, message);
                    message = String.format("Using getter method: %s", getterMethodFound);
                    LOGGER.trace(message);
                    reportService.addReportEntry(TestPojoReportService.TestType.SetterGetter, clazz, message);
                    final Object value;
                    if (Collection.class.isAssignableFrom(field.getType())
                            || Map.class.isAssignableFrom(field.getType())) {
                        final Type type = field.getGenericType();
                        if (type instanceof ParameterizedType) {
                            final ParameterizedType pType = (ParameterizedType) type;
                            final Type[] typeArguments = pType.getActualTypeArguments();
                            final Class<?>[] typeParameters = TestPojoUtils.typesToClasses(typeArguments);
                            value = Instancio.of(field.getType())
                                    .withTypeParameters(typeParameters)
                                    .create();
                        } else {
                            throw new TestPojoRawUseException(setterMethodFound, field.getType());
                        }
                    } else {
                        value = Instancio.create(field.getType());
                    }
                    message = String.format("\tUsing value: %s", value);
                    LOGGER.trace(message);
                    reportService.addReportEntry(TestPojoReportService.TestType.SetterGetter, clazz, message);
                    try {
                        setterMethodFound.invoke(object, value);
                        final Object result = getterMethodFound.invoke(object);
                        message = String.format("\tGetter result value: %s", result);
                        LOGGER.trace(message);
                        reportService.addReportEntry(TestPojoReportService.TestType.SetterGetter, clazz, message);
                        if (result == null || !result.equals(value)) {
                            throw new TestPojoSetterGetterException(
                                    setterMethodFound,
                                    getterMethodFound,
                                    value,
                                    result
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
    }

    /**
     * Evaluates whether a given setter and getter methods match the configured filtering criteria.
     * If no {@code methodPredicate} has been defined (is {@code null}), this method
     * defaults to {@code true}, effectively including all methods.
     *
     * @param setterMethodFound the setter {@link java.lang.reflect.Method} to evaluate against the predicate
     * @param getterMethodFound the getter {@link java.lang.reflect.Method} to evaluate against the predicate
     * @return {@code true} if both methods match the predicate or if no predicate is set;
     * {@code false} otherwise
     */
    private boolean checkMethodPredicate(final Method setterMethodFound, final Method getterMethodFound) {
        final boolean result = (methodPredicate == null
                || (methodPredicate.test(setterMethodFound) && methodPredicate.test(getterMethodFound)));
        LOGGER.trace("Skipping setter method based on predicate: {}", setterMethodFound);
        LOGGER.trace("Skipping getter method based on predicate: {}", getterMethodFound);
        return result;
    }
}
