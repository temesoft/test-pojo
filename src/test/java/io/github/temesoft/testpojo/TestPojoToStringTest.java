package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.exception.TestPojoToStringException;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

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
                "\tMethod: public java.lang.String io.github.temesoft.testpojo.TestPojoToStringTest$Pojo_BadToString.toString()", thrown.getMessage());
    }

    static class Pojo_BadToString {
        @Override
        public String toString() {
            final byte[] buff = new byte[128];
            new SecureRandom().nextBytes(buff);
            return new String(buff, StandardCharsets.UTF_8);
        }
    }
}