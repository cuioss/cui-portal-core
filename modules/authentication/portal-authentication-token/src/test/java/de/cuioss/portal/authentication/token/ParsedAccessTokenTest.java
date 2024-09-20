package de.cuioss.portal.authentication.token;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.generator.domain.EmailGenerator;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static de.cuioss.portal.authentication.token.TestTokenProducer.*;
import static org.junit.jupiter.api.Assertions.*;


@EnableTestLogger
class ParsedAccessTokenTest {

    private static final String TEST_CONTEXT = "Test";
    private static final String EXISTING_SCOPE = "email";
    private static final String DEFINITELY_NO_SCOPE = "Definitely No Scope";

    private static final CuiLogger LOGGER = new CuiLogger(ParsedAccessTokenTest.class);

    @Test
    void shouldParseValidToken() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);

        assertTrue(parsedAccessToken.isValid());
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
        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertEquals(0, parsedAccessToken.getScopes().size());
    }



    @Test
    void shouldHandleGivenRoles() {
        String initialToken = validSignedJWTWithClaims(SOME_ROLES);
        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.hasRole("reader"));
    }

    @Test
    void shouldHandleMissingRoles() {
        String initialToken = validSignedJWTWithClaims(SOME_ROLES);
        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertFalse(parsedAccessToken.hasRole(DEFINITELY_NO_SCOPE));
    }

    @Test
    void shouldHandleNoRoles() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);
        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertTrue(parsedAccessToken.getRoles().isEmpty());
    }

    @Test
    void shouldHandleSubjectId() {
        String expectedSubjectId = Generators.letterStrings(4, 9).next();
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES, expectedSubjectId);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertEquals(expectedSubjectId, parsedAccessToken.getSubjectId());
    }

    @Test
    void shouldHandleGivenEmail() {
        String expectedEmail = new EmailGenerator().next();
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, expectedEmail, DEFAULT_TOKEN_PARSER);

        assertEquals(expectedEmail, parsedAccessToken.getEmail().get());
    }

    @Test
    void shouldHandleMissingEmail() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertFalse(parsedAccessToken.getEmail().isPresent());
    }

    @Test
    void shouldHandleGivenName() {
        String initialToken = validSignedJWTWithClaims(SOME_NAME);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertEquals("hello", parsedAccessToken.getName().get());
    }

    @Test
    void shouldHandleMissingName() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertFalse(parsedAccessToken.getName().isPresent());
    }


    @Test
    void shouldHandlePreferredName() {
        String initialToken = validSignedJWTWithClaims(SOME_NAME);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertEquals("world", parsedAccessToken.getPreferredUsername().get());
    }

    @Test
    void shouldHandleMissingPreferredName() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        ParsedAccessToken parsedAccessToken = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
        assertFalse(parsedAccessToken.getPreferredUsername().isPresent());
    }

}
