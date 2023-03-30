package de.cuioss.portal.core.test.mocks.core;

import static de.cuioss.test.generator.Generators.letterStrings;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.storage.PortalSessionStorage;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import lombok.Getter;

@EnableAutoWeld
class PortalSessionStorageMockTest implements ShouldBeNotNull<PortalSessionStorageMock> {

    @Inject
    @PortalSessionStorage
    @Getter
    private PortalSessionStorageMock underTest;

    @Test
    void shouldHandleBasicOperationsForEmpty() {
        var someKey = letterStrings(2, 8).next();
        var someValue = letterStrings(2, 8).next();
        assertFalse(underTest.containsKey(someKey));
        assertNull(underTest.get(someKey));
        assertEquals(someValue, underTest.get(someKey, someValue));
        assertNull(underTest.remove(someKey));
    }

    @Test
    void shouldHandleBasicOperations() {
        var someKey = letterStrings(2, 8).next();
        var someValue = letterStrings(2, 8).next();
        var defaultValue = letterStrings(2, 8).next();
        underTest.put(someKey, someValue);
        assertTrue(underTest.containsKey(someKey));
        assertEquals(someValue, underTest.get(someKey));
        assertEquals(someValue, underTest.get(someKey, defaultValue));
        assertEquals(someValue, underTest.remove(someKey));
    }

    @Test
    void shouldHandleThrowOnce() {
        var someKey = letterStrings(2, 8).next();
        var someValue = letterStrings(2, 8).next();
        underTest.setThrowIllegalStateOnAccessOnce(true);
        assertThrows(IllegalStateException.class, () -> underTest.put(someKey, someValue));
        assertDoesNotThrow(() -> underTest.put(someKey, someValue));
    }

    @Test
    void shouldHandleThrowAlways() {
        var someKey = letterStrings(2, 8).next();
        var someValue = letterStrings(2, 8).next();
        underTest.setThrowIllegalStateOnAccess(true);
        assertThrows(IllegalStateException.class, () -> underTest.put(someKey, someValue));
        assertThrows(IllegalStateException.class, () -> underTest.put(someKey, someValue));
    }

}
