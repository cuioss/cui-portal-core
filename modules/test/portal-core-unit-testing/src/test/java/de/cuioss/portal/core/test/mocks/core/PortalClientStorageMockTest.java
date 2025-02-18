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

import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.core.storage.PortalClientStorage;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldBeNotNull;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@EnableAutoWeld
@DisplayName("PortalClientStorageMock Tests")
class PortalClientStorageMockTest implements ShouldBeNotNull<PortalClientStorageMock> {

    private static final String KEY = "testKey";
    private static final String VALUE = "testValue";

    @Getter
    @Inject
    @PortalClientStorage
    private PortalClientStorageMock underTest;

    @Nested
    @DisplayName("Storage Operations")
    class StorageOperationsTest {

        @Test
        @DisplayName("Should store and retrieve values")
        void shouldStoreAndRetrieveValues() {
            underTest.put(KEY, VALUE);
            assertEquals(VALUE, underTest.get(KEY),
                    "Should retrieve stored value");
            assertEquals(VALUE, underTest.get(KEY, "default"),
                    "Should retrieve stored value with default");
            assertTrue(underTest.containsKey(KEY),
                    "Should contain stored key");
        }

        @Test
        @DisplayName("Should handle value removal")
        void shouldHandleValueRemoval() {
            underTest.put(KEY, VALUE);
            assertEquals(VALUE, underTest.remove(KEY),
                    "Should return removed value");

            assertNull(underTest.get(KEY),
                    "Should not retrieve removed value");
            assertEquals("default", underTest.get(KEY, "default"),
                    "Should return default value for removed key");
            assertFalse(underTest.containsKey(KEY),
                    "Should not contain removed key");
        }

        @Test
        @DisplayName("Should handle null values")
        void shouldHandleNullValues() {
            underTest.put(KEY, null);
            assertNull(underTest.get(KEY),
                    "Should not store null value");
            assertEquals("default", underTest.get(KEY, "default"),
                    "Should return default value for null stored value");
            assertFalse(underTest.containsKey(KEY),
                    "Should not contain key with null value");
        }

        @Test
        @DisplayName("Should handle empty string values")
        void shouldHandleEmptyStringValues() {
            underTest.put(KEY, "");
            assertEquals("", underTest.get(KEY),
                    "Should store and retrieve empty string");
            assertEquals("", underTest.get(KEY, "default"),
                    "Should return empty string instead of default");
            assertTrue(underTest.containsKey(KEY),
                    "Should contain key with empty string value");
        }

        @Test
        @DisplayName("Should handle null keys")
        void shouldHandleNullKeys() {
            underTest.put(null, VALUE);
            assertNull(underTest.get(null),
                    "Should not store with null key");
            assertEquals("default", underTest.get(null, "default"),
                    "Should return default for null key");
            assertFalse(underTest.containsKey(null),
                    "Should not contain null key");
        }
    }

    @Test
    @DisplayName("Should handle default values")
    void shouldHandleDefaultValues() {
        assertNull(underTest.get(KEY),
                "Should return null for non-existent key");
        assertEquals("default", underTest.get(KEY, "default"),
                "Should return default value for non-existent key");
        assertFalse(underTest.containsKey(KEY),
                "Should not contain non-existent key");
    }
}
