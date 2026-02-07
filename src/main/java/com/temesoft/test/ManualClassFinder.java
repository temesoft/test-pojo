package com.temesoft.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class ManualClassFinder {

    public static Set<Class<?>> findAllClassesUsingClassLoader(final String packageName) {
        final String path = packageName.replaceAll("[.]", "/");
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        try (final InputStream stream = classLoader.getResourceAsStream(path);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            return reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> getClass(line, packageName))
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            throw new RuntimeException("Unable to find classes using ClassLoader", e);
        }
    }

    private static Class<?> getClass(final String className, final String packageName) {
        try {
            final String fullClassName = packageName + "." + className.substring(0, className.lastIndexOf('.'));
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class '" + className + "' not found", e);
        }
    }
}
