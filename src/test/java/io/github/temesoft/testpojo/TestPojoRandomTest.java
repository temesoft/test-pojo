package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.model.Pojo1;
import io.github.temesoft.testpojo.model.PojoComplex;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestPojoRandomTest {

    @Test
    public void testClassPredicate() {
        final String report = TestPojo.processClass(Pojo1.class, PojoComplex.class)
                .filterClasses(aClass -> !aClass.equals(Pojo1.class))
                .testRandom()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Class: " + PojoComplex.class.getName()));
    }

    @Test
    public void testMethodPredicate() {
        final String report = TestPojo.processClass(Pojo1.class)
                .filterMethods(method -> !method.getDeclaringClass().equals(Pojo1.class))
                .testRandom()
                .getReport();
        assertTrue(report.contains("Methods tested: 0"));
        assertTrue(report.contains("Class: " + Pojo1.class.getName()));
    }
}