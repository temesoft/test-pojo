package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoEqualsException;
import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SuppressWarnings("EqualsGetClass")
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
                        "\tMethod: public boolean io.github.temesoft.testpojo.TestPojoEqualsAndHashCodeTest$Pojo_BadEquals_1.equals(java.lang.Object)",
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
                        "\tMethod: public boolean io.github.temesoft.testpojo.TestPojoEqualsAndHashCodeTest$Pojo_BadEquals_2.equals(java.lang.Object)",
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
                        "\tMethod: public boolean io.github.temesoft.testpojo.TestPojoEqualsAndHashCodeTest$Pojo_BadEquals_3.equals(java.lang.Object)",
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
                        "\tMethod: public boolean io.github.temesoft.testpojo.TestPojoEqualsAndHashCodeTest$Pojo_BadEquals_4.equals(java.lang.Object)",
                thrown.getMessage());
    }

    static class Pojo_BadEquals_1 {
        private String value;

        @Override
        public boolean equals(final Object o) {
            return o == null;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    static class Pojo_BadEquals_2 {
        private String value;

        @Override
        public boolean equals(final Object o) {
            if (o == null) return false;
            if (getClass() != o.getClass()) return true;
            final Pojo_BadEquals_2 that = (Pojo_BadEquals_2) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    static class Pojo_BadEquals_3 {
        private String value;

        @Override
        public boolean equals(final Object o) {
            return o != null && getClass() == o.getClass();
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

    static class Pojo_BadEquals_4 {
        private String value;

        @Override
        public boolean equals(final Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            final Pojo_BadEquals_4 that = (Pojo_BadEquals_4) o;
            if (!value.equals(that.value)) {
                return false;
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }
    }

}