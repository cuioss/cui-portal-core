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

import de.cuioss.portal.core.storage.PortalSessionStorage;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static de.cuioss.test.generator.Generators.letterStrings;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableAutoWeld
@DisplayName("PortalSessionStorageMock Tests")
class PortalSessionStorageMockTest implements ShouldBeNotNull<PortalSessionStorageMock> {

    @Inject
    @PortalSessionStorage
    @Getter
    private PortalSessionStorageMock underTest;

    @BeforeEach
    void setUp() {
        underTest.setThrowIllegalStateOnAccess(false);
        underTest.setThrowIllegalStateOnAccessOnce(false);
    }

    @Nested
    @DisplayName("Basic Storage Operations")
    class BasicStorageOperationsTest {

        @Test
        @DisplayName("Should handle operations on empty storage")
        void shouldHandleBasicOperationsForEmpty() {
            var someKey = letterStrings(2, 8).next();
            var someValue = letterStrings(2, 8).next();

            assertFalse(underTest.containsKey(someKey),
                    "Empty storage should not contain any key");
            assertNull(underTest.get(someKey),
                    "Get on empty storage should return null");
            assertEquals(someValue, underTest.get(someKey, someValue),
                    "Get with default value should return default");
            assertNull(underTest.remove(someKey),
                    "Remove on empty storage should return null");
        }

        @Test
        @DisplayName("Should handle basic put/get/remove operations")
        void shouldHandleBasicOperations() {
            var someKey = letterStrings(2, 8).next();
            var someValue = letterStrings(2, 8).next();
            var defaultValue = letterStrings(2, 8).next();

            underTest.put(someKey, someValue);
            assertTrue(underTest.containsKey(someKey),
                    "Storage should contain inserted key");
            assertEquals(someValue, underTest.get(someKey),
                    "Get should return inserted value");
            assertEquals(someValue, underTest.get(someKey, defaultValue),
                    "Get with default should return inserted value");
            assertEquals(someValue, underTest.remove(someKey),
                    "Remove should return removed value");
            assertFalse(underTest.containsKey(someKey),
                    "Storage should not contain removed key");
        }

        @Test
        @DisplayName("Should handle null values")
        void shouldHandleNullValues() {
            var someKey = letterStrings(2, 8).next();

            // Null values are not stored as per MapStorageImpl implementation
            underTest.put(someKey, null);
            assertFalse(underTest.containsKey(someKey),
                    "Storage should not contain key with null value");
            assertNull(underTest.get(someKey),
                    "Get should return null for non-existent key");

            // Verify that we can store a value after attempting to store null
            var someValue = letterStrings(2, 8).next();
            underTest.put(someKey, someValue);
            assertTrue(underTest.containsKey(someKey),
                    "Storage should contain key after storing non-null value");
            assertEquals(someValue, underTest.get(someKey),
                    "Get should return stored value");
        }

        @Test
        @DisplayName("Should handle null keys")
        void shouldHandleNullKeys() {
            var someValue = letterStrings(2, 8).next();

            // Null keys are not allowed as per MapStorageImpl implementation
            underTest.put(null, someValue);
            assertFalse(underTest.containsKey(null),
                    "Storage should not contain null key");
            assertNull(underTest.get(null),
                    "Get should return null for null key");
            assertNull(underTest.remove(null),
                    "Remove should return null for null key");
        }

        @Test
        @DisplayName("Should handle multiple operations")
        void shouldHandleMultipleOperations() {
            Map<String, Serializable> testData = new HashMap<>();
            testData.put(letterStrings(2, 8).next(), letterStrings(2, 8).next());
            testData.put(letterStrings(2, 8).next(), letterStrings(2, 8).next());

            testData.forEach((key, value) -> underTest.put(key, value));

            testData.forEach((key, value) -> {
                assertTrue(underTest.containsKey(key),
                        "Storage should contain inserted key");
                assertEquals(value, underTest.get(key),
                        "Get should return inserted value");
            });

            testData.forEach((key, value) -> {
                assertEquals(value, underTest.remove(key),
                        "Remove should return correct value");
                assertFalse(underTest.containsKey(key),
                        "Storage should not contain removed key");
            });
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTest {

        @Test
        @DisplayName("Should handle throw once behavior")
        void shouldHandleThrowOnce() {
            var someKey = letterStrings(2, 8).next();
            var someValue = letterStrings(2, 8).next();

            underTest.setThrowIllegalStateOnAccessOnce(true);

            assertThrows(IllegalStateException.class,
                    () -> underTest.put(someKey, someValue),
                    "First access should throw exception");

            assertDoesNotThrow(() -> underTest.put(someKey, someValue),
                    "Subsequent access should not throw");

            assertTrue(underTest.containsKey(someKey),
                    "Operation should succeed after exception");
        }

        @Test
        @DisplayName("Should handle throw always behavior")
        void shouldHandleThrowAlways() {
            var someKey = letterStrings(2, 8).next();
            var someValue = letterStrings(2, 8).next();

            underTest.setThrowIllegalStateOnAccess(true);

            assertThrows(IllegalStateException.class,
                    () -> underTest.put(someKey, someValue),
                    "First access should throw exception");

            assertThrows(IllegalStateException.class,
                    () -> underTest.put(someKey, someValue),
                    "Subsequent access should also throw");

            assertThrows(IllegalStateException.class,
                    () -> underTest.get(someKey),
                    "Get operation should throw");

            assertThrows(IllegalStateException.class,
                    () -> underTest.remove(someKey),
                    "Remove operation should throw");
        }

        @Test
        @DisplayName("Should handle error state reset")
        void shouldHandleErrorStateReset() {
            var someKey = letterStrings(2, 8).next();
            var someValue = letterStrings(2, 8).next();

            underTest.setThrowIllegalStateOnAccess(true);
            assertThrows(IllegalStateException.class,
                    () -> underTest.put(someKey, someValue));

            underTest.setThrowIllegalStateOnAccess(false);
            assertDoesNotThrow(() -> underTest.put(someKey, someValue),
                    "Operation should succeed after error state reset");
        }
    }
}
