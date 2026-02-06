/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.test.tests.configuration;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests AbstractConfigurationKeyVerifierTest")
class AbstractConfigurationKeyVerifierTestTest extends AbstractConfigurationKeyVerifierTest {

    static final String PATH1 = "mock-configuration";

    @Getter
    private String configSourceName;

    @Getter
    private List<String> keysIgnoreList;

    @Getter
    private List<String> configurationKeysIgnoreList;

    @BeforeEach
    void before() {
        configSourceName = PATH1;
        keysIgnoreList = new ArrayList<>();
        configurationKeysIgnoreList = new ArrayList<>();
    }

    @Override
    public Class<?> getKeyHolder() {
        return ConfigurationKeys.class;
    }

    @Nested
    @DisplayName("Key Resolution Tests")
    class KeyResolutionTest {

        @Test
        @DisplayName("Should resolve all valid keys")
        void shouldResolveAllValidKeys() {
            SortedSet<String> keys = resolveKeyNames();
            assertEquals(4, keys.size(), "Should find all public static final String fields");
            assertTrue(keys.contains(ConfigurationKeys.KEY_1), "Should contain KEY_1");
            assertTrue(keys.contains(ConfigurationKeys.KEY_2), "Should contain KEY_2");
            assertTrue(keys.contains(ConfigurationKeys.KEY_3), "Should contain KEY_3");
            assertTrue(keys.contains(ConfigurationKeys.NOT_PREFIXED), "Should contain NOT_PREFIXED");
        }

        @Test
        @DisplayName("Should ignore non-String fields")
        void shouldIgnoreNonStringFields() {
            SortedSet<String> keys = resolveKeyNames();
            assertFalse(keys.contains(ConfigurationKeys.INTEGER_KEY.toString()),
                    "Should not contain INTEGER_KEY");
        }

        @Test
        @DisplayName("Should ignore package-private fields")
        void shouldIgnorePackagePrivateFields() {
            SortedSet<String> keys = resolveKeyNames();
            assertFalse(keys.contains("de.cui.test.private"),
                    "Should not contain package-private field");
        }
    }

    @Nested
    @DisplayName("Key Filtering Tests")
    class KeyFilteringTest {

        @Test
        @DisplayName("Should filter single key")
        void shouldFilterSingleKey() {
            assertEquals(4, resolveKeyNames().size(), "Initial size should be 4");

            keysIgnoreList.add(ConfigurationKeys.KEY_1);
            assertEquals(3, resolveKeyNames().size(), "Size should be 3 after filtering one key");
            assertFalse(resolveKeyNames().contains(ConfigurationKeys.KEY_1),
                    "Filtered key should not be present");
        }

        @Test
        @DisplayName("Should filter multiple keys")
        void shouldFilterMultipleKeys() {
            keysIgnoreList.add(ConfigurationKeys.KEY_1);
            keysIgnoreList.add(ConfigurationKeys.KEY_2);

            assertEquals(2, resolveKeyNames().size(), "Size should be 2 after filtering two keys");
            assertFalse(resolveKeyNames().contains(ConfigurationKeys.KEY_1),
                    "First filtered key should not be present");
            assertFalse(resolveKeyNames().contains(ConfigurationKeys.KEY_2),
                    "Second filtered key should not be present");
        }

        @Test
        @DisplayName("Should filter configuration keys by prefix")
        void shouldFilterConfigurationKeysByPrefix() {
            assertEquals(4, resolveConfigurationKeyNames().size(),
                    "Initial configuration keys size should be 4");

            configurationKeysIgnoreList.add("de.cui");
            assertEquals(1, resolveConfigurationKeyNames().size(),
                    "Should only contain non-prefixed key after filtering");

            var remainingKeys = resolveConfigurationKeyNames();
            assertTrue(remainingKeys.contains(ConfigurationKeys.NOT_PREFIXED),
                    "NOT_PREFIXED key should remain");
            assertFalse(remainingKeys.contains(ConfigurationKeys.KEY_1),
                    "Prefixed key should be filtered");
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTest {

        @Test
        @DisplayName("Should handle empty config source name")
        void shouldHandleEmptyConfigSourceName() {
            configSourceName = "";
            assertThrows(AssertionError.class, AbstractConfigurationKeyVerifierTestTest.this::extractConfigurationKeys,
                    "Should throw AssertionError for empty config source name");
        }

        @Test
        @DisplayName("Should handle null config source name")
        void shouldHandleNullConfigSourceName() {
            configSourceName = null;
            assertThrows(AssertionError.class, AbstractConfigurationKeyVerifierTestTest.this::extractConfigurationKeys,
                    "Should throw AssertionError for null config source name");
        }
    }
}
