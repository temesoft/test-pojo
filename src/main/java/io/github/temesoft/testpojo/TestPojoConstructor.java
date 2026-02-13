package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoConstructorException;
import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import io.github.temesoft.testpojo.report.TestPojoReportService;
import io.github.temesoft.testpojo.report.TestPojoReportService.TestType;
import io.github.temesoft.testpojo.report.TestPojoReportServiceImpl;
import org.instancio.Instancio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
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

    private final TestPojoReportService reportService = new TestPojoReportServiceImpl();

    private final Class<?> clazz;
    private final Predicate<Class<?>> classPredicate;
    private final Predicate<Constructor<?>> constructorPredicate;

    /**
     * Constructs a new {@code TestPojoConstructor} for testing the constructors of the specified class.
     *
     * @param clazz                the class whose constructors will be tested, must not be null
     * @param classPredicate       criteria used to filter classes during processing.
     * @param constructorPredicate criteria used to filter constructors during processing.
     */
    TestPojoConstructor(final Class<?> clazz,
                        final Predicate<Class<?>> classPredicate,
                        final Predicate<Constructor<?>> constructorPredicate) {
        this.clazz = clazz;
        this.classPredicate = classPredicate;
        this.constructorPredicate = constructorPredicate;
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
     *   <li>Handles special cases like {@link Collection}, and {@link Map} types, interfaces and
     *   abstract class arguments</li>
     *   <li>Invokes the constructor with the generated arguments</li>
     * </ul>
     * <p>
     * Special handling is provided for:
     * </p>
     * <ul>
     *   <li>Interfaces and abstract classes - created using proxy model</li>
     *   <li>Generic collections and maps - created with proper type parameters</li>
     *   <li>Other types - created using Instancio's default generation</li>
     * </ul>
     *
     * @throws TestPojoConstructorException if any constructor cannot be instantiated successfully
     * @throws TestPojoRawUseException      if for example a {@link Collection} or {@link Map} parameter is used
     *                                      without generic type parameters (raw type usage)
     */
    void testClass() {
        if (classPredicate != null && !classPredicate.test(clazz)) {
            LOGGER.trace("Skipping class based on predicate: {}", clazz.getName());
            return;
        }
        final Constructor<?>[] constructors = clazz.getConstructors();
        if (Modifier.isInterface(clazz.getModifiers())) {
            LOGGER.trace("Skipping interface class: {}", clazz.getName());
            return;
        }
        if (Modifier.isAbstract(clazz.getModifiers())) {
            LOGGER.trace("Skipping abstract class: {}", clazz.getName());
            return;
        }
        if (clazz.isEnum()) {
            LOGGER.trace("Skipping enum class: {}", clazz.getName());
            return;
        }
        LOGGER.debug("Running constructor test for: {}", clazz.getName());
        for (final Constructor<?> constructor : constructors) {
            if (constructorPredicate != null && !constructorPredicate.test(constructor)) {
                LOGGER.trace("Skipping constructor based on predicate: {}", constructor);
                continue;
            }
            String message = String.format("Constructor: %s", constructor);
            LOGGER.trace(message);
            reportService.addReportEntry(TestType.Constructor, clazz, message);
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
                            List<Class<?>> listOfParamClasses = new ArrayList<>();
                            for (int i = 0; i < typeParameters.length; i++) {
                                final Class<?> typeParameter = typeParameters[i];
                                if (i < typeParameters.length - 1) {
                                    listOfParamClasses.add(typeParameter);
                                } else {
                                    listOfParamClasses.add(TestPojoUtils.getGenericTypeToken().getClass());
                                }
                            }
                            arguments.add(
                                    Instancio.of(parameter.getType())
                                            .withTypeParameters(listOfParamClasses.toArray(new Class[0]))
                                            .create()
                            );
                        } else {
                            throw new TestPojoRawUseException(constructor, parameter.getType());
                        }
                    } else {
                        arguments.add(TestPojoUtils.createObject(parameter.getType()));
                    }
                }
            }
            try {
                if (!arguments.isEmpty()) {
                    message = String.format("Arguments: %s", arguments);
                    LOGGER.trace(message);
                    reportService.addReportEntry(TestType.Constructor, clazz, message);
                }
                final Object unused = constructor.newInstance(arguments.toArray(new Object[0]));
            } catch (Exception e) {
                throw new TestPojoConstructorException(constructor, "Constructor instantiation exception: " + e.getMessage());
            }
        }
    }
}
