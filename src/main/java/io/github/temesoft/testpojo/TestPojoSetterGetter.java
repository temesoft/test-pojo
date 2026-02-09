package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import io.github.temesoft.testpojo.exception.TestPojoSetterGetterException;
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

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    /**
     * Constructs a new {@code TestPojoSetterGetter} for testing the setter and getter methods
     * of the specified class.
     *
     * @param clazz          the class whose setter and getter methods will be tested, must not be null
     * @param excludeMethods collection of method names to exclude from testing, may be empty but not null
     */
    TestPojoSetterGetter(final Class<?> clazz,
                         final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
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
     * <h3>Setter method discovery (tried in order):</h3>
     * <ul>
     *   <li>{@code setFieldName(Type)} - standard JavaBeans convention</li>
     *   <li>{@code set_fieldName(Type)} - underscore variant</li>
     *   <li>{@code fieldName(Type)} - builder-style setter</li>
     * </ul>
     *
     * <h3>Getter method discovery (tried in order):</h3>
     * <ul>
     *   <li>{@code getFieldName()} - standard JavaBeans convention</li>
     *   <li>{@code get_fieldName()} - underscore variant</li>
     *   <li>{@code isFieldName()} - boolean property convention</li>
     *   <li>{@code fieldName()} - direct field name accessor</li>
     * </ul>
     *
     * <h3>Value generation:</h3>
     * <ul>
     *   <li>For {@link Collection} and {@link Map} fields with generic type information:
     *       creates instances with proper type parameters using Instancio</li>
     *   <li>For other field types: creates random instances using Instancio</li>
     *   <li>Handles parameterized types by extracting and applying generic type arguments</li>
     * </ul>
     *
     * <h3>Validation:</h3>
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
        if (Modifier.isAbstract(clazz.getModifiers())) {
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
                        && !isMethodExcluded(setterMethodFound, excludeMethods)
                        && !isMethodExcluded(getterMethodFound, excludeMethods)) {
                    LOGGER.trace("Using setter method: {}", setterMethodFound);
                    LOGGER.trace("Using getter method: {}", getterMethodFound);
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
                    LOGGER.trace("Using value: {}", value);
                    try {
                        setterMethodFound.invoke(object, value);
                        final Object result = getterMethodFound.invoke(object);
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
}
