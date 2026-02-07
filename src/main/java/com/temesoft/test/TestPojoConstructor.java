package com.temesoft.test;

import com.temesoft.test.exception.TestPojoConstructorException;
import org.instancio.Instancio;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestPojoConstructor {

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
            } catch (InstantiationException e) {
                throw new TestPojoConstructorException(constructor, "Instantiation exception: " + e.getMessage());
            } catch (IllegalAccessException e) {
                throw new TestPojoConstructorException(constructor, "Illegal access exception: " + e.getMessage());
            } catch (InvocationTargetException e) {
                throw new TestPojoConstructorException(constructor, "Invocation target exception: " + e.getMessage());
            }
        }
    }
}
