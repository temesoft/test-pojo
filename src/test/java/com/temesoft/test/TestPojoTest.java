package com.temesoft.test;

import com.temesoft.test.exception.TestPojoConstructorException;
import com.temesoft.test.exception.TestPojoEqualsException;
import com.temesoft.test.exception.TestPojoHashCodeException;
import com.temesoft.test.exception.TestPojoSetterGetterException;
import com.temesoft.test.exception.TestPojoToStringException;
import org.junit.Test;

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
        TestPojo.processPackage("com.temesoft.test")
                .excludeMethodsContaining("TestPojoTest.")
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
