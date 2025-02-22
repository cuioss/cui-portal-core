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
package de.cuioss.portal.configuration.connections.impl;

import org.junit.jupiter.api.Test;

import static de.cuioss.tools.collect.CollectionLiterals.immutableMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationTypeTest {

    private static final String TEST = "test";
    private static final String SOME_VALUE = "some.value";
    private static final String AUTH_BASE = "authentication.";
    private static final String BASIC_BASE = AUTH_BASE + "basic.";
    private static final String CERT_BASE = AUTH_BASE + "certificate.";
    private static final String TOKEN_BASE = AUTH_BASE + "token.";

    @Test
    void shouldHandleEmptyOrInvalidMap() {
        assertEquals(AuthenticationType.NONE, AuthenticationType.resolveFrom(TEST, null));
        assertEquals(AuthenticationType.NONE, AuthenticationType.resolveFrom(TEST, immutableMap()));
        assertEquals(AuthenticationType.NONE,
                AuthenticationType.resolveFrom(TEST, immutableMap("some.key", SOME_VALUE)));
        assertEquals(AuthenticationType.NONE,
                AuthenticationType.resolveFrom(TEST, immutableMap(AUTH_BASE + "notThere", SOME_VALUE)));
    }

    @Test
    void shouldResolveCertificate() {
        assertEquals(AuthenticationType.CERTIFICATE, AuthenticationType.resolveFrom(TEST,
                immutableMap("authentication", AuthenticationType.CERTIFICATE.name())));
        assertEquals(AuthenticationType.CERTIFICATE,
                AuthenticationType.resolveFrom(TEST, immutableMap(CERT_BASE + "keyStore", SOME_VALUE)));
        assertEquals(AuthenticationType.CERTIFICATE,
                AuthenticationType.resolveFrom(TEST, immutableMap(CERT_BASE + "keyStore.location", SOME_VALUE)));
    }

    @Test
    void shouldResolveBasic() {
        assertEquals(AuthenticationType.BASIC,
                AuthenticationType.resolveFrom(TEST, immutableMap("authentication", AuthenticationType.BASIC.name())));
        assertEquals(AuthenticationType.BASIC,
                AuthenticationType.resolveFrom(TEST, immutableMap(BASIC_BASE + "username", SOME_VALUE)));
    }

    @Test
    void shouldResolveApplicationToken() {
        assertEquals(AuthenticationType.TOKEN_APPLICATION,
                AuthenticationType.resolveFrom(TEST, immutableMap("authentication", "token.application")));
        assertEquals(AuthenticationType.TOKEN_APPLICATION,
                AuthenticationType.resolveFrom(TEST, immutableMap(TOKEN_BASE + "application", SOME_VALUE)));
    }

    @Test
    void shouldResolveUserToken() {
        assertEquals(AuthenticationType.TOKEN_FROM_USER,
                AuthenticationType.resolveFrom(TEST, immutableMap("authentication", "token.user")));
        assertEquals(AuthenticationType.TOKEN_FROM_USER,
                AuthenticationType.resolveFrom(TEST, immutableMap(TOKEN_BASE + "user", SOME_VALUE)));
    }
}
