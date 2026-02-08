package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoSetterGetterException;
import org.instancio.Instancio;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import static io.github.temesoft.testpojo.TestPojoUtils.getGetterMethodWithNameAndReturnType;
import static io.github.temesoft.testpojo.TestPojoUtils.getSetterMethodWithNameAndArgumentType;
import static io.github.temesoft.testpojo.TestPojoUtils.isMethodExcluded;

final class TestPojoSetterGetter {

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    TestPojoSetterGetter(final Class<?> clazz,
                         final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    void testClass() {
        final Object object = Instancio.create(clazz);
        final Method[] methods = clazz.getMethods();
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            Method setterMethodFound = getSetterMethodWithNameAndArgumentType(methods, "set" + field.getName(), field.getType());
            if (setterMethodFound == null) {
                setterMethodFound = getSetterMethodWithNameAndArgumentType(methods, "set_" + field.getName(), field.getType());
            }
            if (setterMethodFound == null) {
                setterMethodFound = getSetterMethodWithNameAndArgumentType(methods, field.getName(), field.getType());
            }

            Method getterMethodFound = getGetterMethodWithNameAndReturnType(methods, "get" + field.getName(), field.getType());
            if (getterMethodFound == null) {
                getterMethodFound = getGetterMethodWithNameAndReturnType(methods, "get_" + field.getName(), field.getType());
            }
            if (getterMethodFound == null) {
                getterMethodFound = getGetterMethodWithNameAndReturnType(methods, "is" + field.getName(), field.getType());
            }
            if (getterMethodFound == null) {
                getterMethodFound = getGetterMethodWithNameAndReturnType(methods, field.getName(), field.getType());
            }

            final Object value = Instancio.create(field.getType());
            if (setterMethodFound != null
                    && getterMethodFound != null
                    && !isMethodExcluded(setterMethodFound, excludeMethods)
                    && !isMethodExcluded(getterMethodFound, excludeMethods)) {
                try {
                    setterMethodFound.invoke(object, value);
                    final Object result = getterMethodFound.invoke(object);
                    if (result == null || !result.equals(value)) {
                        throw new TestPojoSetterGetterException(setterMethodFound, getterMethodFound, value, result);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Method invocation exception: " + e.getMessage(), e);
                }
            }
        }
    }
}
