package com.temesoft.test;

import com.temesoft.test.exception.TestPojoEqualsException;
import com.temesoft.test.exception.TestPojoHashCodeException;
import org.instancio.Instancio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;

import static com.temesoft.test.TestPojoUtils.isMethodExcluded;

final class TestPojoEqualsAndHashCode {

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    TestPojoEqualsAndHashCode(final Class<?> clazz,
                              final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    void testClass() {
        final Object objectRandom1 = Instancio.create(clazz);
        final Object objectRandom2 = Instancio.create(clazz);
        final Method[] methods = clazz.getMethods();
        for (final Method method : methods) {
            if (method.getName().equals("equals")
                    && method.getReturnType().equals(boolean.class)
                    && method.getParameterCount() == 1
                    && method.getParameters()[0].getType().equals(Object.class)
                    && !isMethodExcluded(method, excludeMethods)) {
                try {
                    boolean response = (boolean) method.invoke(objectRandom1, (Object) null);
                    if (response) {
                        throw new TestPojoEqualsException(method, "Equals should not return true when null is passed as argument");
                    }
                    response = (boolean) method.invoke(objectRandom1, this);
                    if (response) {
                        throw new TestPojoEqualsException(method, "Equals should not return true when object of different type is passed as argument");
                    }
                    response = (boolean) method.invoke(objectRandom1, objectRandom2);
                    if (response) {
                        throw new TestPojoEqualsException(method, "Two objects with random attributes should not equal");
                    }
                    response = (boolean) method.invoke(objectRandom1, objectRandom1);
                    if (!response) {
                        throw new TestPojoEqualsException(method, "Same object should be equal");
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Illegal access exception", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Invocation target exception", e);
                }
            } else if (method.getName().equals("hashCode")
                    && method.getReturnType().equals(int.class)
                    && method.getParameterCount() == 0
                    && !isMethodExcluded(method, excludeMethods)) {
                try {
                    final int response1 = (int) method.invoke(objectRandom1);
                    final int response2 = (int) method.invoke(objectRandom2);
                    if (response1 == response2) {
                        throw new TestPojoHashCodeException(method, "Two objects with different attributes should return different hashCode value");
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
