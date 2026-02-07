package com.temesoft.test;

import org.instancio.Instancio;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class TestPojoRandom {

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    TestPojoRandom(final Class<?> clazz,
                   final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    void testClass() {
        System.out.println("========== " + this.getClass().getSimpleName() + " ==========; Testing " + clazz.getName());
        final Object object = Instancio.create(clazz);
        final Method[] methods = object.getClass().getMethods();
        for (final Method method : methods) {
            if (!TestPojoUtils.isMethodExcluded(method, excludeMethods)
                    && !method.toString().contains("java.lang.Object.notify()")
                    && !method.toString().contains("java.lang.Object.notifyAll()")
                    && !method.toString().contains("java.lang.Object.wait(long)")
                    && !method.toString().contains("java.lang.Object.wait()")
                    && !method.toString().contains("java.lang.Object.wait(long,int)")
                    && method.canAccess(object)) {
                System.out.println("Method: " + method);
                final List<Object> invokeParameters = new ArrayList<>();
                final Parameter[] parameters = method.getParameters();
                for (final Parameter parameter : parameters) {
                    System.out.println("\t Parameter: " + parameter);
                    final Object parameterValue = Instancio.create(parameter.getType());
                    invokeParameters.add(parameterValue);
                }
                try {
                    final Object unused = method.invoke(object, invokeParameters.toArray(new Object[0]));
                    if (!method.getReturnType().equals(void.class)) {
                        System.out.println("\t Result: " + unused);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Illegal access exception; method: " + method, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Invocation target exception; method: " + method, e);
                }
            }
        }
    }
}
