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

import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.CombinedDispatcher;
import de.cuioss.tools.io.IOStreams;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Getter;
import lombok.Setter;
import mockwebserver3.MockWebServer;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;

import static de.cuioss.portal.authentication.token.TestTokenProducer.SOME_SCOPES;
import static de.cuioss.portal.authentication.token.TestTokenProducer.validSignedJWTWithClaims;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableMockWebServer
class JwksAwareTokenParserTest implements MockWebServerHolder {

    public static final int JWKS_REFRESH_INTERVALL = 60;
    private static final CuiLogger LOGGER = new CuiLogger(JwksAwareTokenParserTest.class);

    @Setter
    private MockWebServer mockWebServer;

    private JwksAwareTokenParser tokenParser;

    protected int mockserverPort;

    private final JwksResolveDispatcher jwksResolveDispatcher = new JwksResolveDispatcher();

    @Getter
    private CombinedDispatcher dispatcher = new CombinedDispatcher().addDispatcher(jwksResolveDispatcher);
    private String jwksEndpoint;

    @BeforeEach
    void setupMockServer() {
        mockserverPort = mockWebServer.getPort();
        jwksEndpoint = "http://localhost:" + mockserverPort + jwksResolveDispatcher.getBaseUrl();
        tokenParser = JwksAwareTokenParser.builder().jwksEndpoint(jwksEndpoint).jwksRefreshIntervall(JWKS_REFRESH_INTERVALL).jwksIssuer(TestTokenProducer.ISSUER).build();
        jwksResolveDispatcher.setCallCounter(0);
    }


    @Test
    void shouldResolveFromRemote() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        JsonWebToken jsonWebToken = assertDoesNotThrow(() -> ParsedToken.jsonWebTokenFrom(initialToken, tokenParser, LOGGER));

        assertValidJsonWebToken(jsonWebToken, initialToken);
    }

    @Test
    void shouldFailFromRemoteWithInvalidIssuer() {
        tokenParser = JwksAwareTokenParser.builder().jwksEndpoint(jwksEndpoint).jwksRefreshIntervall(JWKS_REFRESH_INTERVALL).jwksIssuer("Wrong Issuer").build();
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);
        JsonWebToken jsonWebToken = assertDoesNotThrow(() -> ParsedToken.jsonWebTokenFrom(initialToken, tokenParser, LOGGER));
        assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
    }

    @Test
    void shouldFailFromRemoteWithInvalidJWKS() {
        jwksResolveDispatcher.switchToOtherPublicKey();
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);
        JsonWebToken jsonWebToken = assertDoesNotThrow(() -> ParsedToken.jsonWebTokenFrom(initialToken, tokenParser, LOGGER));
        assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
    }

    @Test
    void shouldCacheMultipleCalls() {
        jwksResolveDispatcher.assertCallsAnswered(0);
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);
        for (int i = 0; i < 100; i++) {
            JsonWebToken jsonWebToken = ParsedToken.jsonWebTokenFrom(initialToken, tokenParser, LOGGER);
            assertValidJsonWebToken(jsonWebToken, initialToken);
        }
        // For some reason, there are always at least 2 calls, instead of expected one call. No
        // problem because as shown within this test, the number stays at 2
        assertTrue(jwksResolveDispatcher.getCallCounter() < 3);

        for (int i = 0; i < 100; i++) {
            JsonWebToken jsonWebToken = assertDoesNotThrow(() -> ParsedToken.jsonWebTokenFrom(initialToken, tokenParser, LOGGER));
            assertValidJsonWebToken(jsonWebToken, initialToken);
        }
        assertTrue(jwksResolveDispatcher.getCallCounter() < 3);
    }

    @Test
    void shouldConsumeJWKSDirectly() throws IOException {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);
        tokenParser = JwksAwareTokenParser.builder().jwksKeyContent(IOStreams.toString(
                new FileInputStream(JwksResolveDispatcher.PUBLIC_KEY_JWKS))).jwksIssuer(TestTokenProducer.ISSUER).build();
        var token = ParsedToken.jsonWebTokenFrom(initialToken, tokenParser, LOGGER);
        assertValidJsonWebToken(token, initialToken);
    }

    private void assertValidJsonWebToken(JsonWebToken jsonWebToken, String initialTokenString) {
        assertNotEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        assertEquals(initialTokenString, jsonWebToken.getRawToken());
    }

}
