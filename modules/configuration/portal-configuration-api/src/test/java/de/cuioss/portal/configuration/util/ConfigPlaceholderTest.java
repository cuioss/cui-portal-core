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
package de.cuioss.portal.configuration.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("el-syntax")
class ConfigPlaceholderTest {

    @Test
    void npeIfConfigKeyIsNull() {
        assertThrows(NullPointerException.class, () -> new ConfigPlaceholder(null));
        assertThrows(NullPointerException.class, () -> new ConfigPlaceholder(null, null));
    }

    @Test
    void keyOnlyConstructorSetsDefaultValueToNull() {
        var nullDefault = new ConfigPlaceholder("key").getDefaultValue();
        assertNotNull(nullDefault);
        assertTrue(nullDefault.isEmpty());
    }

    @Test
    void returnsOptionalEmptyDefaultValueIfNull() {
        var nullDefault = new ConfigPlaceholder("key", null).getDefaultValue();
        assertNotNull(nullDefault);
        assertTrue(nullDefault.isEmpty());
    }

    @Test
    void returnsDefaultValueIfNotNull() {
        var nullDefault = new ConfigPlaceholder("key", "default").getDefaultValue();
        assertNotNull(nullDefault);
        assertTrue(nullDefault.isPresent());
        assertEquals("default", nullDefault.get());
    }


    @Test
    void verifyInvalidPlaceholders() {
        assertFalse(ConfigPlaceholder.isPlaceholder(null));
        assertFalse(ConfigPlaceholder.isPlaceholder(""));
        assertFalse(ConfigPlaceholder.isPlaceholder("${key"));
        assertFalse(ConfigPlaceholder.isPlaceholder("${key:default"));
        assertFalse(ConfigPlaceholder.isPlaceholder(" ${key}"));
        assertFalse(ConfigPlaceholder.isPlaceholder(" ${key:default}"));
        assertFalse(ConfigPlaceholder.isPlaceholder("${:default}"));
    }

    @Test
    void verifyValidPlaceholders() {
        assertTrue(ConfigPlaceholder.isPlaceholder("${key}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:default}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:foo:bar}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:${key2:default}crap}"));
        assertTrue(ConfigPlaceholder.isPlaceholder("${key:${key2}crap}"));
    }

    @Test
    void splitInvalidPlaceholderThrowsException() {
        assertThrows(NullPointerException.class, () -> ConfigPlaceholder.split(null));
        assertThrows(IllegalArgumentException.class, () -> ConfigPlaceholder.split(""));
        assertThrows(IllegalArgumentException.class, () -> ConfigPlaceholder.split(" ${key}"));
    }

    @Test
    void splitPlaceholderWithKeyOnly() {
        var result = ConfigPlaceholder.split("${key}");

        assertEquals("key", result.getConfigKey());
        assertTrue(result.getDefaultValue().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({ "${key:default}, key, default", "${key:${key2}}, key, ${key2}", "${key:${key2}crap}, key, ${key2}crap",
            "${key:foo:bar}, key, foo:bar" })
    void splitPlaceholderWithDefault(String full, String expected1, String expected2) {
        var result = ConfigPlaceholder.split(full);

        assertEquals(expected1, result.getConfigKey());
        assertEquals(expected2, result.getDefaultValue().orElse(null));
    }
}
