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

import de.cuioss.portal.common.PortalCommonCDILogMessages;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

@EnableAutoWeld
@EnableTestLogger(trace = PortalResourceBundleWrapperImplTest.class)
@AddBeanClasses({PortalMessages.class, ResourceBundleRegistry.class, ResourceBundleWrapperImpl.class})
@ActivateScopes({RequestScoped.class, SessionScoped.class})
@DisplayName("Verifies the ResourceBundleWrapper implementation")
class PortalResourceBundleWrapperImplTest implements ShouldHandleObjectContracts<ResourceBundleWrapper> {

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

    @BeforeEach
    void beforeTest() {
        projectStage = ProjectStage.PRODUCTION;
        locale = Locale.ENGLISH;
    }

    @Nested
    @DisplayName("Basic Message Resolution Tests")
    class MessageResolutionTests {
        @Test
        @DisplayName("Should return correct messages from different bundles")
        void shouldReturnCorrectMessage() {
            assertAll("Message resolution from different bundles",
                    () -> assertEquals("Logout", underTest.getString("page.logout.srHeader"),
                            "Should return correct portal message"),
                    () -> assertEquals(PORTAL_TITLE, underTest.getString("portal.title"),
                            "Should return correct vendor message")
            );
        }

        @Test
        @DisplayName("Should switch message bundle when locale changes")
        void shouldSwitchMessageBundleOnLocaleChange() {
            assertAll("Locale change handling",
                    () -> assertEquals("Internal server error", underTest.getString("page.error.title"),
                            "Should return English message initially"),
                    () -> {
                        locale = Locale.GERMAN;
                        localeChangeEvent.fire(Locale.GERMAN);
                        assertEquals("Interner Server Fehler", underTest.getString("page.error.title"),
                                "Should return German message after locale change");
                    }
            );
        }

        @ParameterizedTest
        @CsvSource({
                "page.logout.srHeader, Logout",
                "portal.title, CUI Oss Portal",
                "page.error.title, Internal server error"
        })
        @DisplayName("Should return correct messages for various keys")
        void shouldReturnCorrectMessages(String key, String expected) {
            assertEquals(expected, underTest.getString(key),
                    () -> "Should return correct message for key: " + key);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        @Test
        @DisplayName("Should throw MissingResourceException for invalid key in development")
        void shouldFailOnInvalidKey() {
            projectStage = ProjectStage.DEVELOPMENT;
            var exception = assertThrows(MissingResourceException.class,
                    () -> underTest.getString("not.there"),
                    "Should throw MissingResourceException for invalid key in development mode");
            assertTrue(exception.getMessage().contains(PortalCommonCDILogMessages.PREFIX + "-505"),
                    "Should contain error code");
        }

        @Test
        @DisplayName("Should log on invalid key in production")
        void shouldLogOnInvalidKey() {
            projectStage = ProjectStage.PRODUCTION;
            var result = underTest.getString("not.there");
            assertAll("Invalid key handling in production",
                    () -> assertEquals("??not.there??", result, "Should wrap invalid key with ??"),
                    () -> LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN,
                            PortalCommonCDILogMessages.PREFIX + "-505: No key 'not.there'")
            );
        }

        @ParameterizedTest
        @DisplayName("Should handle invalid input gracefully in production")
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        void shouldHandleInvalidInput(String invalidKey) {
            projectStage = ProjectStage.PRODUCTION;
            var result = underTest.getString(invalidKey);
            var expectedKey = invalidKey == null ? "null" : invalidKey;
            assertAll("Invalid input handling",
                    () -> assertEquals("??" + expectedKey + "??", result,
                            "Should wrap invalid key with ??"),
                    () -> LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN,
                            PortalCommonCDILogMessages.PREFIX + "-505: No key '" + expectedKey + "'")
            );
        }
    }

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
}
