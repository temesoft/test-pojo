package com.temesoft.test;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TestPojo {

    final Class<?> clazz;
    final String packageName;
    private Collection<String> excludeMethods;

    private TestPojo(final Class<?> clazz, final String packageName) {
        this.clazz = clazz;
        this.packageName = packageName;
    }

    public static TestPojo processClass(final Class<?> clazz) {
        return new TestPojo(clazz, null);
    }

    public static TestPojo processPackage(final String packageName) {
        return new TestPojo(null, packageName);
    }

    public TestPojo excludeMethodsContaining(final Collection<String> excludeMethods) {
        this.excludeMethods = excludeMethods;
        return this;
    }

    public TestPojo excludeMethodsContaining(final String... excludeMethodsContaining) {
        this.excludeMethods = List.of(excludeMethodsContaining);
        return this;
    }

    public TestPojo excludeMethodContaining(final String excludeMethod) {
        this.excludeMethods = List.of(excludeMethod);
        return this;
    }

    public TestPojo testRandom() {
        if (clazz != null) {
            new TestPojoRandom(clazz, excludeMethods).testClass();
        } else if (packageName != null) {
            final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader(packageName);
            classes.forEach(aClass -> new TestPojoRandom(aClass, excludeMethods).testClass());
        }
        return this;
    }

    public TestPojo testSettersGetters() {
        if (clazz != null) {
            new TestPojoSetterGetter(clazz, excludeMethods).testClass();
        } else if (packageName != null) {
            final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader(packageName);
            classes.forEach(aClass -> new TestPojoSetterGetter(aClass, excludeMethods).testClass());
        }
        return this;
    }

    public TestPojo testEqualsAndHashCode() {
        if (clazz != null) {
            new TestPojoEqualsAndHashCode(clazz, excludeMethods).testClass();
        } else if (packageName != null) {
            final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader(packageName);
            classes.forEach(aClass -> new TestPojoEqualsAndHashCode(aClass, excludeMethods).testClass());
        }
        return this;
    }

    public TestPojo testToString() {
        if (clazz != null) {
            new TestPojoToString(clazz, excludeMethods).testClass();
        } else if (packageName != null) {
            final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader(packageName);
            classes.forEach(aClass -> new TestPojoToString(aClass, excludeMethods).testClass());
        }
        return this;
    }
}
