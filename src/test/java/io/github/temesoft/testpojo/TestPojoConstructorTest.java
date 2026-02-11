package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.model.Pojo1;
import io.github.temesoft.testpojo.model.PojoComplex1;
import io.github.temesoft.testpojo.model.PojoImmutable;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPojoConstructorTest {

    @Test
    public void testClassPredicate() {
        final String report = TestPojo.processClass(Pojo1.class, PojoComplex1.class)
                .filterClasses(aClass -> !aClass.equals(Pojo1.class))
                .testConstructor()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Class: " + PojoComplex1.class.getName()));
    }

    @Test
    public void testConstructorPredicate() {
        final String report = TestPojo.processClass(Pojo1.class, PojoImmutable.class)
                .filterConstructors(constructor -> constructor.getParameters().length > 0)
                .testConstructor()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Class: " + PojoImmutable.class.getName()));
    }
}