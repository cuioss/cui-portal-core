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

import static de.cuioss.portal.authentication.token.TestTokenProducer.DEFAULT_TOKEN_PARSER;
import static de.cuioss.portal.authentication.token.TestTokenProducer.SOME_SCOPES;
import static de.cuioss.portal.authentication.token.TestTokenProducer.validSignedJWTWithClaims;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;

@EnableTestLogger
@DisplayName("Tests ParsedToken functionality")
class ParsedTokenTest {

    private static final CuiLogger LOGGER = new CuiLogger(ParsedTokenTest.class);

    @Nested
    @DisplayName("Token Parsing Error Cases")
    class TokenParsingErrorTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = "  ")
        @DisplayName("Should handle empty or blank token strings")
        void shouldProvideEmptyFallbackOnEmptyInput(String initialTokenString) {
            var jsonWebToken = ParsedToken.jsonWebTokenFrom(initialTokenString,
                    TestTokenProducer.DEFAULT_TOKEN_PARSER, LOGGER);
            assertFalse(jsonWebToken.isPresent(), "Token should not be present for empty input");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                    PortalTokenLogMessages.TOKEN_IS_EMPTY.resolveIdentifierString());
        }

        @Test
        @DisplayName("Should handle invalid token format")
        void shouldProvideEmptyFallbackOnParseError() {
            String initialTokenString = Generators.letterStrings(10, 20).next();

            var jsonWebToken = ParsedToken.jsonWebTokenFrom(initialTokenString,
                    TestTokenProducer.DEFAULT_TOKEN_PARSER, LOGGER);

            assertFalse(jsonWebToken.isPresent(), "Token should not be present for invalid format");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                    PortalTokenLogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
        }

        @Test
        @DisplayName("Should handle invalid issuer")
        void shouldProvideEmptyFallbackOnInvalidIssuer() {
            String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);

            var jsonWebToken = ParsedToken
                    .jsonWebTokenFrom(initialTokenString, TestTokenProducer.WRONG_ISSUER_TOKEN_PARSER, LOGGER);

            assertFalse(jsonWebToken.isPresent(), "Token should not be present for invalid issuer");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                    PortalTokenLogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
        }

        @Test
        @DisplayName("Should handle invalid signature")
        void shouldProvideEmptyFallbackOnWrongPublicKey() {
            String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);

            var jsonWebToken = ParsedToken
                    .jsonWebTokenFrom(initialTokenString,
                            TestTokenProducer.WRONG_SIGNATURE_TOKEN_PARSER, LOGGER);
            assertFalse(jsonWebToken.isPresent(), "Token should not be present for invalid signature");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                    PortalTokenLogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
        }
    }

    @Nested
    @DisplayName("Token Expiration Tests")
    class TokenExpirationTests {

        @Test
        @DisplayName("Should correctly handle token expiration checks")
        void shouldHandleNotExpiredToken() {
            String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

            var token = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);
            assertTrue(token.isPresent(), "Token should be present for valid input");
            assertFalse(token.get().isExpired(), "Token should not be expired");
            assertFalse(token.get().willExpireInSeconds(5), "Token should not expire in 5 seconds");
            assertTrue(token.get().willExpireInSeconds(500), "Token should expire in 500 seconds");
        }
    }
}
