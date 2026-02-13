package io.github.temesoft.testpojo;

import com.google.common.reflect.ClassPath;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

public class ManualClassFinderTest {

    @Test
    public void testFindAllClassesUsingClassLoader() {
        final Set<Class<?>> classes = ManualClassFinder.findAllClassesUsingClassLoader("io.github.temesoft.testpojo");
        assertNotNull(classes);
        assertTrue(classes.contains(TestPojoTest.class));
        assertTrue(classes.contains(ManualClassFinderTest.class));
    }

    @Test
    public void testFindAllClassesUsingClassLoader_ExceptionThrown() {
        try (final MockedStatic<ClassPath> mockedClassPath = mockStatic(ClassPath.class)) {
            mockedClassPath.when(() -> {
                        final ClassPath unused = ClassPath.from(any(ClassLoader.class));
                    })
                    .thenThrow(new IOException("Simulated IO failure"));
            final RuntimeException thrown = assertThrows(
                    RuntimeException.class,
                    () -> {
                        ManualClassFinder.findAllClassesUsingClassLoader("bad-package-name");
                    }
            );
            assertEquals("Unable to scan classpath for package: bad-package-name", thrown.getMessage());
        }
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