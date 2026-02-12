package io.github.temesoft.testpojo;

import org.instancio.Instancio;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Internal utility class providing helper methods for reflection-based testing operations.
 * <p>
 * This class contains static utility methods used by the test-pojo framework to discover,
 * filter, and validate methods and fields through reflection. It provides functionality for
 * method exclusion checking, setter/getter discovery, type conversions, field retrieval,
 * and accessibility verification.
 * </p>
 * <p>
 * This class cannot be instantiated as it only provides static utility methods.
 * This class is package-private and intended for internal use only within the test-pojo framework.
 * </p>
 */
final class TestPojoUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private TestPojoUtils() {
    }

    /**
     * Determines whether a method should be excluded from testing based on the exclusion list.
     * <p>
     * A method is considered excluded if its {@link Method#toString()} representation contains
     * any of the strings in the exclusion collection. This allows for flexible matching including
     * partial method signatures.
     * </p>
     *
     * @param method         the method to check for exclusion, may be {@code null}
     * @param excludeMethods collection of exclusion patterns to match against the method's string
     *                       representation, may be {@code null} or empty
     * @return {@code true} if the method should be excluded from testing, {@code false} otherwise.
     * Returns {@code false} if the method is {@code null} or if the exclusion collection
     * is {@code null} or empty.
     */
    public static boolean isMethodExcluded(final Method method, final Collection<String> excludeMethods) {
        if (method == null) {
            return false;
        }
        if (excludeMethods != null && !excludeMethods.isEmpty()) {
            for (String excludeMethod : excludeMethods) {
                final boolean excluded = method.toString().contains(excludeMethod);
                if (excluded) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds a setter method with the specified name and argument type from an array of methods.
     * <p>
     * This method searches for a setter method that matches the following criteria:
     * </p>
     * <ul>
     *   <li>Method name matches {@code nameCaseIgnore} (case-insensitive)</li>
     *   <li>Method has exactly one parameter</li>
     *   <li>The parameter type exactly matches {@code argumentType}</li>
     *   <li>The method is accessible (can be invoked on the provided object)</li>
     * </ul>
     *
     * @param methods        array of methods to search through, must not be {@code null}
     * @param nameCaseIgnore the method name to match (case-insensitive), must not be {@code null}
     * @param argumentType   the expected parameter type of the setter, must not be {@code null}
     * @param object         the object instance used to verify method accessibility, must not be {@code null}
     * @return the first matching setter method, or {@code null} if no matching method is found
     */
    public static Method getSetterMethodWithNameAndArgumentType(final Method[] methods,
                                                                final String nameCaseIgnore,
                                                                final Class<?> argumentType,
                                                                final Object object) {
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(nameCaseIgnore)) {
                final Parameter[] parameters = method.getParameters();
                if (parameters.length == 1) {
                    final Parameter parameter = parameters[0];
                    if (parameter.getType().equals(argumentType)) {
                        if (canAccess(method, object)) {
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a getter method with the specified name and return type from an array of methods.
     * <p>
     * This method searches for a getter method that matches the following criteria:
     * </p>
     * <ul>
     *   <li>Method name matches {@code nameCaseIgnore} (case-insensitive)</li>
     *   <li>Method has no parameters</li>
     *   <li>Return type exactly matches {@code returnType}</li>
     *   <li>The method is accessible (can be invoked on the provided object)</li>
     * </ul>
     *
     * @param methods        array of methods to search through, must not be {@code null}
     * @param nameCaseIgnore the method name to match (case-insensitive), must not be {@code null}
     * @param returnType     the expected return type of the getter, must not be {@code null}
     * @param object         the object instance used to verify method accessibility, must not be {@code null}
     * @return the first matching getter method, or {@code null} if no matching method is found
     */
    public static Method getGetterMethodWithNameAndReturnType(final Method[] methods,
                                                              final String nameCaseIgnore,
                                                              final Class<?> returnType,
                                                              final Object object) {
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(nameCaseIgnore)) {
                if (method.getParameters().length == 0 && method.getReturnType().equals(returnType)) {
                    if (canAccess(method, object)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Converts an array of {@link Type} objects into an array of {@link Class} objects by
     * resolving raw types and type variable bounds.
     * <p>
     * This method performs safe resolution of various {@link Type} implementations:
     * <ul>
     *     <li><b>Class:</b> Directly added to the result.</li>
     *     <li><b>ParameterizedType:</b> The raw type (e.g., {@code List} from {@code List<String>}) is extracted.</li>
     *     <li><b>TypeVariable:</b> The first upper bound is inspected. If the bound is
     *         {@link Object}, it is mapped to {@link String}; otherwise, the bound's raw
     *         class is resolved.</li>
     * </ul>
     *
     * @param types an array of {@link Type} instances to be converted
     * @return an array of {@link Class} objects representing the best-effort resolution
     * of the provided types
     * @see java.lang.reflect.ParameterizedType#getRawType()
     * @see java.lang.reflect.TypeVariable#getBounds()
     */
    public static Class<?>[] typesToClasses(final Type[] types) {
        if (types == null) {
            return new Class[0];
        }
        final List<Class<?>> result = new ArrayList<>();
        for (final Type type : types) {
            final Class<?> resolvedClass = resolveToClass(type);
            if (resolvedClass != null) {
                result.add(resolvedClass);
            }
        }
        return result.toArray(new Class[0]);
    }

    /**
     * Helper to safely extract a Class from a Type without risk of ClassCastException.
     */
    private static Class<?> resolveToClass(final Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (type instanceof TypeVariable) {
            final Type[] bounds = ((TypeVariable<?>) type).getBounds();
            if (bounds.length > 0) {
                if (Object.class.equals(bounds[0])) {
                    return String.class;
                }
                return resolveToClass(bounds[0]);
            }
        }

        throw new IllegalStateException("Unable to resolve type [" + type + "] to class");
    }

    /**
     * Retrieves all fields declared in a class and its superclass hierarchy, excluding
     * fields from {@link Object}.
     * <p>
     * This method traverses the entire class hierarchy from the specified class up to (but not
     * including) {@link Object}, collecting all declared fields along the way. This includes
     * private, protected, package-private, and public fields from the class and all its
     * superclasses.
     * </p>
     *
     * @param clazz the class whose fields should be retrieved, must not be {@code null}
     * @return a list containing all fields from the class and its superclass hierarchy,
     * excluding fields from {@link Object}. The list is never {@code null} but may be empty.
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        final List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }

    /**
     * Determines whether a method can be accessed in the current context.
     * <p>
     * This method checks accessibility differently based on whether the method is static:
     * </p>
     * <ul>
     *   <li><strong>Static methods:</strong> Checks if the method can be accessed without an object instance</li>
     *   <li><strong>Instance methods:</strong> Checks if the method can be accessed on the provided object instance</li>
     * </ul>
     * <p>
     * This is useful for determining whether a method can be invoked via reflection, taking into
     * account Java's module system and accessibility rules introduced in Java 9+.
     * </p>
     *
     * @param method the method to check for accessibility, must not be {@code null}
     * @param object the object instance on which the method would be invoked (ignored for static methods),
     *               must not be {@code null} for instance methods
     * @return {@code true} if the method can be accessed (invoked), {@code false} otherwise
     */
    public static boolean canAccess(final Method method, final Object object) {
        return (Modifier.isStatic(method.getModifiers()) && method.canAccess(null))
                || (!Modifier.isStatic(method.getModifiers()) && method.canAccess(object));
    }

    /**
     * Creates an instance of the specified class, automatically resolving generic type
     * parameters where necessary.
     * <p>
     * This method applies specific mapping rules for unknown or broad types:
     * <ul>
     *     <li>If the class is {@link java.lang.Class}, it returns {@link java.lang.String String.class}.</li>
     *     <li>If the class is {@link java.lang.Object}, it returns a generated {@link java.lang.String}.</li>
     *     <li>If a {@link java.lang.reflect.TypeVariable TypeVariable} has {@link java.lang.Object}
     *         as its primary bound, it is instantiated using {@link java.lang.String}.</li>
     * </ul>
     * <p>
     * The actual object instantiation is delegated to the
     * <a href="https://www.instancio.org">Instancio library</a>, which handles
     * the population of fields and generic type arguments.
     *
     * @param clazz the {@link Class} to be instantiated
     * @return a fully populated object of the requested type, or a {@link String}-based
     * alternative according to the mapping rules
     * @throws org.instancio.exception.InstancioException if Instancio fails to create the object
     */
    public static Object createObject(final Class<?> clazz) {
        final TypeVariable<?>[] typeAttributes = clazz.getTypeParameters();
        final Object object;
        if (typeAttributes.length > 0) {
            final List<Class<?>> typedParameters = new ArrayList<>();
            for (int i = 0; i < typeAttributes.length; i++) {
                final TypeVariable<?> typeAttribute = typeAttributes[i];
                Class<?>[] bounds = TestPojoUtils.typesToClasses(typeAttribute.getBounds());
                if (bounds.length > 0) {
                    if (bounds[0].equals(Object.class)) {
                        typedParameters.add(String.class);
                    } else {
                        typedParameters.add(bounds[0]);
                    }
                }
            }
            if (clazz.equals(java.lang.Class.class)) {
                object = java.lang.String.class;
            } else {
                object = Instancio.of(clazz)
                        .withTypeParameters(typedParameters.toArray(new Class[0]))
                        .create();
            }
        } else {
            if (clazz.equals(Object.class)) {
                object = Instancio.create(String.class);
            } else {
                object = Instancio.create(clazz);
            }

        }
        return object;
    }
}
