package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoToStringException;
import org.instancio.Instancio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import static io.github.temesoft.testpojo.TestPojoUtils.isMethodExcluded;

final class TestPojoToString {

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    TestPojoToString(final Class<?> clazz,
                     final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    void testClass() {
        final Object objectRandom = Instancio.create(clazz);
        final Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            if (method.getName().equals("toString")
                    && method.getReturnType().equals(String.class)
                    && method.getParameterCount() == 0
                    && !isMethodExcluded(method, excludeMethods)) {
                try {
                    final String response = (String) method.invoke(objectRandom);
                    final String responseRepeat = (String) method.invoke(objectRandom);
                    if (!responseRepeat.equals(response)) {
                        throw new TestPojoToStringException(method, "Same unchanged object should return same toString() value every time");
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
