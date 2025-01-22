package de.cuioss.portal.authentication.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenFactoryTest {

    private TokenFactory tokenFactory;

    @BeforeEach
    void setUp() {
        tokenFactory = TokenFactory.of(TestTokenProducer.DEFAULT_TOKEN_PARSER);
    }

    @Test
    void shouldCreateAccessToken() {
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);
        var parsedToken = tokenFactory.createAccessToken(token);

        assertNotNull(parsedToken);
        assertFalse(parsedToken.getScopes().isEmpty());
        assertNotNull(parsedToken.getSubject());
        assertNotNull(parsedToken.getIssuer());
    }

    @Test
    void shouldCreateIdToken() {
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_ID_TOKEN);
        var parsedToken = tokenFactory.createIdToken(token);

        assertNotNull(parsedToken);
        assertNotNull(parsedToken.getSubject());
        assertNotNull(parsedToken.getIssuer());
    }

    @Test
    void shouldCreateRefreshToken() {
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.REFRESH_TOKEN);
        var parsedToken = tokenFactory.createRefreshToken(token);

        assertNotNull(parsedToken);
    }

    @Test
    void shouldHandleExpiredToken() {
        var expiredToken = TestTokenProducer.validSignedJWTExpireAt(
                Instant.now().minus(1, ChronoUnit.HOURS));
        var token = tokenFactory.createAccessToken(expiredToken);

        assertNotNull(token);
        assertTrue(token.isEmpty());
    }

    @Test
    void shouldHandleInvalidIssuer() {
        var wrongIssuerTokenFactory = TokenFactory.of(TestTokenProducer.WRONG_ISSUER_TOKEN_PARSER);
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);
        var parsedToken = wrongIssuerTokenFactory.createAccessToken(token);
        assertNotNull(parsedToken);
        assertTrue(parsedToken.isEmpty());
    }

    @Test
    void shouldHandleInvalidSignature() {
        var wrongSignatureTokenFactory = TokenFactory.of(TestTokenProducer.WRONG_SIGNATURE_TOKEN_PARSER);
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);
        var parsedToken = wrongSignatureTokenFactory.createAccessToken(token);
        assertNotNull(parsedToken);
        assertTrue(parsedToken.isEmpty());
    }
}
