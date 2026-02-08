package io.github.temesoft.testpojo;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ManualClassFinderTest {

    @Test
    public void testFindAllClassesUsingClassLoader() {
        final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader("io.github.temesoft.testpojo");
        assertNotNull(classes);
        assertTrue(classes.contains(TestPojoTest.class));
        assertTrue(classes.contains(ManualClassFinderTest.class));
    }

    @Test
    public void testFindAllClassesUsingClassLoader_NoPackageFound() {
        final RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> {
                    ManualClassFinder.findAllClassesUsingClassLoader("bad-package-name");
                }
        );
        assertEquals("Unable to find classes using ClassLoader in package: bad-package-name", thrown.getMessage());
    }

    @Test
    public void testGetClass() {
        final Class<?> stringClass = ManualClassFinder.getClass("String.class", "java.lang");
        assertNotNull(stringClass);
        assertEquals("java.lang.String", stringClass.getName());
    }

    @Test
    public void testGetClass_ClassNotFound() {
        final RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> {
                    ManualClassFinder.getClass("MyPojo.class", "bad.package");
                }
        );
        assertEquals("Class 'MyPojo.class' not found", thrown.getMessage());
    }
}