package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoConstructorException;
import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import org.instancio.Instancio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Internal utility class responsible for testing all public constructors of a given class.
 * <p>
 * This class uses reflection to discover all public constructors of a target class and
 * attempts to invoke each one with automatically generated test data. It leverages the
 * Instancio library to create random instances of parameter types, including support for
 * generic collections and maps.
 * </p>
 * <p>
 * This class is package-private and intended for internal use only within the test-pojo framework.
 * </p>
 */
final class TestPojoConstructor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestPojoConstructor.class);

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    /**
     * Constructs a new {@code TestPojoConstructor} for testing the constructors of the specified class.
     *
     * @param clazz          the class whose constructors will be tested, must not be null
     * @param excludeMethods collection of method names to exclude (currently unused for constructor testing)
     */
    TestPojoConstructor(final Class<?> clazz,
                        final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    /**
     * Tests all public constructors of the target class by attempting to invoke each one
     * with automatically generated parameters.
     * <p>
     * For each constructor, this method:
     * </p>
     * <ul>
     *   <li>Analyzes each parameter type</li>
     *   <li>Generates appropriate test data using Instancio</li>
     *   <li>Handles special cases like {@link Constructor}, {@link Class}, {@link Collection}, and {@link Map} types</li>
     *   <li>Invokes the constructor with the generated arguments</li>
     * </ul>
     * <p>
     * Special handling is provided for:
     * </p>
     * <ul>
     *   <li>{@link Constructor} parameters - passed as null</li>
     *   <li>{@link Class} parameters - passed the parameter type itself</li>
     *   <li>Generic collections and maps - created with proper type parameters</li>
     *   <li>Other types - created using Instancio's default generation</li>
     * </ul>
     *
     * @throws TestPojoConstructorException if any constructor cannot be instantiated successfully
     * @throws TestPojoRawUseException      if a {@link Collection} or {@link Map} parameter is used
     *                                      without generic type parameters (raw type usage)
     */
    void testClass() {
        LOGGER.debug("Running constructor test for: {}", clazz.getName());
        final Constructor<?>[] constructors = clazz.getConstructors();
        for (final Constructor<?> constructor : constructors) {
            final List<Object> arguments = new ArrayList<>();
            final Parameter[] parameters = constructor.getParameters();
            for (final Parameter parameter : parameters) {
                if (parameter.getType().equals(Constructor.class)) {
                    arguments.add(null);
                } else if (parameter.getType().equals(Class.class)) {
                    arguments.add(parameter.getType());
                } else {
                    if (Collection.class.isAssignableFrom(parameter.getType())
                            || Map.class.isAssignableFrom(parameter.getType())) {
                        final Type genericType = parameter.getParameterizedType();
                        if (genericType instanceof ParameterizedType) {
                            final ParameterizedType pType = (ParameterizedType) genericType;
                            final Type[] typeArguments = pType.getActualTypeArguments();
                            final Class<?>[] typeParameters = TestPojoUtils.typesToClasses(typeArguments);
                            arguments.add(
                                    Instancio.of(parameter.getType())
                                            .withTypeParameters(typeParameters)
                                            .create()
                            );
                        } else {
                            throw new TestPojoRawUseException(constructor, parameter.getType());
                        }
                    } else {
                        arguments.add(Instancio.create(parameter.getType()));
                    }
                }
            }
            try {
                final Object unused = constructor.newInstance(arguments.toArray(new Object[0]));
            } catch (Exception e) {
                throw new TestPojoConstructorException(constructor, "Constructor instantiation exception: " + e.getMessage());
            }
        }
    }
}
