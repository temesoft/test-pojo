package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoToStringException;
import io.github.temesoft.testpojo.model.Pojo1;
import io.github.temesoft.testpojo.model.PojoComplex;
import io.github.temesoft.testpojo.model.Pojo_BadToString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestPojoToStringTest {

    @Test
    public void testSameObjectShouldBeEqual() {
        final TestPojoToString testPojo = new TestPojoToString(Pojo_BadToString.class, null, null, null);
        final TestPojoToStringException thrown = assertThrows(
                TestPojoToStringException.class,
                testPojo::testClass
        );
        assertEquals("ToString method assertion error:\n" +
                        "\tError: Same unchanged object should return same toString() value every time\n" +
                        "\tMethod: public java.lang.String io.github.temesoft.testpojo.model.Pojo_BadToString.toString()",
                thrown.getMessage());
    }

    @Test
    public void testClassPredicate() {
        final String report = TestPojo.processClass(Pojo1.class, PojoComplex.class)
                .filterClasses(aClass -> !aClass.equals(Pojo1.class))
                .testToString()
                .getReport();
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Class: " + PojoComplex.class.getName()));
    }

    @Test
    public void testMethodPredicate() {
        final String report = TestPojo.processClass(Pojo1.class, PojoComplex.class)
                .filterMethods(method -> !method.getName().contains("toString"))
                .testToString()
                .getReport();
        System.err.println(report);
        assertFalse(report.contains("Class: " + Pojo1.class.getName()));
        assertFalse(report.contains("Class: " + PojoComplex.class.getName()));
    }

}