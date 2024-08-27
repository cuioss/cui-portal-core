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

import static de.cuioss.portal.configuration.util.ConfigurationPlaceholderHelper.replacePlaceholders;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.test.juli.junit5.EnableTestLogger;

@EnableTestLogger(debug = ConfigurationPlaceholderHelper.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@SuppressWarnings("el-syntax")
class ConfigurationPlaceholderHelperTest {

    private Set<String> addedSysProperties = new HashSet<>();

    @AfterEach
    void cleanSystemProperties() {
        addedSysProperties.forEach(System::clearProperty);
        addedSysProperties = new HashSet<>();
    }

    @Test
    void replaceNull() {
        assertNull(assertDoesNotThrow(() -> replacePlaceholders(null, true)));
    }

    @Test
    void placeNothing() {
        assertEquals("test", assertDoesNotThrow(() -> replacePlaceholders("test", true)));

        assertEquals("test:a", assertDoesNotThrow(() -> replacePlaceholders("test:a", true)));

        assertEquals("{test:a}", assertDoesNotThrow(() -> replacePlaceholders("{test:a}", true)));
    }

    @Test
    void ignoreMissingKey() {
        var result = assertDoesNotThrow(() -> replacePlaceholders("start${missing.key}end", false));

        assertEquals("start${missing.key}end", result);
    }

    @Test
    void missingKeysAreLogged() {
        assertDoesNotThrow(() -> replacePlaceholders("${missing_1}${missing_2}", false));

        var warnMsgs = TestLoggerFactory.getTestHandler().resolveLogMessages(TestLogLevel.WARN);
        assertEquals(1, warnMsgs.size(), "Missing WARN log statement for Portal-161 message");
        var firstWarnMsg = warnMsgs.get(0).getMessage();
        switch (firstWarnMsg) {
            case "Portal-161: Missing config key/s: missing_1, missing_2",
                 "Portal-161: Missing config key/s: missing_2, missing_1" -> {
                // test passed
            }
            default -> fail("Unexpected WARN log statement for Portal-161: " + firstWarnMsg);
        }
    }

    @Test
    void ignoreMissingKeyAsDefault() {
        var result = assertDoesNotThrow(() -> replacePlaceholders("start${key1:${key2}}end", false));

        assertEquals("start${key2}end", result);
    }

    @Test
    void ignoreInvalidPlaceholder() {
        assertEquals("start{missing.key}end",
                assertDoesNotThrow(() -> replacePlaceholders("start{missing.key}end", false)));

        assertEquals("start${missing.key end",
                assertDoesNotThrow(() -> replacePlaceholders("start${missing.key end", false)));
    }

    @Test
    void usePlaceholderDefaultIfKeyMissing() {
        var result = assertDoesNotThrow(() -> replacePlaceholders("${MISSING_KEY:default}", false));

        assertEquals("default", result);
    }

    @Test
    void replaceNestedPlaceholders() {
        assertEquals(">>default2<<", assertDoesNotThrow(
                () -> replacePlaceholders(">>${$1:${NESTED_KEY_1:${NESTED_KEY_2:default2}}}<<", false)));
    }

    @Test
    void replaceMultilineCRLF() {
        addSystemProperty("line.key", "line2");

        var result = assertDoesNotThrow(() -> replacePlaceholders("line1\r\n${line.key}\r\nline3", false));

        assertEquals("line1\r\nline2\r\nline3", result);
    }

    @Test
    void replaceMultilineCR() {
        addSystemProperty("line.key", "line2");

        var result = assertDoesNotThrow(() -> replacePlaceholders("line1\n${line.key}\nline3", false));

        assertEquals("line1\nline2\nline3", result);
    }

    @Test
    void exception_dontThrowIfDefaultIsAvailable() {
        var result = assertDoesNotThrow(() -> replacePlaceholders("${MISSING_KEY:default}", true));

        assertEquals("default", result);
    }

    @Test
    void exception_throwIfKeyMissing() {
        var ex = assertThrows(NoSuchElementException.class, () -> replacePlaceholders("${MISSING_KEY}", true));

        assertEquals("Portal-161: Missing config key/s: MISSING_KEY", ex.getMessage());
    }

    @Test
    void exceptionIfPlaceholdersNestedTooDeep() {
        var ex = assertThrows(ConfigKeyNestingException.class,
                () -> replacePlaceholders("${KEY_1:${KEY_2:${KEY_3:${KEY_4:${KEY_5:${KEY_6}}}}}}", false));
        assertEquals("Config key is nested too deep: KEY_1", ex.getMessage());
        assertEquals("Config key is nested too deep: KEY_2", ex.getCause().getMessage());
        assertEquals("Config key is nested too deep: KEY_3", ex.getCause().getCause().getMessage());
        assertEquals("Config key is nested too deep: KEY_4", ex.getCause().getCause().getCause().getMessage());
        assertEquals("Config key is nested too deep: KEY_5",
                ex.getCause().getCause().getCause().getCause().getMessage());
    }

    private void addSystemProperty(final String key, final String value) {
        System.setProperty(key, value);
        addedSysProperties.add(key);
    }
}
