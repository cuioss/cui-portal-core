package de.cuioss.portal.authentication.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenFactoryTest {

    private TokenFactory tokenFactory;


    @BeforeEach
    void setUp() throws IOException {
        tokenFactory = TokenFactory.of(JwksAwareTokenParserTest.getValidJWKSParserWithLocalJWKS());
    }

    @Test
    void shouldCreateAccessToken() {
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);
        var parsedToken = tokenFactory.createAccessToken(token);

        assertTrue(parsedToken.isPresent());
        assertFalse(parsedToken.get().getScopes().isEmpty());
        assertNotNull(parsedToken.get().getSubject());
        assertEquals(TestTokenProducer.ISSUER, parsedToken.get().getIssuer());
    }

    @Test
    void shouldCreateIdToken() {
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_ID_TOKEN);
        var parsedToken = tokenFactory.createIdToken(token);

        assertTrue(parsedToken.isPresent());
        assertNotNull(parsedToken.get().getSubject());
        assertEquals(TestTokenProducer.ISSUER, parsedToken.get().getIssuer());
    }

    @Test
    void shouldCreateRefreshToken() {
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.REFRESH_TOKEN);
        var parsedToken = tokenFactory.createRefreshToken(token);

        assertTrue(parsedToken.isPresent());
        assertNotNull(parsedToken.get().getTokenString());
    }

    @Test
    void shouldHandleExpiredToken() {
        var expiredToken = TestTokenProducer.validSignedJWTExpireAt(
                Instant.now().minus(1, ChronoUnit.HOURS));

        var token = tokenFactory.createAccessToken(expiredToken);

        assertFalse(token.isPresent());
    }

    @Test
    void shouldHandleInvalidIssuer() throws IOException {
        var wrongIssuerTokenFactory = TokenFactory.of(JwksAwareTokenParserTest.getInvalidValidJWKSParserWithLocalJWKSAndWrongIssuer());
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);
        var parsedToken = wrongIssuerTokenFactory.createAccessToken(token);
        assertFalse(parsedToken.isPresent());
    }

    @Test
    void shouldHandleInvalidSignature() throws IOException {
        var wrongSignatureTokenFactory = TokenFactory.of(JwksAwareTokenParserTest.getInvalidJWKSParserWithWrongLocalJWKS());
        var token = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);
        var parsedToken = wrongSignatureTokenFactory.createAccessToken(token);
        assertFalse(parsedToken.isPresent());
    }
}
