package io.github.temesoft.testpojo;

import io.github.temesoft.testpojo.model.Pojo1;
import io.github.temesoft.testpojo.model.SomeInterface;
import org.instancio.TypeToken;
import org.junit.Test;

import java.util.List;

import static io.github.temesoft.testpojo.TestPojoUtils.createInterface;
import static io.github.temesoft.testpojo.TestPojoUtils.getGenericTypeToken;
import static io.github.temesoft.testpojo.TestPojoUtils.isMethodExcluded;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestPojoUtilsTest {

    @Test
    public void testIsMethodExcluded() throws NoSuchMethodException {
        assertFalse(isMethodExcluded(null, null));
        assertFalse(isMethodExcluded(Pojo1.class.getMethod("toString"), null));
        assertFalse(isMethodExcluded(Pojo1.class.getMethod("toString"), List.of()));
        assertTrue(isMethodExcluded(Pojo1.class.getMethod("toString"), List.of("toString")));
    }

    @Test
    public void testCreateInterface() {
        final Object someInterface = createInterface(SomeInterface.class);
        assertTrue(someInterface instanceof SomeInterface);
        assertEquals("Proxy<SomeInterface>", someInterface.toString());
        final Object someInterface2 = createInterface(SomeInterface.class);
        assertTrue(someInterface.hashCode() != someInterface2.hashCode());
        assertNotEquals(someInterface, someInterface2);
    }

    @Test
    public void testGetGenericTypeToken() {
        final TypeToken<?> genericTypeToken = getGenericTypeToken();
        assertEquals(Object.class.getName(), genericTypeToken.get().getTypeName());
    }
}