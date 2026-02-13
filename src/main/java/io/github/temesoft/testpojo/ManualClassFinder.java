package io.github.temesoft.testpojo;

import com.google.common.reflect.ClassPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for discovering and loading all classes within a specified package
 * using the system class loader.
 * <p>
 * This class provides functionality to scan packages and retrieve all class definitions
 * contained within them. It uses the classpath resources to locate .class files and
 * loads them dynamically.
 * </p>
 * <p>
 * This class cannot be instantiated as it only provides static utility methods.
 * </p>
 */
public class ManualClassFinder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManualClassFinder.class);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ManualClassFinder() {
    }

    /**
     * Finds and loads all classes within the specified package using the system class loader.
     * <p>
     * This method scans the package directory for all .class files and attempts to load
     * each one. The package is searched using Guava's ClassPath
     * </p>
     *
     * @param packageName the fully qualified package name to search (e.g., "com.example.myapp")
     * @return a {@link Set} of {@link Class} objects representing all classes found in the package
     * @throws RuntimeException     if the package cannot be accessed, the resource stream cannot be read,
     *                              or if any class cannot be loaded
     * @throws NullPointerException if the package does not exist or the resource stream is null
     */
    public static Set<Class<?>> findAllClassesUsingClassLoader(final String packageName) {
        LOGGER.debug("Searching for classes in package: {}", packageName);
        try {
            return ClassPath.from(Thread.currentThread().getContextClassLoader())
                    .getTopLevelClasses(packageName)
                    .stream()
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("Unable to scan classpath for package: " + packageName, e);
        }
    }

    /**
     * Loads a class by constructing its fully qualified name from the class file name and package name.
     * <p>
     * This method constructs the complete class name by combining the package name with the
     * class file name (minus the .class extension) and loads the class using {@link Class#forName(String)}.
     * </p>
     *
     * @param className   the name of the class file (e.g., "MyClass.class")
     * @param packageName the fully qualified package name containing the class
     * @return the {@link Class} object representing the loaded class
     * @throws RuntimeException if the class cannot be found or loaded, wrapping the underlying
     *                          {@link ClassNotFoundException}
     */
    static Class<?> getClass(final String className, final String packageName) {
        try {
            final String fullClassName = packageName + "."
                    + className.substring(0, className.lastIndexOf('.'));
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class '" + className + "' not found", e);
        }
    }
}
