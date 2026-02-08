package io.github.temesoft.testpojo;

import org.instancio.Instancio;

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
        final Object object = Instancio.create(clazz);
        final Method[] methods = object.getClass().getMethods();
        for (final Method method : methods) {
            if (!TestPojoUtils.isMethodExcluded(method, excludeMethods)
                    && !method.toString().contains("java.lang.Object.notify()")
                    && !method.toString().contains("java.lang.Object.notifyAll()")
                    && !method.toString().contains("java.lang.Object.wait(long)")
                    && !method.toString().contains("java.lang.Object.wait()")
                    && !method.toString().contains("java.lang.Object.wait(long,int)")
                    && !method.toString().contains("java.lang.Throwable.printStackTrace(java.io.PrintWriter)")
                    && !method.toString().contains("java.lang.Throwable.initCause(java.lang.Throwable)")
                    && method.canAccess(object)) {
                final List<Object> invokeParameters = new ArrayList<>();
                final Parameter[] parameters = method.getParameters();
                for (final Parameter parameter : parameters) {
                    final Object parameterValue = Instancio.create(parameter.getType());
                    invokeParameters.add(parameterValue);
                }
                try {
                    final Object unused = method.invoke(object, invokeParameters.toArray(new Object[0]));
                } catch (Exception e) {
                    throw new RuntimeException("Method invocation exception: " + e.getMessage(), e);
                }
            }
        }
    }
}
