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
import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresent;
import static de.cuioss.test.juli.LogAsserts.assertSingleLogMessagePresent;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.valueobjects.ValueObjectTest;
import de.cuioss.test.valueobjects.api.contracts.VerifyBuilder;

@VerifyBuilder(required = {"action"})
@EnableTestLogger
class LoginEventTest extends ValueObjectTest<LoginEvent> {

    @Test
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
