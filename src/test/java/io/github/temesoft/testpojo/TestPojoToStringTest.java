package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoToStringException;
import io.github.temesoft.testpojo.model.Pojo_BadToString;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class TestPojoToStringTest {

    @Test
    public void testSameObjectShouldBeEqual() {
        final TestPojoToString testPojo = new TestPojoToString(Pojo_BadToString.class, null);
        final TestPojoToStringException thrown = assertThrows(
                TestPojoToStringException.class,
                testPojo::testClass
        );
        assertEquals("ToString method assertion error:\n" +
                        "\tError: Same unchanged object should return same toString() value every time\n" +
                        "\tMethod: public java.lang.String io.github.temesoft.testpojo.model.Pojo_BadToString.toString()",
                thrown.getMessage());
    }
}