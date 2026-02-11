# test-pojo

[![Java CI](https://github.com/temesoft/test-pojo/actions/workflows/main.yml/badge.svg)](https://github.com/temesoft/test-pojo/actions/workflows/main.yml)
[![Javadoc](https://javadoc.io/badge2/io.github.temesoft/test-pojo/javadoc.svg)](https://javadoc.io/doc/io.github.temesoft/test-pojo)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.temesoft/test-pojo.svg)](https://central.sonatype.com/artifact/io.github.temesoft/test-pojo)
<img src='https://raw.githubusercontent.com/temesoft/test-pojo/refs/heads/main/jacoco.svg' alt='Test coverage (jacoco)' title='Test coverage (jacoco)'>

> **test-pojo** is a lightweight Java testing library that automatically validates your Plain Old Java Objects (POJOs).
> It uses reflection and the [Instancio](https://www.instancio.org/) library to thoroughly test all public methods,
> getters, setters, `equals()`, `hashCode()`, `toString()`, and constructors with minimal boilerplate code.

## Why test-pojo?

Writing comprehensive tests for POJOs is tedious and repetitive. **test-pojo** automates this process:

- **Automatic validation** of getters and setters
- **Equals/HashCode contract** verification
- **ToString consistency** checking
- **Constructor testing** with random data
- **Random method invocation** with generated parameters
- **Package-level scanning** to test multiple classes at once
- **Fluent API** for readable test code
- **Powerful filtering** with predicates for fine-grained control
- **Comprehensive error messages** with detailed diagnostics
- **Minimal dependencies** - just Instancio for random data generation
- **Wide JDK support** - Support JDK 11, 17, 21, 25
- **Slf4j logging** debug and trace level logging
- **Test reporting** - Generate and export test execution reports

## Table of Contents

- [Quick Start](#quick-start)
- [Available Test Methods](#available-test-methods)
- [Installation](#installation)
- [Usage](#usage)
    - [Basic Examples](#basic-examples)
    - [Filtering with Predicates](#filtering-with-predicates)
    - [Excluding Methods and Classes](#excluding-methods-and-classes)
- [Advanced Usage](#advanced-usage)
    - [Test Reporting](#test-reporting)
    - [Combining Filters and Exclusions](#combining-filters-and-exclusions)
- [Requirements](#requirements)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Quick Start

```java
import io.github.temesoft.testpojo.TestPojo;
import org.junit.Test;

public class MyPojoTest {
    @Test
    public void testMyPojo() {
        TestPojo.processClass(MyPojo.class)
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString();
    }
}
```

## Available Test Methods

| Method                    | Description                                    | What It Tests                                                                                                             |
|---------------------------|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| `testSettersGetters()`    | Tests getter/setter pairs                      | Validates that setting a value via setter and retrieving via getter returns the same value                                |
| `testEqualsAndHashCode()` | Tests equals() and hashCode()                  | Validates equals contract (null check, type check, reflexivity) and hashCode consistency                                  |
| `testToString()`          | Tests toString() method                        | Validates toString() returns consistent results for the same object                                                       |
| `testConstructor()`       | Tests all constructors                         | Validates all public constructors can be invoked with random data                                                         |
| `testRandom()`            | Random instantiation and method invocation     | Tests that class can be instantiated with random field values, calls all public methods (including ones taking arguments) |
| `testAll()`               | Runs all available tests                       | Equivalent to calling testRandom(), testConstructor(), testSettersGetters(), testEqualsAndHashCode(), and testToString()  |


## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.temesoft</groupId>
    <artifactId>test-pojo</artifactId>
    <version>1.0.3</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```gradle
testImplementation 'io.github.temesoft:test-pojo:1.0.3'
```

## Usage

### Basic Examples

Test a simple POJO:

```java
@Test
public void testUserPojo() {
    TestPojo.processClass(User.class)
            .testSettersGetters()
            .testEqualsAndHashCode()
            .testToString();
}
```

Test multiple classes:

```java
@Test
public void testMultiplePojos() {
    TestPojo.processClass(User.class, Product.class, Order.class)
            .testAll(); 
}
```

Test an entire package:

```java
@Test
public void testAllModels() {
    TestPojo.processPackage("com.mycompany.model")
            .testAll();
}
```

### Filtering with Predicates

Predicates provide powerful, programmatic filtering for fine-grained control over what gets tested.

#### Filter Classes

Only test concrete classes (skip abstract classes and interfaces):

```java
@Test
public void testOnlyConcreteClasses() {
    TestPojo.processPackage("com.mycompany.model")
            .filterClasses(clazz -> 
                !Modifier.isAbstract(clazz.getModifiers()) &&
                !clazz.isInterface())
            .testAll();
}
```

Only test classes with specific annotations:

```java
@Test
public void testEntities() {
    TestPojo.processPackage("com.mycompany.model")
            .filterClasses(clazz -> clazz.isAnnotationPresent(Entity.class))
            .testSettersGetters();
}
```

Only test DTOs (by naming convention):

```java
@Test
public void testDtos() {
    TestPojo.processPackage("com.mycompany.dto")
            .filterClasses(clazz -> clazz.getSimpleName().endsWith("Dto"))
            .testAll();
}
```

#### Filter Methods

Only test public methods:

```java
@Test
public void testPublicMethodsOnly() {
    TestPojo.processClass(MyClass.class)
            .filterMethods(method -> Modifier.isPublic(method.getModifiers()))
            .testRandom();
}
```

Exclude deprecated methods:

```java
@Test
public void testNonDeprecatedMethods() {
    TestPojo.processClass(MyClass.class)
            .filterMethods(method -> !method.isAnnotationPresent(Deprecated.class))
            .testRandom();
}
```

Only test methods with specific annotations:

```java
@Test
public void testValidatedMethods() {
    TestPojo.processClass(MyClass.class)
            .filterMethods(method -> method.isAnnotationPresent(Tested.class))
            .testRandom();
}
```

Complex method filtering:

```java
@Test
public void testComplexFilter() {
    TestPojo.processClass(MyClass.class)
            .filterMethods(method -> 
                Modifier.isPublic(method.getModifiers()) &&
                !method.isAnnotationPresent(Deprecated.class) &&
                method.getParameterCount() <= 3)
            .testRandom();
}
```

#### Filter Constructors

Only test the no-arg constructor:

```java
@Test
public void testNoArgConstructor() {
    TestPojo.processClass(MyClass.class)
            .filterConstructors(constructor -> constructor.getParameterCount() == 0)
            .testConstructor();
}
```

Only test constructors with 3 or fewer parameters:

```java
@Test
public void testSimpleConstructors() {
    TestPojo.processClass(MyClass.class)
            .filterConstructors(constructor -> constructor.getParameterCount() <= 3)
            .testConstructor();
}
```

### Excluding Methods and Classes

String-based exclusions are still supported:

#### Exclude Methods by Name Pattern

```java
@Test
public void testWithExclusions() {
    TestPojo.processClass(MyPojo.class)
            .excludeMethodsContaining("getInternalState", "setDebugMode")
            .testSettersGetters();
}
```

#### Exclude Classes

```java
@Test
public void testWithClassExclusions() {
    TestPojo.processClass(User.class, Product.class, Order.class)
            .excludeClasses(Order.class)
            .testAll();
}
```

Or when testing a package:

```java
@Test
public void testPackageWithExclusions() {
    TestPojo.processPackage("com.mycompany.model", 
            AbstractBase.class,
            BaseInterface.class)
            .testAll();
}
```

## Advanced Usage

### Test Reporting

Generate detailed reports of test execution:

#### Print Report to Console

```java
@Test
public void testWithReport() {
    TestPojo.processPackage("com.mycompany.model")
            .testAll()
            .printReport();
}
```

#### Get Report as String

```java
@Test
public void testAndGetReport() {
    TestPojo testPojo = TestPojo.processPackage("com.mycompany.model")
            .testAll();
    
    String report = testPojo.getReport();
    // Use report as needed
}
```

#### Save Report to File

```java
@Test
public void testAndSaveReport() throws IOException {
    Path reportPath = Paths.get("target/test-reports/pojo-test-report.txt");
    TestPojo.processPackage("com.mycompany.model")
            .testAll()
            .saveReport(reportPath);
}
```

### Combining Filters and Exclusions

Predicates and string-based exclusions work together:

```java
@Test
public void testCombinedFilters() {
    TestPojo.processPackage("com.mycompany.model")
            // Predicate: only concrete classes
            .filterClasses(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
            // Predicate: exclude deprecated methods
            .filterMethods(method -> !method.isAnnotationPresent(Deprecated.class))
            // String exclusion: skip specific methods
            .excludeMethodsContaining("toString", "getClass")
            .testAll();
}
```

All filters are applied:
1. String-based method exclusions
2. Class predicate filter
3. Method predicate filter
4. Constructor predicate filter

A class/method/constructor must pass ALL filters to be tested.

## Requirements

- **Java**: 11 or higher
- **JUnit**: 4.x or 5.x
- **Instancio**: 5.5.1+ (automatically included)
- **SLF4J**: 2.0.0+ (for logging)

## Troubleshooting

### "No such method" errors

If you get errors about missing getters/setters, check:

- Method naming follows JavaBeans conventions
- Field types match exactly (e.g., `boolean` vs `Boolean`)
- Methods are public
- Getter has no parameters and returns the field type
- Setter has one parameter of the field type and returns void (or the object for builder pattern)
- Use `filterMethods()` or `excludeMethodsContaining()` to skip problematic methods

### Constructor test failures

If constructor tests fail:

- Ensure constructors don't have validation that rejects random data
- Verify all parameter types can be instantiated by Instancio
- Check for null pointer exceptions in constructor logic
- Use `filterConstructors()` to skip problematic constructors

### Package scanning issues

If `processPackage()` doesn't find classes:

- Verify the package name is correct and fully qualified
- Ensure classes are compiled
- Check that classes are accessible (public)
- Verify the ClassLoader can find the package resources

### TestPojoRawUseException

If you get raw use exceptions:

- Add generic type parameters to all Collection and Map declarations
- Example: Change `List items` to `List<String> items`
- Example: Change `Map map` to `Map<String, Integer> map`

### toString() consistency failures

If toString() tests fail:

- Don't include timestamps or random values in toString()
- Don't include thread IDs or other non-deterministic data
- Ensure toString() doesn't modify object state
- Make toString() depend only on object fields

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to
discuss what you would like to change.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

* the freedom to use the software for any purpose,
* the freedom to change the software to suit your needs,
* the freedom to share the software with your friends and neighbors
* the freedom to share the changes you make.

## Acknowledgments

- [Instancio](https://www.instancio.org/) for excellent random data generation
- All contributors and users of test-pojo
