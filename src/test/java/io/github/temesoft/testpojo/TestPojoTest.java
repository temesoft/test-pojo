package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoConstructorException;
import io.github.temesoft.testpojo.exception.TestPojoEqualsException;
import io.github.temesoft.testpojo.exception.TestPojoHashCodeException;
import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import io.github.temesoft.testpojo.exception.TestPojoSetterGetterException;
import io.github.temesoft.testpojo.exception.TestPojoToStringException;
import io.github.temesoft.testpojo.model.Pojo1;
import io.github.temesoft.testpojo.model.PojoComplex1;
import io.github.temesoft.testpojo.model.PojoExtendingAbstractBase;
import io.github.temesoft.testpojo.model.PojoImmutable;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_1;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_2;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_3;
import io.github.temesoft.testpojo.model.Pojo_BadEquals_4;
import io.github.temesoft.testpojo.model.Pojo_BadGetter_1;
import io.github.temesoft.testpojo.model.Pojo_BadGetter_2;
import io.github.temesoft.testpojo.model.Pojo_BadHashCode;
import io.github.temesoft.testpojo.model.Pojo_BadRawUsageInSetter;
import io.github.temesoft.testpojo.model.Pojo_BadToString;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestPojoTest {

    private static final List<Class<?>> CLASSES_TO_EXCLUDE = List.of(
            Pojo_BadEquals_1.class,
            Pojo_BadEquals_2.class,
            Pojo_BadEquals_3.class,
            Pojo_BadEquals_4.class,
            Pojo_BadRawUsageInSetter.class,
            Pojo_BadGetter_1.class,
            Pojo_BadGetter_2.class,
            Pojo_BadHashCode.class,
            Pojo_BadToString.class
    );

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
                .excludeMethodsContaining("Pojo1.printValue")
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
        TestPojo.processPackage(Pojo1.class.getPackageName())
                .excludeMethodsContaining(List.of("Pojo_Bad"))
                .testRandom()
                .testSettersGetters()
                .testEqualsAndHashCode()
                .testToString();
    }

    @Test
    public void testRandomProcessPackage_WithExcludeClasses_1() {
        TestPojo.processPackage(Pojo1.class.getPackageName(), CLASSES_TO_EXCLUDE.toArray(new Class<?>[0]))
                .testAll();
    }

    @Test
    public void testRandomProcessPackage_WithExcludeClasses_2() {
        TestPojo.processPackage(Pojo1.class.getPackageName())
                .excludeClasses(CLASSES_TO_EXCLUDE.toArray(new Class<?>[0]))
                .testAll();
    }

    @Test
    public void testRandomProcessPackage_WithExcludeClasses_3() {
        TestPojo.processPackage(Pojo1.class.getPackageName())
                .excludeClasses(CLASSES_TO_EXCLUDE)
                .testAll();
    }

    @Test
    public void testRandomProcessPackage_PrintReport() {
        TestPojo.processClass(Pojo1.class)
                .testAll()
                .printReport();
    }

    @Test
    public void testRandomProcessPackage_GetReport() {
        final String report = TestPojo.processClass(Pojo1.class)
                .testAll()
                .getReport();
        assertTrue(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Test type: SetterGetter"));
        assertTrue(report.contains("Test type: EqualsAndHashCode"));
        assertTrue(report.contains("Test type: Constructor"));
        assertTrue(report.contains("Test type: Random"));
    }

    @Test
    public void testRandomProcessPackage_SaveReport() throws IOException {
        final Path path = Files.createTempFile("test-pojo", "txt");
        TestPojo.processClass(Pojo1.class)
                .testAll()
                .saveReport(path);
        final String report = Files.readString(path);
        assertTrue(report.contains("Class: " + Pojo1.class.getName()));
        assertTrue(report.contains("Test type: SetterGetter"));
        assertTrue(report.contains("Test type: EqualsAndHashCode"));
        assertTrue(report.contains("Test type: Constructor"));
        assertTrue(report.contains("Test type: Random"));
    }

    @Test
    public void testRandomProcessClass_Exceptions() {
        TestPojo.processClass(
                        TestPojoConstructorException.class,
                        TestPojoEqualsException.class,
                        TestPojoHashCodeException.class,
                        TestPojoRawUseException.class,
                        TestPojoSetterGetterException.class,
                        TestPojoToStringException.class
                )
                .testConstructor()
                .testToString()
                .testRandom();
    }

    @Test
    public void testRandomProcessClass_ComplexClassWithLombok() {
        TestPojo.processClass(PojoComplex1.class).testAll();
    }

    @Test
    public void testRandomProcessClass_PojoExtendingAbstractBase() {
        TestPojo.processClass(PojoExtendingAbstractBase.class).testAll();
    }

    @Test
    public void testRandomProcessClass_RawUsageInSetter() {
        final TestPojoRawUseException thrown = assertThrows(
                TestPojoRawUseException.class,
                () -> TestPojo.processClass(Pojo_BadRawUsageInSetter.class).testRandom()
        );
        assertEquals("Raw use assertion error:\n" +
                        "\tError: Raw use of parameterized class: java.util.Map\n" +
                        "\tMethod: public void io.github.temesoft.testpojo.model.Pojo_BadRawUsageInSetter.setHeaders(java.util.Map)",
                thrown.getMessage());
    }
}
