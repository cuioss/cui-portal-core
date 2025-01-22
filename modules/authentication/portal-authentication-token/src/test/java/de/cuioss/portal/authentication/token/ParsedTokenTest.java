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

import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static de.cuioss.portal.authentication.token.TestTokenProducer.DEFAULT_TOKEN_PARSER;
import static de.cuioss.portal.authentication.token.TestTokenProducer.SOME_SCOPES;
import static de.cuioss.portal.authentication.token.TestTokenProducer.validSignedJWTWithClaims;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger
class ParsedTokenTest {

    private static final CuiLogger LOGGER = new CuiLogger(ParsedTokenTest.class);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "  ")
    void shouldProvideEmptyFallbackOnEmptyInput(String initialTokenString) {
        JsonWebToken jsonWebToken = ParsedToken.jsonWebTokenFrom(initialTokenString,
                TestTokenProducer.DEFAULT_TOKEN_PARSER, LOGGER);

        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                LogMessages.TOKEN_IS_EMPTY.resolveIdentifierString());
    }

    @Test
    void shouldProvideEmptyFallbackOnParseError() {
        String initialTokenString = Generators.letterStrings(10, 20).next();

        JsonWebToken jsonWebToken = ParsedToken.jsonWebTokenFrom(initialTokenString,
                TestTokenProducer.DEFAULT_TOKEN_PARSER, LOGGER);

        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                LogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
    }

    @Test
    void shouldProvideEmptyFallbackOnInvalidIssuer() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);

        JsonWebToken jsonWebToken = ParsedToken
                .jsonWebTokenFrom(initialTokenString, TestTokenProducer.WRONG_ISSUER_TOKEN_PARSER, LOGGER);

        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                LogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
    }

    @Test
    void shouldProvideEmptyFallbackOnWrongPublicKey() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);

        JsonWebToken jsonWebToken = ParsedToken
                .jsonWebTokenFrom(initialTokenString,
                        TestTokenProducer.WRONG_SIGNATURE_TOKEN_PARSER, LOGGER);
        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN,
                LogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
    }

    @Test
    void shouldHandleNotExpiredToken() {
        String initialToken = validSignedJWTWithClaims(SOME_SCOPES);

        var token = ParsedAccessToken.fromTokenString(initialToken, DEFAULT_TOKEN_PARSER);

        assertFalse(token.isExpired());
        assertFalse(token.willExpireInSeconds(5));
        assertTrue(token.willExpireInSeconds(500));
    }
}
