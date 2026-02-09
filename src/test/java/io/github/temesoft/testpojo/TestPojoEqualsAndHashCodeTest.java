package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoEqualsException;
import io.github.temesoft.testpojo.exception.TestPojoHashCodeException;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_1;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_2;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_3;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_4;
import io.github.temesoft.testpojo.model.Pojo_BadHashCode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestPojoEqualsAndHashCodeTest {

    @Test
    public void testShouldNotReturnTrueWhenNullPassed() {
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_1.class, null);
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
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_2.class, null);
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
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_3.class, null);
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
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadEquals_4.class, null);
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
        final TestPojoEqualsAndHashCode testPojo = new TestPojoEqualsAndHashCode(Pojo_BadHashCode.class, null);
        final TestPojoHashCodeException thrown = assertThrows(
                TestPojoHashCodeException.class,
                testPojo::testClass
        );
        assertEquals("HashCode method assertion error:\n" +
                        "\tError: Two objects with different attributes should return different hashCode value\n" +
                        "\tMethod: public int io.github.temesoft.testpojo.model.Pojo_BadHashCode.hashCode()",
                thrown.getMessage());
    }
}