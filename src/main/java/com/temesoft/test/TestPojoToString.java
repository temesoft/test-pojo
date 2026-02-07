package com.temesoft.test;

import com.temesoft.test.exception.TestPojoToStringException;
import org.instancio.Instancio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import static com.temesoft.test.TestPojoUtils.isMethodExcluded;

public class TestPojoToString {

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    TestPojoToString(final Class<?> clazz,
                     final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    void testClass() {
        System.out.println("========== " + this.getClass().getSimpleName() + " ==========; Testing " + clazz.getName());
        final Object objectRandom1 = Instancio.create(clazz);
        final Object objectRandom2 = Instancio.create(clazz);
        final Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            if (method.getName().equals("toString")
                    && method.getReturnType().equals(String.class)
                    && method.getParameterCount() == 0
                    && !isMethodExcluded(method, excludeMethods)) {
                try {
                    final String response1 = (String) method.invoke(objectRandom1);
                    final String response2 = (String) method.invoke(objectRandom2);
                    final String response2Repeat = (String) method.invoke(objectRandom2);
                    if (response1.equals(response2)) {
                        throw new TestPojoToStringException(method, "Two objects with different attributes should return different toString() value");
                    }
                    if (!response2Repeat.equals(response2)) {
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
