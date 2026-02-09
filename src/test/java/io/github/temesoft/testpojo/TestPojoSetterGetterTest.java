package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoRawUseException;
import io.github.temesoft.testpojo.exception.TestPojoSetterGetterException;
import io.github.temesoft.testpojo.model.Pojo_BadGetter_1;
import io.github.temesoft.testpojo.model.Pojo_BadGetter_2;
import io.github.temesoft.testpojo.model.Pojo_BadRawUsageInSetter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TestPojoSetterGetterTest {

    @Test
    public void testRawUseException() {
        TestPojoSetterGetter testPojo = new TestPojoSetterGetter(Pojo_BadRawUsageInSetter.class, null);
        final TestPojoRawUseException thrown = assertThrows(
                TestPojoRawUseException.class,
                testPojo::testClass
        );
        assertEquals("Raw use assertion error:\n" +
                        "\tError: Raw use of parameterized class: java.util.Map\n" +
                        "\tMethod: public void io.github.temesoft.testpojo.model.Pojo_BadRawUsageInSetter.setHeaders(java.util.Map)",
                thrown.getMessage());
    }

    @Test
    public void testBadGetterMethod_FixedReturn() {
        TestPojoSetterGetter testPojo = new TestPojoSetterGetter(Pojo_BadGetter_1.class, null);
        final TestPojoSetterGetterException thrown = assertThrows(
                TestPojoSetterGetterException.class,
                testPojo::testClass
        );
        assertTrue(thrown.getMessage()
                .contains("Setter/Getter assertion error:\n" +
                        "\tError: Getter return value does not correspond to Setter argument used\n" +
                        "\tSetter method: public void io.github.temesoft.testpojo.model.Pojo_BadGetter_1.setKey(java.lang.String)\n" +
                        "\tGetter method: public java.lang.String io.github.temesoft.testpojo.model.Pojo_BadGetter_1.getKey()\n")
        );
        assertTrue(thrown.getMessage().contains("Actual result: key"));
    }

    @Test
    public void testBadGetterMethod_NullReturn() {
        TestPojoSetterGetter testPojo = new TestPojoSetterGetter(Pojo_BadGetter_2.class, null);
        final TestPojoSetterGetterException thrown = assertThrows(
                TestPojoSetterGetterException.class,
                testPojo::testClass
        );
        assertTrue(thrown.getMessage()
                .contains("Setter/Getter assertion error:\n" +
                        "\tError: Getter return value does not correspond to Setter argument used\n" +
                        "\tSetter method: public void io.github.temesoft.testpojo.model.Pojo_BadGetter_2.setKey(java.lang.String)\n" +
                        "\tGetter method: public java.lang.String io.github.temesoft.testpojo.model.Pojo_BadGetter_2.getKey()\n")
        );
        assertTrue(thrown.getMessage().contains("Actual result: null"));
    }

}