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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
