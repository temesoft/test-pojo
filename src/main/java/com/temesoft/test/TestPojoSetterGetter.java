package com.temesoft.test;

import com.temesoft.test.exception.TestPojoSetterGetterException;
import org.instancio.Instancio;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import static com.temesoft.test.TestPojoUtils.getGetterMethodWithNameAndReturnType;
import static com.temesoft.test.TestPojoUtils.getSetterMethodWithNameAndArgumentType;
import static com.temesoft.test.TestPojoUtils.isMethodExcluded;

final class TestPojoSetterGetter {

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    TestPojoSetterGetter(final Class<?> clazz,
                         final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    void testClass() {
        System.out.println("========== " + this.getClass().getSimpleName() + " ==========; Testing " + clazz.getName());
        final Object object = Instancio.create(clazz);
        final Method[] methods = clazz.getMethods();
        final Field[] fields = clazz.getDeclaredFields();
        for (final Field field : fields) {
            System.out.println("field.name: " + field.getName() + "; field.type: " + field.getType());
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
                System.out.println("\tsetter method: " + setterMethodFound);
                System.out.println("\tgetter method: " + getterMethodFound);
                try {
                    setterMethodFound.invoke(object, value);
                    final Object result = getterMethodFound.invoke(object);
                    if (result == null || !result.equals(value)) {
                        throw new TestPojoSetterGetterException(setterMethodFound, getterMethodFound, value, result);
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
