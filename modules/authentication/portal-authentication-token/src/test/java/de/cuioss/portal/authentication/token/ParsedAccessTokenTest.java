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
package de.cuioss.portal.authentication.token;

import static de.cuioss.portal.authentication.token.TestTokenProducer.*;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.domain.EmailGenerator;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import org.junit.jupiter.api.Test;

import java.util.Set;


@EnableTestLogger
class ParsedAccessTokenTest {

    private static final String TEST_CONTEXT = "Test";
    private static final String EXISTING_SCOPE = "email";
    private static final String DEFINITELY_NO_SCOPE = "Definitely No Scope";

    private static final CuiLogger LOGGER = new CuiLogger(ParsedAccessTokenTest.class);

    @Test
    void shouldParseValidToken() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);
        var retrievedToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);

        assertTrue(retrievedToken.isPresent());
        var parsedAccessToken = retrievedToken.get();

        assertEquals(initialToken, parsedAccessToken.getTokenString());
        assertEquals(3, parsedAccessToken.getScopes().size());
        assertTrue(parsedAccessToken.getScopes().contains(EXISTING_SCOPE));
        assertFalse(parsedAccessToken.getScopes().contains(DEFINITELY_NO_SCOPE));

        assertTrue(parsedAccessToken.providesScopes(Set.of(EXISTING_SCOPE)));
        assertFalse(parsedAccessToken.providesScopes(Set.of(DEFINITELY_NO_SCOPE)));
        assertFalse(parsedAccessToken.providesScopes(Set.of(DEFINITELY_NO_SCOPE, EXISTING_SCOPE)));

        assertTrue(parsedAccessToken.providesScopesAndDebugIfScopesAreMissing(Set.of(EXISTING_SCOPE), TEST_CONTEXT, LOGGER));
        assertFalse(parsedAccessToken.providesScopesAndDebugIfScopesAreMissing(Set.of(EXISTING_SCOPE, DEFINITELY_NO_SCOPE), TEST_CONTEXT, LOGGER));

        Set<String> missingScopes = parsedAccessToken.determineMissingScopes(Set.of(EXISTING_SCOPE));
        assertTrue(missingScopes.isEmpty());
        missingScopes = parsedAccessToken.determineMissingScopes(Set.of(DEFINITELY_NO_SCOPE));
        assertEquals(1, missingScopes.size());
        assertTrue(missingScopes.contains(DEFINITELY_NO_SCOPE));

        missingScopes = parsedAccessToken.determineMissingScopes(Set.of(EXISTING_SCOPE, DEFINITELY_NO_SCOPE));
        assertEquals(1, missingScopes.size());
        assertTrue(missingScopes.contains(DEFINITELY_NO_SCOPE));

    }

    @Test
    void shouldHandleMissingScopes() {
        String initialToken = validSignedEmptyJWT();
        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertTrue(parsedAccessToken.get().getScopes().isEmpty());
    }


    @Test
    void shouldHandleGivenRoles() {
        String initialToken = validSignedJWTWithClaims(SOME_ROLES);
        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertTrue(parsedAccessToken.get().hasRole("reader"));
    }

    @Test
    void shouldHandleMissingRoles() {
        String initialToken = validSignedJWTWithClaims(SOME_ROLES);
        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertFalse(parsedAccessToken.get().hasRole(DEFINITELY_NO_SCOPE));
    }

    @Test
    void shouldHandleNoRoles() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);
        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertTrue(parsedAccessToken.get().getRoles().isEmpty());
    }

    @Test
    void shouldHandleSubjectId() {
        String expectedSubjectId = Generators.letterStrings(4, 9).next();
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES, expectedSubjectId);

        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertEquals(expectedSubjectId, parsedAccessToken.get().getSubjectId());
    }

    @Test
    void shouldHandleGivenEmail() {
        String expectedEmail = new EmailGenerator().next();
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, expectedEmail, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertEquals(expectedEmail, parsedAccessToken.get().getEmail().get());
    }

    @Test
    void shouldHandleMissingEmail() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());

        assertFalse(parsedAccessToken.get().getEmail().isPresent());
    }

    @Test
    void shouldHandleGivenName() {
        String initialToken = validSignedJWTWithClaims(SOME_NAME);

        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());

        assertEquals("hello", parsedAccessToken.get().getName().get());
    }

    @Test
    void shouldHandleMissingName() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertFalse(parsedAccessToken.get().getName().isPresent());
    }


    @Test
    void shouldHandlePreferredName() {
        String initialToken = validSignedJWTWithClaims(SOME_NAME);

        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());
        assertEquals("world", parsedAccessToken.get().getPreferredUsername().get());
    }

    @Test
    void shouldHandleMissingPreferredName() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        var parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.isPresent());

        assertFalse(parsedAccessToken.get().getPreferredUsername().isPresent());
    }

}
