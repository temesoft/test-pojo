package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoConstructorException;
import org.instancio.Instancio;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

final class TestPojoConstructor {

    final Class<?> clazz;
    final Collection<String> excludeMethods;

    TestPojoConstructor(final Class<?> clazz,
                        final Collection<String> excludeMethods) {
        this.clazz = clazz;
        this.excludeMethods = excludeMethods;
    }

    void testClass() {
        final Constructor<?>[] constructors = clazz.getConstructors();
        for (final Constructor<?> constructor : constructors) {
            final List<Object> arguments = new ArrayList<>();
            final Parameter[] parameters = constructor.getParameters();
            for (final Parameter parameter : parameters) {
                if (parameter.getType().equals(Constructor.class)) {
                    arguments.add(null);
                } else {
                    arguments.add(Instancio.create(parameter.getType()));
                }
            }
            try {
                final Object unused = constructor.newInstance(arguments.toArray(new Object[0]));
            } catch (Exception e) {
                throw new TestPojoConstructorException(constructor, "Constructor instantiation exception: " + e.getMessage());
            }
        }
    }
}
