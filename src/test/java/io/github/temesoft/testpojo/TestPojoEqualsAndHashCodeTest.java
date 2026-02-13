package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoEqualsException;
import io.github.temesoft.testpojo.exception.TestPojoHashCodeException;
import io.github.temesoft.testpojo.model.Pojo1;
import io.github.temesoft.testpojo.model.PojoComplex;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_1;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_2;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_3;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_4;
import io.github.temesoft.testpojo.model.Pojo_BadHashCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestPojoEqualsAndHashCodeTest {

    @Test
    public void testShouldNotReturnTrueWhenNullPassed() {
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_1.class, null, null, null);
        final TestPojoEqualsException thrown = assertThrows(
                TestPojoEqualsException.class,
                testPojo::testClass
        );
        assertEquals("Equals method assertion error:\n" +
                        "\tError: Equals should not return true when null is passed as argument\n" +
                        "\tMethod: public boolean io.github.temesoft.testpojo.model.Pojo_BadEquals_1.equals(java.lang.Object)",
                thrown.getMessage());
    }

    @Test
    public void testShouldNotReturnTrueWhenObjectOfDifferentTypePassed() {
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_2.class, null, null, null);
        final TestPojoEqualsException thrown = assertThrows(
                TestPojoEqualsException.class,
                testPojo::testClass
        );
        assertEquals("Equals method assertion error:\n" +
                        "\tError: Equals should not return true when object of different type is passed as argument\n" +
                        "\tMethod: public boolean io.github.temesoft.testpojo.model.Pojo_BadEquals_2.equals(java.lang.Object)",
                thrown.getMessage());
    }

    @Test
    public void testObjectsWithRandomAttributesShouldNotEqual() {
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_3.class, null, null, null);
        final TestPojoEqualsException thrown = assertThrows(
                TestPojoEqualsException.class,
                testPojo::testClass
        );
        assertEquals("Equals method assertion error:\n" +
                        "\tError: Two objects with random attributes should not equal\n" +
                        "\tMethod: public boolean io.github.temesoft.testpojo.model.Pojo_BadEquals_3.equals(java.lang.Object)",
                thrown.getMessage());
    }

    @Test
    public void testSameObjectShouldBeEqual() {
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_4.class, null, null, null);
        final TestPojoEqualsException thrown = assertThrows(
                TestPojoEqualsException.class,
                testPojo::testClass
        );
        assertEquals("Equals method assertion error:\n" +
                        "\tError: Same object should be equal\n" +
                        "\tMethod: public boolean io.github.temesoft.testpojo.model.Pojo_BadEquals_4.equals(java.lang.Object)",
                thrown.getMessage());
    }

    @Test
    public void testObjectsWithDifferentAttributesShouldReturnDifferentHashCode() {
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadHashCode.class, null, null, null);
        final TestPojoHashCodeException thrown = assertThrows(
                TestPojoHashCodeException.class,
                testPojo::testClass
        );
        assertEquals("HashCode method assertion error:\n" +
                        "\tError: Two objects with different attributes should return different hashCode value\n" +
                        "\tMethod: public int io.github.temesoft.testpojo.model.Pojo_BadHashCode.hashCode()",
                thrown.getMessage());
    }

    @Test
    public void testClassPredicate() {
        final String report = TestPojo.processClass(Pojo1.class, PojoComplex.class)
                .filterClasses(aClass -> !aClass.equals(Pojo1.class))
                .testEqualsAndHashCode()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Class: " + PojoComplex.class.getName()));
    }

    @Test
    public void testMethodPredicate() {
        final String report = TestPojo.processClass(Pojo1.class)
                .filterMethods(method -> !method.toString().contains("java.lang.Object"))
                .testEqualsAndHashCode()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
    }
}