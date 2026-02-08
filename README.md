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
- **Package-level scanning** to test multiple classes at once
- **Fluent API** for readable test code
- **Minimal dependencies** - just Instancio for random data generation
- **Wide JDK support** - Support JDK 11, 17, 21, 25

## Table of Contents

- [Quick Start](#quick-start)
- [Installation](#installation)
- [Usage](#usage)
    - [Basic Examples](#basic-examples)
    - [Testing Individual Classes](#testing-individual-classes)
    - [Testing Entire Packages](#testing-entire-packages)
    - [Excluding Methods](#excluding-methods)
    - [Available Test Methods](#available-test-methods)
- [Advanced Usage](#advanced-usage)
- [How It Works](#how-it-works)
- [Requirements](#requirements)
- [Contributing](#contributing)
- [License](#license)

## Quick Start

```java
import com.temesoft.test.TestPojo;
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

That's it! This will comprehensively test your POJO's getters, setters, equals, hashCode, and toString methods.

## Installation

### Maven

```xml

<dependency>
    <groupId>io.github.temesoft</groupId>
    <artifactId>test-pojo</artifactId>
    <version>1.0.1</version>
    <scope>test</scope>
</dependency>
```

### Gradle

```gradle
testImplementation 'io.github.temesoft:test-pojo:1.0.1'
```

## Usage

### Basic Examples

#### Example 1: Testing a Simple POJO

```java
public class User {
    private String name;
    private String email;
    private int age;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, age);
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', email='" + email + "', age=" + age + "}";
    }
}
```

**Test class:**

```java
import com.temesoft.test.TestPojo;
import org.junit.Test;

public class UserTest {

    @Test
    public void testUserPojo() {
        TestPojo.processClass(User.class)
                .testSettersGetters()      // Validates all getters/setters work correctly
                .testEqualsAndHashCode()    // Validates equals() and hashCode() contracts
                .testToString();            // Validates toString() consistency
    }
}
```

#### Example 2: Testing an Immutable POJO

```java
public class Product {
    private final String id;
    private final String name;
    private final double price;

    public Product(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 &&
                Objects.equals(id, product.id) &&
                Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price);
    }

    @Override
    public String toString() {
        return "Product{id='" + id + "', name='" + name + "', price=" + price + "}";
    }
}
```

**Test class:**

```java

@Test
public void testProductPojo() {
    TestPojo.processClass(Product.class)
            .testConstructor()          // Tests all constructors with random data
            .testEqualsAndHashCode()
            .testToString();
    // Note: No testSettersGetters() since Product is immutable
}
```

### Testing Individual Classes

You can test one or more specific classes:

```java

@Test
public void testMultiplePojos() {
    TestPojo.processClass(User.class, Product.class, Order.class)
            .testSettersGetters()
            .testEqualsAndHashCode()
            .testToString()
            .testConstructor();
}
```

### Testing Entire Packages

Test all POJOs in a package at once:

```java

@Test
public void testAllModels() {
    TestPojo.processPackage("com.mycompany.model")
            .testSettersGetters()
            .testEqualsAndHashCode()
            .testToString();
}
```

This will automatically discover and test all classes in the `com.mycompany.model` package.

### Excluding Methods

Sometimes you need to exclude certain methods from testing:

#### Exclude by Method Name Pattern

```java

@Test
public void testWithExclusions() {
    TestPojo.processClass(MyPojo.class)
            .excludeMethodsContaining("getInternalState", "setDebugMode", "MyService.getDetails")
            .testSettersGetters()
            .testEqualsAndHashCode();
}
```

#### Exclude Single Method

```java

@Test
public void testExcludeSingleMethod() {
    TestPojo.processClass(MyPojo.class)
            .excludeMethodContaining("getCalculatedValue")
            .testSettersGetters();
}
```

#### Exclude by Collection

```java

@Test
public void testExcludeMultiple() {
    List<String> exclusions = Arrays.asList(
            "getClass",
            "wait",
            "notify"
    );

    TestPojo.processClass(MyPojo.class)
            .excludeMethodsContaining(exclusions)
            .testSettersGetters();
}
```

### Available Test Methods

| Method                    | Description                   | What It Tests                                                                                                             |
|---------------------------|-------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| `testSettersGetters()`    | Tests getter/setter pairs     | Validates that setting a value via setter and retrieving via getter returns the same value                                |
| `testEqualsAndHashCode()` | Tests equals() and hashCode() | Validates equals contract (null check, type check, reflexivity) and hashCode consistency                                  |
| `testToString()`          | Tests toString() method       | Validates toString() returns consistent results for the same object                                                       |
| `testConstructor()`       | Tests all constructors        | Validates all public constructors can be invoked with random data                                                         |
| `testRandom()`            | Random instantiation test     | Tests that class can be instantiated with random field values, calls all public methods (including ones taking arguments) |

### Chaining Methods

All test methods return the `TestPojo` instance, allowing for fluent chaining:

```java

@Test
public void testEverything() {
    TestPojo.processClass(MyPojo.class)
            .testRandom()
            .testConstructor()
            .testSettersGetters()
            .testEqualsAndHashCode()
            .testToString();
}
```

## Advanced Usage

### Testing POJOs with Special Naming Conventions

test-pojo handles various getter/setter naming conventions:

- Standard JavaBeans: `getName()` / `setName()`
- Boolean getters: `isActive()` / `setActive()`
- Underscore prefix: `get_name()` / `set_name()`
- Direct field access: `name()` / `name(String value)`

```java
public class FlexibleNaming {
    private String name;
    private boolean active;

    // Standard
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Boolean prefix
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

@Test
public void testFlexibleNaming() {
    TestPojo.processClass(FlexibleNaming.class)
            .testSettersGetters();  // Works with all naming conventions!
}
```

### Testing Nested POJOs

test-pojo works well with nested objects:

```java
public class Address {
    private String street;
    private String city;
    private String zipCode;
    // getters, setters, equals, hashCode, toString...
}

public class Customer {
    private String name;
    private Address address;  // Nested POJO
    // getters, setters, equals, hashCode, toString...
}

@Test
public void testNestedPojos() {
    // Test both classes
    TestPojo.processClass(Address.class, Customer.class)
            .testSettersGetters()
            .testEqualsAndHashCode();
}
```

### Integration with Existing Tests

test-pojo integrates seamlessly with your existing tests:

```java

@Test
public void comprehensiveUserTest() {
    User user = new User();

    // Your custom business logic tests
    user.setName("John Doe");
    assertEquals("John Doe", user.getName());

    // ... more custom tests ...

    // Add automated POJO testing
    TestPojo.processClass(User.class)
            .testSettersGetters()
            .testEqualsAndHashCode()
            .testToString();
}
```

### Testing with JUnit 5

Works perfectly with JUnit 5:

```java
import org.junit.jupiter.api.Test;
import com.temesoft.test.TestPojo;

class UserTest {

    @Test
    void shouldValidateUserPojo() {
        TestPojo.processClass(User.class)
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString();
    }

    @Test
    void shouldValidateAllModelClasses() {
        TestPojo.processPackage("com.myapp.model")
                .excludeMethodsContaining("getClass")
                .testSettersGetters()
                .testEqualsAndHashCode();
    }
}
```

## How It Works

1. **Reflection-based**: test-pojo uses Java reflection to discover fields, methods, and constructors
2. **Random data generation**: Uses [Instancio](https://www.instancio.org/) to generate random test data
3. **Intelligent matching**: Automatically matches getters with setters based on field names and types
4. **Contract validation**: Verifies standard Java contracts (equals symmetry, hashCode consistency, etc.)

### What Gets Tested?

#### testSettersGetters()

- Finds all fields in the class
- Locates corresponding getter and setter methods
- Sets a random value via setter
- Retrieves via getter and verifies it matches

#### testEqualsAndHashCode()

For `equals()`:

- Null comparison returns false
- Comparison with different type returns false
- Two different random objects are not equal
- Same object compared with itself returns true

For `hashCode()`:

- Two different random objects have different hash codes

#### testToString()

- Calling toString() multiple times on same object returns same result

#### testConstructor()

- All public constructors can be invoked
- Random data can be used for all parameter types

#### testRandom()

- All public methods can be invoked with random data arguments when required
- Random data can be used for all parameter types

## Requirements

- **Java**: 11 or higher
- **JUnit**: 4.x or 5.x (for running tests)
- **Instancio**: 5.5.1+ (automatically included)

## Limitations and Best Practices

### Limitations

1. **Hash collisions**: The `testEqualsAndHashCode()` method assumes two randomly generated objects will have different
   hash codes. While extremely rare, hash collisions are theoretically possible.
2. **Complex constructors**: Constructors with complex validation logic might fail if random data doesn't meet
   requirements. Use `excludeMethodsContaining()` to skip these.
3. **Lazy initialization**: Fields initialized lazily might not be tested correctly by getters/setters.
4. **Static methods**: Only instance methods are tested.

### Best Practices

1. **Combine with custom tests**: Use test-pojo for boilerplate validation, but write custom tests for business logic
2. **Exclude when needed**: Use exclusion methods for methods with special behavior
3. **Test packages carefully**: When using `processPackage()`, ensure all classes in the package are valid POJOs
4. **Document exclusions**: Comment why certain methods are excluded

```java

@Test
public void testUserPojo() {
    TestPojo.processClass(User.class)
            // Exclude calculated field that has no setter
            .excludeMethodContaining("getFullName")
            .testSettersGetters()
            .testEqualsAndHashCode();
}
```

**ModelPackageTest.java:**

```java
public class ModelPackageTest {

    @Test
    public void testAllModelPojos() {
        TestPojo.processPackage("com.mycompany.model")
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString()
                .testConstructor();
    }
}
```

## Troubleshooting

### "No such method" errors

If you get errors about missing getters/setters, check:

- Method naming follows JavaBeans conventions
- Field types match exactly (e.g., `boolean` vs `Boolean`)
- Methods are public

### Constructor test failures

If constructor tests fail:

- Ensure constructors don't have validation that rejects random data
- Use `excludeMethodsContaining()` to skip problematic constructors
- Verify all parameter types can be instantiated by Instancio

### Package scanning issues

If `processPackage()` doesn't find classes:

- Verify the package name is correct
- Ensure classes are compiled
- Check that classes are accessible (public)

## Performance Tips

- **Package scanning** can be slow for large packages; consider testing specific classes instead
- **Exclusions** are evaluated using string matching; keep exclusion lists small
- **Multiple test runs** on same classes are independent; no caching between runs

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

## Support

- 📚 [Documentation](https://javadoc.io/doc/io.github.temesoft/test-pojo)
- 🐛 [Issue Tracker](https://github.com/temesoft/test-pojo/issues)
