package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoConstructorException;
import io.github.temesoft.testpojo.exception.TestPojoEqualsException;
import io.github.temesoft.testpojo.exception.TestPojoHashCodeException;
import io.github.temesoft.testpojo.exception.TestPojoSetterGetterException;
import io.github.temesoft.testpojo.exception.TestPojoToStringException;
import org.junit.Test;

import java.util.List;

public class TestPojoTest {

    @Test
    public void testRandomProcessClass_Pojo() {
        TestPojo.processClass(Pojo1.class)
                .testRandom()
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString();
    }

    @Test
    public void testRandomProcessClass_PojoExcludeSingleMethod() {
        TestPojo.processClass(Pojo1.class)
                .excludeMethodContaining("Pojo1.printValue")
                .testRandom()
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString();
    }

    @Test
    public void testRandomProcessClass_TestAll() {
        TestPojo.processClass(Pojo1.class)
                .testAll();
    }

    @Test
    public void testRandomProcessClass_PojoImmutable() {
        TestPojo.processClass(PojoImmutable.class)
                .excludeMethodsContaining("Pojo1.getKey()")
                .testRandom()
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString();
    }

    @Test
    public void testRandomProcessPackage() {
        TestPojo.processPackage("io.github.temesoft.testpojo")
                .excludeMethodsContaining(List.of(
                        "TestPojoTest.",
                        "TestPojoEqualsAndHashCodeTest.",
                        "TestPojoToStringTest.",
                        "Pojo_Bad"
                ))
                .testRandom()
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString();
    }

    @Test
    public void testRandomProcessClass_Exceptions() {
        TestPojo.processClass(
                        TestPojoConstructorException.class,
                        TestPojoEqualsException.class,
                        TestPojoHashCodeException.class,
                        TestPojoSetterGetterException.class,
                        TestPojoToStringException.class
                )
                .testConstructor()
                .testToString()
                .testRandom();
    }

    static class Pojo1 {
        private String key;
        private Double value;
        private boolean ready;

        public String printValue() {
            return key + "=" + value;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(final boolean ready) {
            this.ready = ready;
        }

        public void setValue(final Double value) {
            this.value = value;
        }

        public void setKey(final String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public Double getValue() {
            return value;
        }
    }

    static class PojoImmutable {
        private final String key;
        private final Double value;
        private final boolean ready;

        public PojoImmutable(final String key, final Double value, final boolean ready) {
            this.key = key;
            this.value = value;
            this.ready = ready;
        }

        public String printValue() {
            return key + "=" + value;
        }

        public boolean isReady() {
            return ready;
        }

        public String getKey() {
            return key;
        }

        public Double getValue() {
            return value;
        }
    }
}
