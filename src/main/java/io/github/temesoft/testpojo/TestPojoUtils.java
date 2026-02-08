package io.github.temesoft.testpojo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;

final class TestPojoUtils {

    private TestPojoUtils() {
    }

    public static boolean isMethodExcluded(final Method method, final Collection<String> excludeMethods) {
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

    public static Method getSetterMethodWithNameAndArgumentType(final Method[] methods,
                                                                final String nameCaseIgnore,
                                                                final Class<?> argumentType) {
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(nameCaseIgnore)) {
                final Parameter[] parameters = method.getParameters();
                if (parameters.length == 1) {
                    final Parameter parameter = parameters[0];
                    if (parameter.getType().equals(argumentType)) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    public static Method getGetterMethodWithNameAndReturnType(final Method[] methods,
                                                              final String nameCaseIgnore,
                                                              final Class<?> returnType) {
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(nameCaseIgnore)) {
                if (method.getParameters().length == 0 && method.getReturnType().equals(returnType)) {
                    return method;
                }
            }
        }
        return null;
    }
}
