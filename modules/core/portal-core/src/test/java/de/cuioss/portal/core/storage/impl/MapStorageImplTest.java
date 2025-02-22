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
package de.cuioss.portal.core.storage.impl;

import de.cuioss.portal.core.storage.MapStorage;
import de.cuioss.test.generator.TypedGenerator;
import de.cuioss.test.valueobjects.ValueObjectTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static de.cuioss.test.generator.Generators.integers;
import static de.cuioss.test.generator.Generators.nonEmptyStrings;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link MapStorageImpl} which provides a basic key-value storage implementation.
 * Includes edge cases and boundary testing using parameterized tests.
 * 
 * Implementation note: The MapStorageImpl does not store null keys or null values.
 */
@DisplayName("MapStorageImpl Tests")
class MapStorageImplTest extends ValueObjectTest<MapStorageImpl<String, String>> {

    private final TypedGenerator<Integer> integerGenerator = integers(0, 10);
    private final TypedGenerator<String> strings = nonEmptyStrings();

    @Override
    protected MapStorageImpl<String, String> anyValueObject() {
        final var mapStorageImpl = new MapStorageImpl<String, String>();
        for (var i = 0; i < integerGenerator.next(); i++) {
            mapStorageImpl.put(strings.next(), strings.next());
        }
        return mapStorageImpl;
    }

    @Nested
    @DisplayName("Empty Storage Tests")
    class EmptyStorageTests {

        @ParameterizedTest(name = "Should handle key={0} on empty storage")
        @NullSource
        @ValueSource(strings = {"", " ", "key", "very.long.key.path"})
        void shouldHandleEmptyStorageWithDifferentKeys(String key) {
            final MapStorage<String, String> storage = new MapStorageImpl<>();
            final var value = strings.next();

            assertFalse(storage.containsKey(key),
                    "Empty storage should not contain key: " + key);
            assertNull(storage.get(key),
                    "Get on empty storage should return null for key: " + key);
            assertEquals(value, storage.get(key, value),
                    "Get with default value should return default for key: " + key);
            assertNull(storage.remove(key),
                    "Remove on empty storage should return null for key: " + key);
        }
    }

    @Nested
    @DisplayName("Null Handling Tests")
    class NullHandlingTests {

        @ParameterizedTest(name = "Should handle null value with key={0}")
        @ValueSource(strings = {"", " ", "key"})
        void shouldHandleNullValues(String key) {
            final MapStorage<String, String> storage = new MapStorageImpl<>();
            final var defaultValue = strings.next();
            final var initialValue = strings.next();

            // First store a non-null value
            storage.put(key, initialValue);
            assertTrue(storage.containsKey(key),
                    "Storage should contain key with initial value");
            assertEquals(initialValue, storage.get(key),
                    "Get should return initial value");

            // Try to store null value - should be ignored per implementation
            storage.put(key, null);
            assertTrue(storage.containsKey(key),
                    "Storage should still contain key after null value put");
            assertEquals(initialValue, storage.get(key),
                    "Get should still return initial value after null put");
            assertEquals(initialValue, storage.get(key, defaultValue),
                    "Get with default should return initial value");
        }

        @Test
        @DisplayName("Should handle null key operations")
        void shouldHandleNullKeyOperations() {
            final MapStorage<String, String> storage = new MapStorageImpl<>();
            final var value = strings.next();

            assertFalse(storage.containsKey(null),
                    "Storage should not contain null key initially");
            assertNull(storage.get(null),
                    "Get with null key should return null initially");

            // Try to store with null key - should be ignored per implementation
            storage.put(null, value);
            assertFalse(storage.containsKey(null),
                    "Storage should not contain null key after put attempt");
            assertNull(storage.get(null),
                    "Get with null key should return null after put attempt");

            assertNull(storage.remove(null),
                    "Remove with null key should return null");
            assertFalse(storage.containsKey(null),
                    "Storage should not contain null key after remove");
        }
    }

    @Nested
    @DisplayName("Basic Operations Tests")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should handle basic storage operations correctly")
        void shouldHandleBasicOperations() {
            final MapStorage<String, String> storage = new MapStorageImpl<>();
            final var key = strings.next();
            final var value = strings.next();

            assertFalse(storage.containsKey(key), "Fresh storage should not contain key");
            assertNull(storage.get(key), "Get on fresh storage should return null");
            assertNull(storage.remove(key), "Remove on fresh storage should return null");

            storage.put(key, value);
            assertTrue(storage.containsKey(key), "Storage should contain key after put");
            assertEquals(value, storage.get(key), "Get should return stored value");
            assertEquals(value, storage.get(key, ""),
                    "Get with default should return stored value");

            assertEquals(value, storage.remove(key), "Remove should return stored value");
            assertFalse(storage.containsKey(key), "Storage should not contain removed key");
            assertNull(storage.get(key), "Get after remove should return null");
            assertNull(storage.remove(key), "Remove after remove should return null");
        }

        @ParameterizedTest(name = "Should handle value update from {0} to {1}")
        @MethodSource("valueUpdateScenarios")
        void shouldHandleValueUpdates(String initialValue, String updatedValue) {
            final MapStorage<String, String> storage = new MapStorageImpl<>();
            final var key = strings.next();

            // Initial put - only store if value is not null
            storage.put(key, initialValue);
            if (initialValue != null) {
                assertTrue(storage.containsKey(key),
                        "Storage should contain key with non-null initial value");
                assertEquals(initialValue, storage.get(key),
                        "Get should return initial value");
            } else {
                assertFalse(storage.containsKey(key),
                        "Storage should not contain key with null initial value");
                assertNull(storage.get(key),
                        "Get should return null for null initial value");
            }

            // Update - only store if new value is not null
            storage.put(key, updatedValue);
            if (updatedValue != null) {
                assertTrue(storage.containsKey(key),
                        "Storage should contain key with non-null updated value");
                assertEquals(updatedValue, storage.get(key),
                        "Get should return updated value");
            } else {
                // If initial value was non-null, it should be preserved
                if (initialValue != null) {
                    assertTrue(storage.containsKey(key),
                            "Storage should preserve initial value after null update");
                    assertEquals(initialValue, storage.get(key),
                            "Get should return initial value after null update");
                } else {
                    assertFalse(storage.containsKey(key),
                            "Storage should not contain key after null update");
                    assertNull(storage.get(key),
                            "Get should return null after null update");
                }
            }
        }

        static Stream<Arguments> valueUpdateScenarios() {
            return Stream.of(
                    Arguments.of(null, "value"),
                    Arguments.of("value", null),
                    Arguments.of("value1", "value2"),
                    Arguments.of("", " "),
                    Arguments.of(" ", ""),
                    Arguments.of("short", "very long value with spaces")
            );
        }
    }

    @Nested
    @DisplayName("Performance Edge Cases")
    class PerformanceEdgeCases {

        @ParameterizedTest(name = "Should handle {0} sequential operations")
        @ValueSource(ints = {100, 1000, 10000})
        void shouldHandleMultipleOperations(int operationCount) {
            final MapStorage<String, String> storage = new MapStorageImpl<>();
            final var key = strings.next();
            final var value = strings.next();

            // Sequential puts
            for (int i = 0; i < operationCount; i++) {
                storage.put(key + i, value + i);
                assertTrue(storage.containsKey(key + i),
                        "Storage should contain key after put: " + i);
            }

            // Sequential gets
            for (int i = 0; i < operationCount; i++) {
                assertEquals(value + i, storage.get(key + i),
                        "Storage should return correct value: " + i);
            }

            // Sequential removes
            for (int i = 0; i < operationCount; i++) {
                assertEquals(value + i, storage.remove(key + i),
                        "Remove should return correct value: " + i);
                assertFalse(storage.containsKey(key + i),
                        "Storage should not contain key after remove: " + i);
            }
        }
    }
}
