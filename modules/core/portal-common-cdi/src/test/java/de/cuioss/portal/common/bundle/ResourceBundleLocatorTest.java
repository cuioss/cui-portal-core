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
package de.cuioss.portal.common.bundle;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static de.cuioss.portal.common.PortalCommonCDILogMessages.BUNDLE;

import de.cuioss.portal.common.bundle.support.HighPrioBundles;
import de.cuioss.portal.common.bundle.support.InvalidBundlePath;
import de.cuioss.portal.common.bundle.support.MissingBundle;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Locale;

@EnableTestLogger(debug = ResourceBundleLocator.class)
@DisplayName("Tests the ResourceBundleLocator functionality")
class ResourceBundleLocatorTest {

    @Test
    @DisplayName("Should handle happy case")
    void shouldHandleHappyCase() {
        assertTrue(new HighPrioBundles().getBundle(Locale.GERMANY).isPresent());

        // Verify successful bundle loading
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.DEBUG,
                "Successfully loaded ");
        // Verify no warnings or errors in happy path
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.WARN, ResourceBundleLocator.class);
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.ERROR, ResourceBundleLocator.class);
    }

    @Test
    @DisplayName("Should handle missing path")
    void shouldHandleMissingPath() {
        var locator = new MissingBundle();
        assertFalse(locator.getBundle(Locale.GERMANY).isPresent());
    }

    @Test
    @DisplayName("Should handle invalid path")
    void shouldHandleInvalidPath() {
        var locator = new InvalidBundlePath();
        assertFalse(locator.getBundle(Locale.GERMANY).isPresent());

        // Verify error message
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN,
                BUNDLE.WARN.LOAD_FAILED.resolveIdentifierString());
        // Verify no success messages in error case
        LogAsserts.assertNoLogMessagePresent(TestLogLevel.INFO, ResourceBundleLocator.class);
    }
}
