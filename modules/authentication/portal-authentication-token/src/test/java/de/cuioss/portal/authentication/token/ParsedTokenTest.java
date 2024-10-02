package de.cuioss.portal.authentication.token;

import de.cuioss.test.generator.Generators;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.tools.logging.CuiLogger;
import io.smallrye.jwt.auth.principal.DefaultJWTParser;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static de.cuioss.portal.authentication.token.TestTokenProducer.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger
class ParsedTokenTest {

    private static final CuiLogger LOGGER = new CuiLogger(ParsedTokenTest.class);

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "  ")
    void shouldProvideEmptyFallbackOnEmptyInput(String initialTokenString) {
        JsonWebToken jsonWebToken = ParsedToken.jsonWebTokenFrom(initialTokenString, TestTokenProducer.DEFAULT_TOKEN_PARSER, LOGGER);

        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN, LogMessages.TOKEN_IS_EMPTY.resolveIdentifierString());
    }

    @Test
    void shouldProvideEmptyFallbackOnParseError() {
        String initialTokenString = Generators.letterStrings(10, 20).next();

        JsonWebToken jsonWebToken = ParsedToken.jsonWebTokenFrom(initialTokenString, TestTokenProducer.DEFAULT_TOKEN_PARSER, LOGGER);

        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN, LogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
    }

    @Test
    void shouldProvideEmptyFallbackOnInvalidIssuer() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);

        JsonWebToken jsonWebToken = ParsedToken
                .jsonWebTokenFrom(initialTokenString, new DefaultJWTParser(TestTokenProducer.TEST_AUTH_CONTEXT_INFO_WRONG_ISSUER), LOGGER);

        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN, LogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
    }

    @Test
    void shouldProvideEmptyFallbackOnWrongPublicKey() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_SCOPES);

        JsonWebToken jsonWebToken = ParsedToken
                .jsonWebTokenFrom(initialTokenString, new DefaultJWTParser(TestTokenProducer.TEST_AUTH_CONTEXT_INFO_WRONG_PUBLIC_KEY),
                        LOGGER);
        Assertions.assertEquals(ParsedToken.EMPTY_WEB_TOKEN, jsonWebToken);
        LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.WARN, LogMessages.COULD_NOT_PARSE_TOKEN.resolveIdentifierString());
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
