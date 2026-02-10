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
- **Comprehensive error messages** with detailed diagnostics
- **Minimal dependencies** - just Instancio for random data generation
- **Wide JDK support** - Support JDK 11, 17, 21, 25
- **Slf4j logging** debug and trace level logging
- **Test reporting** - Generate and export test execution reports

## Table of Contents

- [Quick Start](#quick-start)
- [Installation](#installation)
- [Usage](#usage)
    - [Basic Examples](#basic-examples)
    - [Testing Individual Classes](#testing-individual-classes)
    - [Testing Entire Packages](#testing-entire-packages)
    - [Excluding Methods and Classes](#excluding-methods-and-classes)
    - [Available Test Methods](#available-test-methods)
- [Advanced Usage](#advanced-usage)
    - [Test Reporting](#test-reporting)
    - [Testing POJOs with Special Naming Conventions](#testing-pojos-with-special-naming-conventions)
    - [Testing Nested POJOs](#testing-nested-pojos)
- [Exception Handling](#exception-handling)
- [How It Works](#how-it-works)
- [Requirements](#requirements)
- [Limitations and Best Practices](#limitations-and-best-practices)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Quick Start

```java
import io.github.temesoft.testpojo.TestPojo;
import org.junit.Test; // or use JUnit 5 (Jupiter) org.junit.jupiter.api.Test

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
import io.github.temesoft.testpojo.TestPojo;
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

#### Excluding Classes from Package Testing

When testing a package, you can exclude specific classes:

```java
@Test
public void testPackageWithExclusions() {
    TestPojo.processPackage("com.mycompany.model", 
            AbstractBase.class,    // Exclude abstract classes
            BaseInterface.class)   // Exclude interfaces
            .testSettersGetters()
            .testEqualsAndHashCode();
}
```

### Excluding Methods and Classes

#### Exclude Methods by Name Pattern

```java
@Test
public void testWithExclusions() {
    TestPojo.processClass(MyPojo.class)
            .excludeMethodsContaining("getInternalState", "setDebugMode", "MyService.getDetails")
            .testSettersGetters()
            .testEqualsAndHashCode();
}
```

#### Exclude Methods by Collection

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

#### Exclude Classes

```java
@Test
public void testWithClassExclusions() {
    TestPojo.processClass(User.class, Product.class, Order.class)
            .excludeClasses(Order.class)  // Skip Order class
            .testAll();
}

// Or with a collection
@Test
public void testWithClassExclusionsCollection() {
    List<Class<?>> exclusions = List.of(AbstractBase.class, TestHelper.class);
    
    TestPojo.processPackage("com.mycompany.model")
            .excludeClasses(exclusions)
            .testAll();
}
```

### Available Test Methods

| Method                    | Description                                    | What It Tests                                                                                                             |
|---------------------------|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|
| `testSettersGetters()`    | Tests getter/setter pairs                      | Validates that setting a value via setter and retrieving via getter returns the same value                                |
| `testEqualsAndHashCode()` | Tests equals() and hashCode()                  | Validates equals contract (null check, type check, reflexivity) and hashCode consistency                                  |
| `testToString()`          | Tests toString() method                        | Validates toString() returns consistent results for the same object                                                       |
| `testConstructor()`       | Tests all constructors                         | Validates all public constructors can be invoked with random data                                                         |
| `testRandom()`            | Random instantiation and method invocation     | Tests that class can be instantiated with random field values, calls all public methods (including ones taking arguments) |
| `testAll()`               | Runs all available tests                       | Equivalent to calling testRandom(), testConstructor(), testSettersGetters(), testEqualsAndHashCode(), and testToString()  |

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

### Test Reporting

test-pojo can generate detailed reports about test execution:

#### Print Report to Console

```java
@Test
public void testWithReport() {
    TestPojo testPojo = TestPojo.processPackage("com.mycompany.model")
            .testAll();
    
    testPojo.printReport();  // Prints report to System.out
}
```

#### Get Report as String

```java
@Test
public void testAndGetReport() {
    TestPojo testPojo = TestPojo.processPackage("com.mycompany.model")
            .testAll();
    
    String report = testPojo.getReport();
    // Use report string as needed (log it, assert on it, etc.)
}
```

#### Save Report to File

```java
@Test
public void testAndSaveReport() throws IOException {
    TestPojo testPojo = TestPojo.processPackage("com.mycompany.model")
            .testAll();
    
    Path reportPath = Paths.get("target/test-reports/pojo-test-report.txt");
    testPojo.saveReport(reportPath);
}
```

**Sample Report Output:**

```
Class: com.mycompany.model.User
	Test type: SetterGetter
		Testing field: name
		Testing field: email
	Test type: EqualsAndHashCode
		equals() contract verified
		hashCode() consistency verified
	Test type: ToString
		toString() consistency verified
Class: com.mycompany.model.Product
	Test type: Constructor
		Constructor with 3 parameters tested
	Test type: EqualsAndHashCode
		equals() contract verified
```

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

### Testing Generic Collections and Maps

test-pojo properly handles generic collections and maps:

```java
public class Order {
    private List<String> items;
    private Map<String, Integer> quantities;
    
    // Getters and setters with proper generics
    public List<String> getItems() {
        return items;
    }
    
    public void setItems(List<String> items) {
        this.items = items;
    }
    
    public Map<String, Integer> getQuantities() {
        return quantities;
    }
    
    public void setQuantities(Map<String, Integer> quantities) {
        this.quantities = quantities;
    }
}

@Test
public void testGenerics() {
    TestPojo.processClass(Order.class)
            .testSettersGetters();  // Automatically generates typed collections
}
```

**Important:** Raw types (collections without generic parameters) will cause a `TestPojoRawUseException`:

```java
// This will fail:
public void setItems(List items) { ... }  // Raw type - NO generics

// This will work:
public void setItems(List<String> items) { ... }  // Properly parameterized
```

## Exception Handling

test-pojo provides detailed exception messages to help diagnose issues:

### TestPojoEqualsException

Thrown when `equals()` method violates contracts:

```
Equals method assertion error:
	Error: Same unchanged object should return true when compared to itself
	Method: public boolean com.example.User.equals(Object)
```

### TestPojoHashCodeException

Thrown when `hashCode()` produces unexpected collisions:

```
HashCode method assertion error:
	Error: Two objects with different attributes should return different hashCode value
	Method: public int com.example.User.hashCode()
```

### TestPojoSetterGetterException

Thrown when setter/getter pair doesn't work correctly:

```
Setter/Getter assertion error:
	Error: Getter return value does not correspond to Setter argument used
	Setter method: public void com.example.User.setName(String)
	Getter method: public String com.example.User.getName()
	Expected result: John Doe
	Actual result: null
```

### TestPojoToStringException

Thrown when `toString()` is not consistent:

```
ToString method assertion error:
	Error: Same unchanged object should return same toString() value every time
	Method: public String com.example.User.toString()
```

### TestPojoConstructorException

Thrown when constructor invocation fails:

```
Constructor assertion error:
	Error: Constructor instantiation exception: NullPointerException
	Constructor: public com.example.User(String,String,int)
```

### TestPojoRawUseException

Thrown when raw types are used instead of parameterized types:

```
Raw use assertion error:
	Error: Raw use of parameterized class: java.util.List
	Method: public void com.example.Order.setItems(List)
```

## How It Works

1. **Reflection-based**: test-pojo uses Java reflection to discover fields, methods, and constructors
2. **Random data generation**: Uses [Instancio](https://www.instancio.org/) to generate random test data
3. **Intelligent matching**: Automatically matches getters with setters based on field names and types
4. **Contract validation**: Verifies standard Java contracts (equals symmetry, hashCode consistency, etc.)

### What Gets Tested?

#### testSettersGetters()

- Finds all fields in the class (including inherited fields)
- Locates corresponding getter and setter methods
- Supports multiple naming conventions (standard, boolean, underscore, direct)
- Sets a random value via setter
- Retrieves via getter and verifies it matches using `equals()`

#### testEqualsAndHashCode()

For `equals()`:

- Null comparison returns false
- Comparison with different type returns false
- Two different random objects are not equal
- Same object compared with itself returns true (reflexivity)

For `hashCode()`:

- Two different random objects have different hash codes

#### testToString()

- Calling toString() multiple times on same unchanged object returns same result
- Verifies deterministic behavior

#### testConstructor()

- All public constructors can be invoked
- Random data can be used for all parameter types
- Supports parameterized types (generics)

#### testRandom()

- Class can be instantiated with random field values
- All public methods can be invoked with random data arguments
- Handles parameterized collections and maps
- Excludes problematic Object methods (wait, notify, etc.)

## Requirements

- **Java**: 11 or higher
- **JUnit**: 4.x or 5.x (for running tests)
- **Instancio**: 5.5.1+ (automatically included)
- **SLF4J**: 2.0.0+ (for logging)

## Limitations and Best Practices

### Limitations

1. **Hash collisions**: The `testEqualsAndHashCode()` method assumes two randomly generated objects will have different
   hash codes. While extremely rare, hash collisions are theoretically possible.
2. **Complex constructors**: Constructors with complex validation logic might fail if random data doesn't meet
   requirements. Use `excludeMethodsContaining()` to skip these.
3. **Lazy initialization**: Fields initialized lazily might not be tested correctly by getters/setters.
4. **Static methods**: Only instance methods are tested.
5. **Raw types**: Collections and Maps without generic type parameters will cause `TestPojoRawUseException`.
6. **Abstract classes**: Abstract classes are automatically skipped during testing.

### Best Practices

1. **Combine with custom tests**: Use test-pojo for boilerplate validation, but write custom tests for business logic
2. **Exclude when needed**: Use exclusion methods for methods with special behavior
3. **Test packages carefully**: When using `processPackage()`, ensure all classes in the package are valid POJOs
4. **Document exclusions**: Comment why certain methods are excluded
5. **Use proper generics**: Always specify generic type parameters for collections and maps
6. **Review test reports**: Use the reporting feature to understand what was tested

```java
@Test
public void testUserPojo() {
    TestPojo.processClass(User.class)
            // Exclude calculated field that has no setter
            .excludeMethodsContaining("getFullName")
            .testSettersGetters()
            .testEqualsAndHashCode();
}
```

## Troubleshooting

### "No such method" errors

If you get errors about missing getters/setters, check:

- Method naming follows JavaBeans conventions
- Field types match exactly (e.g., `boolean` vs `Boolean`)
- Methods are public
- Getter has no parameters and returns the field type
- Setter has one parameter of the field type and returns void (or the object for builder pattern)

### Constructor test failures

If constructor tests fail:

- Ensure constructors don't have validation that rejects random data
- Use `excludeMethodsContaining()` to skip problematic constructors
- Verify all parameter types can be instantiated by Instancio
- Check for null pointer exceptions in constructor logic

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

## Performance Tips

- **Package scanning** can be slow for large packages; consider testing specific classes instead
- **Exclusions** are evaluated using string matching; keep exclusion lists small
- **Multiple test runs** on same classes are independent; no caching between runs
- **Test reports** use static storage; call reset if needed between independent test suites

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
