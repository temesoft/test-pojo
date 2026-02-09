package io.github.temesoft.testpojo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
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
     * Converts an array of {@link Type} objects to an array of {@link Class} objects.
     * <p>
     * This utility method performs an unchecked cast from {@link Type} to {@link Class},
     * which is safe when the types are known to be class types (not wildcards, type variables,
     * or parameterized types). This is typically used when extracting actual type arguments
     * from parameterized types.
     * </p>
     *
     * @param types array of types to convert, must not be {@code null}
     * @return array of {@link Class} objects corresponding to the input types
     * @throws ClassCastException if any of the types cannot be cast to {@link Class}
     */
    public static Class<?>[] typesToClasses(final Type[] types) {
        final List<Class<?>> result = new ArrayList<>();
        for (final Type type : types) {
            result.add((Class<?>) type);
        }
        return result.toArray(new Class[0]);
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
}
