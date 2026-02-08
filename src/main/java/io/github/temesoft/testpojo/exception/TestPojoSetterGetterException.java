package io.github.temesoft.testpojo.exception;

import java.lang.reflect.Method;

public class TestPojoSetterGetterException extends RuntimeException {
    public TestPojoSetterGetterException(final Method setterMethod,
                                         final Method getterMethod,
                                         final Object expectedResult,
                                         final Object actualResult) {
        super("Setter/Getter assertion error:"
                + "\n\tError: Getter return value does not correspond to Setter argument used"
                + "\n\tSetter method: " + setterMethod
                + "\n\tGetter method: " + getterMethod
                + "\n\tExpected result: " + expectedResult
                + "\n\tActual result: " + actualResult);
    }
}
