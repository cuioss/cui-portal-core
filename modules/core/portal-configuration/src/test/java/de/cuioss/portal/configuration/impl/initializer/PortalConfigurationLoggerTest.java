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
package de.cuioss.portal.configuration.impl.initializer;

import de.cuioss.portal.configuration.initializer.ApplicationInitializer;
import de.cuioss.portal.configuration.initializer.PortalInitializer;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import lombok.Getter;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static de.cuioss.test.juli.LogAsserts.assertNoLogMessagePresent;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Tests for {@link PortalConfigurationLogger} verifying logging behavior and edge cases.
 */
@EnableAutoWeld
@EnableTestLogger
@DisplayName("PortalConfigurationLogger Tests")
class PortalConfigurationLoggerTest {

    @Inject
    @Getter
    @PortalInitializer
    private PortalConfigurationLogger underTest;

    @Inject
    @PortalInitializer
    private Instance<ApplicationInitializer> applicationInitializers;

    @BeforeEach
    void beforeTest() {
        applicationInitializers.forEach(ApplicationInitializer::initialize);
    }

    @Nested
    @DisplayName("Log Level Tests")
    class LogLevelTests {

        @Test
        @DisplayName("Should log on INFO level and ignore WARN")
        void shouldLogOnInfoLevel() {
            TestLogLevel.INFO.addLogger(PortalConfigurationLogger.class);
            underTest.initialize();
            assertNoLogMessagePresent(TestLogLevel.WARN, PortalConfigurationLogger.class);
            assertLogMessagePresentContaining(TestLogLevel.INFO, "Portal Configuration");
        }

        @Test
        @DisplayName("Should not log on DEBUG level")
        void shouldNotLogOnDebugLevel() {
            TestLogLevel.DEBUG.addLogger(PortalConfigurationLogger.class);
            underTest.initialize();
            assertNoLogMessagePresent(TestLogLevel.DEBUG, PortalConfigurationLogger.class);
        }

        @Test
        @DisplayName("Should handle multiple log level changes")
        void shouldHandleMultipleLogLevels() {
            TestLogLevel.INFO.addLogger(PortalConfigurationLogger.class);
            TestLogLevel.WARN.addLogger(PortalConfigurationLogger.class);
            TestLogLevel.ERROR.addLogger(PortalConfigurationLogger.class);

            underTest.initialize();

            assertLogMessagePresentContaining(TestLogLevel.INFO, "Portal Configuration");
            assertNoLogMessagePresent(TestLogLevel.WARN, PortalConfigurationLogger.class);
            assertNoLogMessagePresent(TestLogLevel.ERROR, PortalConfigurationLogger.class);
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle repeated initialization")
        void shouldHandleRepeatedInit() {
            TestLogLevel.INFO.addLogger(PortalConfigurationLogger.class);

            // Multiple initializations should not cause issues
            assertDoesNotThrow(() -> {
                underTest.initialize();
                underTest.initialize();
                underTest.initialize();
            });

            assertLogMessagePresentContaining(TestLogLevel.INFO, "Portal Configuration");
        }

        @Test
        @DisplayName("Should handle not log on warn level")
        void shouldHandleNoLogLevel() {
            TestLogLevel.WARN.addLogger(PortalConfigurationLogger.class);
            TestLoggerFactory.getTestHandler().clearRecords();
            assertDoesNotThrow(() -> underTest.initialize());
            assertNoLogMessagePresent(TestLogLevel.INFO, PortalConfigurationLogger.class);
        }
    }
}
