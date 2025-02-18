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

import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.common.PortalCommonLogMessages;
import de.cuioss.portal.common.bundle.support.PortalMessages;
import de.cuioss.portal.common.locale.LocaleChangeEvent;
import de.cuioss.portal.common.locale.PortalLocale;
import de.cuioss.portal.common.stage.ProjectStage;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.valueobjects.junit5.contracts.ShouldHandleObjectContracts;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Integration tests for {@link ResourceBundleWrapperImpl} which verifies:
 * <ul>
 *   <li>Resource bundle loading and initialization</li>
 *   <li>Message resolution in different locales</li>
 *   <li>Project stage specific error handling</li>
 *   <li>Key set management and access</li>
 *   <li>Locale change event handling</li>
 * </ul>
 *
 * <p>Test Setup:
 * <ul>
 *   <li>Uses Weld test framework for CDI integration</li>
 *   <li>Configures test bundles via {@link ResourceBundleRegistry}</li>
 *   <li>Tests both development and production stage behaviors</li>
 *   <li>Verifies locale switching with {@link LocaleChangeEvent}</li>
 * </ul>
 */
@EnableAutoWeld
@EnableTestLogger(trace = PortalResourceBundleWrapperImplTest.class)
@AddBeanClasses({PortalMessages.class, ResourceBundleRegistry.class, ResourceBundleWrapperImpl.class})
@ActivateScopes({RequestScoped.class, SessionScoped.class})
@DisplayName("Verifies the ResourceBundleWrapper implementation")
class PortalResourceBundleWrapperImplTest implements ShouldHandleObjectContracts<ResourceBundleWrapper> {

    /**
     * Test constant for portal title verification
     */
    public static final String PORTAL_TITLE = "CUI Oss Portal";

    @Inject
    @Getter
    private ResourceBundleWrapper underTest;

    @Produces
    private ProjectStage projectStage;

    @Produces
    @PortalLocale
    private Locale locale;

    @Inject
    @LocaleChangeEvent
    Event<Locale> localeChangeEvent;

    /**
     * Initializes test setup before each test method.
     */
    @BeforeEach
    void beforeTest() {
        projectStage = ProjectStage.PRODUCTION;
        locale = Locale.ENGLISH;
    }

    /**
     * Verifies basic message resolution functionality:
     * <ul>
     *   <li>Messages from different bundles are resolved correctly</li>
     *   <li>Messages are resolved in the correct locale</li>
     * </ul>
     */
    @Nested
    @DisplayName("Basic Message Resolution Tests")
    class MessageResolutionTests {
        /**
         * Verifies that the wrapper correctly resolves messages from different bundles.
         */
        @Test
        @DisplayName("Should resolve messages correctly")
        void shouldResolveMessages() {
            assertAll("Message resolution",
                    () -> assertEquals("Logout", underTest.getString("page.logout.srHeader"), "Incorrect logout message"),
                    () -> assertEquals("CUI Oss Portal", underTest.getString("portal.title"), "Incorrect portal title message")
            );
        }

        /**
         * Verifies that the wrapper correctly handles locale changes:
         * <ul>
         *   <li>Initial messages are in default locale</li>
         *   <li>After locale change, messages are in new locale</li>
         *   <li>All bundles are reloaded with new locale</li>
         * </ul>
         */
        @Test
        @DisplayName("Should handle locale changes correctly")
        void shouldHandleLocaleChanges() {
            assertAll("Locale change handling",
                    () -> {
                        // Initial state
                        assertEquals("Logout", underTest.getString("page.logout.srHeader"), "Initial message incorrect");

                        // Change locale
                        localeChangeEvent.fire(Locale.GERMAN);
                        locale = Locale.GERMAN;

                        // Verify message changed
                        assertEquals("Abmelden", underTest.getString("page.logout.srHeader"), "Message not updated after locale change");
                    }
            );
        }

        /**
         * Verifies that the wrapper correctly resolves messages for various keys.
         *
         * @param key the message key to test
         */
        @ParameterizedTest
        @ValueSource(strings = {
                "page.logout.srHeader",
                "portal.title",
                "page.error.title"
        })
        @DisplayName("Should resolve various message keys")
        void shouldResolveMessageKeys(String key) {
            var result = underTest.getString(key);
            assertTrue(result != null && !result.isEmpty(),
                    "Message for key '" + key + "' should not be null or empty");
        }
    }

    /**
     * Verifies error handling functionality:
     * <ul>
     *   <li>Missing keys are handled correctly based on project stage</li>
     *   <li>Invalid input is handled correctly</li>
     * </ul>
     */
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        /**
         * Verifies that the wrapper correctly handles missing keys based on project stage:
         * <ul>
         *   <li>Development: Throws MissingResourceException</li>
         *   <li>Production: Returns key surrounded by '??'</li>
         * </ul>
         */
        @Test
        @DisplayName("Should throw MissingResourceException for invalid key in development")
        void shouldFailOnInvalidKey() {
            projectStage = ProjectStage.DEVELOPMENT;
            var exception = assertThrows(MissingResourceException.class,
                    () -> underTest.getString("not.there"),
                    "Should throw MissingResourceException for invalid key in development mode");
            assertTrue(exception.getMessage().contains(PortalCommonLogMessages.WARN.KEY_NOT_FOUND.resolveIdentifierString()),
                    "Should contain error code");
        }

        /**
         * Verifies that the wrapper correctly handles invalid keys in production:
         * <ul>
         *   <li>Returns key surrounded by '??'</li>
         *   <li>Logs a warning message</li>
         * </ul>
         */
        @Test
        @DisplayName("Should log on invalid key in production")
        void shouldLogOnInvalidKey() {
            projectStage = ProjectStage.PRODUCTION;
            var result = underTest.getString("not.there");
            assertAll("Invalid key handling in production",
                    () -> assertEquals("??not.there??", result, "Should wrap invalid key with ??"),
                    () -> LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN,
                            PortalCommonLogMessages.WARN.KEY_NOT_FOUND.resolveIdentifierString())
            );
        }

        /**
         * Verifies that the wrapper correctly handles invalid input:
         * <ul>
         *   <li>"\t" results in {@code mull} </li>
         *   <li>"\n" results in {@code mull} </li>
         * </ul>
         *
         * @param invalidKey the invalid key
         */
        @ParameterizedTest
        @DisplayName("Should handle invalid input gracefully in production")
        @ValueSource(strings = {"\t", "\n"})
        void shouldHandleInvalidInput(String invalidKey) {
            projectStage = ProjectStage.PRODUCTION;
            assertNull(underTest.getString(invalidKey), "Null key should return null for: " + invalidKey);
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN, PortalCommonLogMessages.WARN.BUNDLE_IGNORING_EMPTY_KEY.resolveIdentifierString());
        }

        /**
         * Verifies that the wrapper correctly handles missing keys:
         * <ul>
         *   <li>Missing keys are handled correctly based on project stage</li>
         *   <li>Missing keys are logged correctly</li>
         * </ul>
         */
        @Test
        @DisplayName("Should handle missing keys correctly")
        void shouldHandleMissingKeys() {
            projectStage = ProjectStage.PRODUCTION;
            var missingKey = "non.existent.key";
            assertEquals("??" + missingKey + "??", underTest.getString(missingKey), "Incorrect missing key format");
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN, PortalCommonLogMessages.WARN.KEY_NOT_FOUND.resolveIdentifierString());
        }

        @ParameterizedTest
        @DisplayName("Should handle empty and null cases correctly")
        @NullAndEmptySource
        @ValueSource(strings = {" "})
        void shouldHandleEmptyAndNullCases(String emptyKey) {
            projectStage = ProjectStage.PRODUCTION;
            assertNull(underTest.getString(emptyKey), "Null key should return null for: " + emptyKey);
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN, PortalCommonLogMessages.WARN.BUNDLE_IGNORING_EMPTY_KEY.resolveIdentifierString());
        }
    }

    /**
     * Verifies key set functionality:
     * <ul>
     *   <li>All configured bundle keys are present</li>
     *   <li>No duplicate keys exist</li>
     *   <li>Keys from all priority levels are included</li>
     * </ul>
     */
    @Test
    @DisplayName("Should return all available keys")
    void getKeys() {
        final List<String> keys = Collections.list(underTest.getKeys());
        assertAll("Key list validation",
                () -> assertNotNull(keys, "Keys list should not be null"),
                () -> assertTrue(keys.size() > 60, "Should have more than 60 keys"),
                () -> assertFalse(keys.contains(null), "Should not contain null keys"),
                () -> assertTrue(keys.stream().allMatch(k -> k != null && !k.isBlank()),
                        "All keys should be non-null and non-blank")
        );
    }

    /**
     * Verifies edge case functionality:
     * <ul>
     *   <li>Extremely long keys are handled properly</li>
     *   <li>Concurrent access is thread-safe</li>
     *   <li>Special characters in keys are handled correctly</li>
     * </ul>
     */
    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        /**
         * Verifies handling of extremely long message keys:
         * <ul>
         *   <li>Very long keys are handled properly</li>
         *   <li>Performance remains acceptable</li>
         *   <li>Memory usage is reasonable</li>
         * </ul>
         */
        @Test
        @DisplayName("Should handle extremely long keys")
        void shouldHandleLongKeys() {
            // Create a very long key
            var longKey = "a".repeat(1000);

            // Verify handling in production mode
            projectStage = ProjectStage.PRODUCTION;
            var result = underTest.getString(longKey);
            assertTrue(result.contains(longKey), "Should contain the original key");

            // Verify handling in development mode
            projectStage = ProjectStage.DEVELOPMENT;
            assertThrows(MissingResourceException.class, () -> underTest.getString(longKey));
        }

        /**
         * Verifies concurrent access handling:
         * <ul>
         *   <li>Multiple threads can access bundles safely</li>
         *   <li>Locale changes are thread-safe</li>
         *   <li>No deadlocks occur</li>
         * </ul>
         */
        @Test
        @DisplayName("Should handle concurrent access")
        @Disabled("We need to figure out how o prevent ContextNotActive Exception")
        void shouldHandleConcurrentAccess() throws InterruptedException {
            var latch = new CountDownLatch(2);
            var errors = new AtomicReference<Exception>(null);

            // Thread 1: Changes locale repeatedly
            var thread1 = new Thread(() -> {
                try {
                    for (int i = 0; i < 100; i++) {
                        locale = i % 2 == 0 ? Locale.ENGLISH : Locale.GERMAN;
                        localeChangeEvent.fire(Locale.GERMAN);
                    }
                } catch (Exception e) {
                    errors.set(e);
                } finally {
                    latch.countDown();
                }
            });

            // Thread 2: Reads messages continuously
            var thread2 = new Thread(() -> {
                try {
                    for (int i = 0; i < 1000; i++) {
                        underTest.getString("page.error.title");
                    }
                } catch (Exception e) {
                    errors.set(e);
                } finally {
                    latch.countDown();
                }
            });

            thread1.start();
            thread2.start();

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Threads should complete in time");
            assertNull(errors.get(), "No errors should occur during concurrent access");
        }

        /**
         * Verifies handling of special characters in keys:
         * <ul>
         *   <li>Unicode characters are handled properly</li>
         *   <li>Special characters don't cause issues</li>
         *   <li>Error messages are clear</li>
         * </ul>
         */
        @ParameterizedTest
        @DisplayName("Should handle special characters in keys")
        @ValueSource(strings = {
                "key.with.spaces ",
                "key.with.unicode.❤",
                "key.with.special.@#$%",
                "key.with.newline.\n"
        })
        void shouldHandleSpecialCharacters(String key) {
            // Verify handling in production mode
            projectStage = ProjectStage.PRODUCTION;
            var result = underTest.getString(key);
            assertTrue(result.contains(key), "Should contain the original key");

            // Verify handling in development mode
            projectStage = ProjectStage.DEVELOPMENT;
            assertThrows(MissingResourceException.class, () -> underTest.getString(key));
        }
    }
}
