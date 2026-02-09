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
import io.github.temesoft.testpojo.model.Pojo_BadRawUsageInSetter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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
        TestPojo.processPackage("io.github.temesoft.testpojo.model")
                .excludeMethodsContaining(List.of("Pojo_Bad"))
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
