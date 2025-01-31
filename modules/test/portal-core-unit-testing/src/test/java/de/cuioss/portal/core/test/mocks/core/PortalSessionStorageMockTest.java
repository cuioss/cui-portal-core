/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.test.mocks.core;

import static de.cuioss.test.generator.Generators.letterStrings;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.core.storage.PortalSessionStorage;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

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
