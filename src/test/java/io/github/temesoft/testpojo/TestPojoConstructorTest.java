package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import io.github.temesoft.testpojo.model.Pojo1;
import io.github.temesoft.testpojo.model.PojoComplex;
import io.github.temesoft.testpojo.model.PojoImmutable;
import io.github.temesoft.testpojo.model.Pojo_BadRawUsage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestPojoConstructorTest {

    @Test
    public void testClassPredicate() {
        final String report = TestPojo.processClass(Pojo1.class, PojoComplex.class)
                .filterClasses(aClass -> !aClass.equals(Pojo1.class))
                .testConstructor()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Class: " + PojoComplex.class.getName()));
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

    @Test
    public void testConstructorRawUsage() {
        final String report = TestPojo.processClass(Pojo1.class, PojoImmutable.class)
                .filterConstructors(constructor -> constructor.getParameters().length > 0)
                .testConstructor()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Class: " + PojoImmutable.class.getName()));
    }

    @Test
    public void testRawUseException() {
        TestPojoConstructor testPojo = new TestPojoConstructor(Pojo_BadRawUsage.class, null, null);
        final TestPojoRawUseException thrown = assertThrows(
                TestPojoRawUseException.class,
                testPojo::testClass
        );
        assertEquals("Raw use assertion error:\n" +
                        "\tError: Raw use of parameterized class: java.util.Map\n" +
                        "\tConstructor: public io.github.temesoft.testpojo.model.Pojo_BadRawUsage(java.util.Map)",
                thrown.getMessage());
    }
}