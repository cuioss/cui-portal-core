package de.cuioss.portal.authentication.token.util;

import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.cuioss.portal.authentication.token.TestTokenProducer.ISSUER;
import static de.cuioss.portal.authentication.token.TestTokenProducer.SOME_NAME;
import static de.cuioss.portal.authentication.token.TestTokenProducer.SOME_SCOPES;
import static de.cuioss.portal.authentication.token.TestTokenProducer.validSignedJWTWithClaims;
import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static de.cuioss.test.juli.LogAsserts.assertNoLogMessagePresent;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger
class NonValidatingJwtTokenParserTest {

    private NonValidatingJwtTokenParser parser;

    @BeforeEach
    void setUp() {
        parser = new NonValidatingJwtTokenParser();
    }

    @Test
    void shouldParseValidToken() {
        var token = validSignedJWTWithClaims(SOME_SCOPES);
        var result = parser.unsecured(token);

        assertTrue(result.isPresent());
        var jwt = result.get();
        assertEquals(ISSUER, jwt.getIssuer());
        assertNotNull(jwt.getSubject());
        assertNotNull(jwt.getTokenID());
        assertTrue(jwt.getExpirationTime() > 0);
        assertTrue(jwt.getIssuedAtTime() > 0);
        assertTrue(jwt.getGroups().isEmpty());
        assertTrue(jwt.getAudience().isEmpty());
        assertNull(jwt.getRawToken());
        assertNoLogMessagePresent(TestLogLevel.WARN, NonValidatingJwtTokenParser.class);
        assertNoLogMessagePresent(TestLogLevel.ERROR, NonValidatingJwtTokenParser.class);
    }


    @Test
    void shouldParseTokenWithName() {
        var token = validSignedJWTWithClaims(SOME_NAME);
        var result = parser.unsecured(token);

        assertTrue(result.isPresent());
        var jwt = result.get();
        assertNotNull(jwt.getName());
        assertNoLogMessagePresent(TestLogLevel.WARN, NonValidatingJwtTokenParser.class);
        assertNoLogMessagePresent(TestLogLevel.ERROR, NonValidatingJwtTokenParser.class);
    }

    @ParameterizedTest(name = "Should handle invalid token format: {0}")
    @CsvSource({
        "not.a.jwt, Failed to parse token",
        "'', Token is empty or null",
        "before.after, Invalid JWT token format: expected 3 parts but got 2",
        "before.after.that.else, Invalid JWT token format: expected 3 parts but got 4",
        "invalid, Invalid JWT token format: expected 3 parts but got 1"
    })
    void shouldHandleInvalidTokenFormat(String invalidToken, String expectedMessage) {
        var result = parser.unsecured(invalidToken);
        assertTrue(result.isEmpty());
        assertLogMessagePresentContaining(TestLogLevel.INFO, expectedMessage);
    }

    @Test
    void shouldHandleNullToken() {
        var result = parser.unsecured(null);
        assertTrue(result.isEmpty());
        assertLogMessagePresentContaining(TestLogLevel.INFO, "Token is empty or null");
    }

    @ParameterizedTest(name = "Should handle oversized token of size {0}KB")
    @ValueSource(ints = {9, 10, 12, 16})
    void shouldRejectOversizedToken(int sizeInKb) {
        String largeToken = createLargeToken(sizeInKb);
        var result = parser.unsecured(largeToken);
        assertTrue(result.isEmpty());
        assertLogMessagePresentContaining(TestLogLevel.WARN, "Token exceeds maximum size limit");
    }

    @ParameterizedTest(name = "Should handle oversized payload of size {0}KB")
    @ValueSource(ints = {17, 20, 24, 32})
    void shouldRejectOversizedPayload(int sizeInKb) {
        String tokenWithLargePayload = createTokenWithLargePayload(sizeInKb);
        var result = parser.unsecured(tokenWithLargePayload);
        assertTrue(result.isEmpty());
        assertLogMessagePresentContaining(TestLogLevel.WARN, "Token exceeds maximum size limit");
    }

    private String createLargeToken(int sizeInKb) {
        String repeatedChar = IntStream.range(0, sizeInKb * 1024)
                .mapToObj(i -> "a")
                .collect(Collectors.joining());
        return repeatedChar + "." + repeatedChar + "." + repeatedChar;
    }

    private String createTokenWithLargePayload(int sizeInKb) {
        // Create a valid header
        String header = Base64.getUrlEncoder().encodeToString(
                "{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));

        // Create a large payload
        String largeJson = "{\"data\":\"" +
                IntStream.range(0, sizeInKb * 1024)
                        .mapToObj(i -> "a")
                        .collect(Collectors.joining()) +
                "\"}";
        String payload = Base64.getUrlEncoder().encodeToString(
                largeJson.getBytes(StandardCharsets.UTF_8));

        // Add a dummy signature
        String signature = Base64.getUrlEncoder().encodeToString("signature".getBytes(StandardCharsets.UTF_8));

        return String.join(".", header, payload, signature);
    }
}
