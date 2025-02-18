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
package de.cuioss.portal.authentication;

import static de.cuioss.portal.authentication.PortalAuthenticationLogMessages.AUTH;
import static de.cuioss.test.juli.LogAsserts.assertSingleLogMessagePresent;
import static org.junit.jupiter.api.Assertions.assertThrows;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test cases for {@link LoginEvent} focusing on logging behavior and builder validation.
 */
@VerifyBuilder(required = {"action"})
@EnableTestLogger
class LoginEventTest extends ValueObjectTest<LoginEvent> {

    @Nested
    @DisplayName("Logging Behavior Tests")
    class LoggingTests {

        @Test
        @DisplayName("Should log successful login with username")
        void shouldLogLoginSuccess() {
            // given
            var username = Generators.nonEmptyStrings().next();
            var event = LoginEvent.builder()
                    .action(LoginEvent.Action.LOGIN_SUCCESS)
                    .username(username)
                    .build();

            // when
            event.logEvent();

            // then
            assertSingleLogMessagePresent(TestLogLevel.INFO, AUTH.INFO.LOGIN_SUCCESS.format(username));
        }

        @Test
        @DisplayName("Should log failed login with username")
        void shouldLogLoginFailed() {
            // given
            var username = Generators.nonEmptyStrings().next();
            var event = LoginEvent.builder()
                    .action(LoginEvent.Action.LOGIN_FAILED)
                    .username(username)
                    .build();

            // when
            event.logEvent();

            // then
            assertSingleLogMessagePresent(TestLogLevel.WARN, AUTH.WARN.LOGIN_FAILED.format(username));
        }

        @Test
        @DisplayName("Should log logout with username")
        void shouldLogLogout() {
            // given
            var username = Generators.nonEmptyStrings().next();
            var event = LoginEvent.builder()
                    .action(LoginEvent.Action.LOGOUT)
                    .username(username)
                    .build();

            // when
            event.logEvent();

            // then
            assertSingleLogMessagePresent(TestLogLevel.INFO, AUTH.INFO.LOGOUT.format(username));
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle null username by logging 'null'")
        void shouldHandleNullUsername() {
            // given
            var event = LoginEvent.builder()
                    .action(LoginEvent.Action.LOGIN_SUCCESS)
                    .username(null)
                    .build();

            // when
            event.logEvent();

            // then
            assertSingleLogMessagePresent(TestLogLevel.INFO, AUTH.INFO.LOGIN_SUCCESS.format("null"));
        }

        @Test
        @DisplayName("Should handle empty username")
        void shouldHandleEmptyUsername() {
            // given
            var event = LoginEvent.builder()
                    .action(LoginEvent.Action.LOGIN_SUCCESS)
                    .username("")
                    .build();

            // when
            event.logEvent();

            // then
            assertSingleLogMessagePresent(TestLogLevel.INFO, AUTH.INFO.LOGIN_SUCCESS.format(""));
        }

        @Test
        @DisplayName("Should handle username with special characters")
        void shouldHandleSpecialCharacters() {
            // given
            var username = "user@domain.com";
            var event = LoginEvent.builder()
                    .action(LoginEvent.Action.LOGIN_SUCCESS)
                    .username(username)
                    .build();

            // when
            event.logEvent();

            // then
            assertSingleLogMessagePresent(TestLogLevel.INFO, AUTH.INFO.LOGIN_SUCCESS.format(username));
        }
    }

    @Nested
    @DisplayName("Error Case Tests")
    class ErrorCaseTests {

        @Test
        @DisplayName("Should throw IllegalStateException for null action")
        void shouldThrowOnNullAction() {
            assertThrows(NullPointerException.class, () ->
                    LoginEvent.builder()
                            .action(null)
                            .username("testUser")
                            .build());
        }
    }
}
