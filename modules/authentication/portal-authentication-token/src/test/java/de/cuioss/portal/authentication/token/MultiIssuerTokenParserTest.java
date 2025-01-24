package de.cuioss.portal.authentication.token;

import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.cuioss.portal.authentication.token.TestTokenProducer.ISSUER;
import static de.cuioss.portal.authentication.token.TestTokenProducer.SOME_SCOPES;
import static de.cuioss.portal.authentication.token.TestTokenProducer.validSignedJWTWithClaims;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnableTestLogger
class MultiIssuerTokenParserTest {

    private MultiIssuerTokenParser multiIssuerParser;
    private JwksAwareTokenParser defaultParser;

    @BeforeEach
    void setUp() throws IOException {
        defaultParser = JwksAwareTokenParserTest.getValidJWKSParserWithLocalJWKS();
        JwksAwareTokenParser otherParser = JwksAwareTokenParserTest.getInvalidValidJWKSParserWithLocalJWKSAndWrongIssuer();
        
        multiIssuerParser = MultiIssuerTokenParser.builder()
                .addParser(defaultParser)
                .addParser(otherParser)
                .build();
    }

    @Test
    void shouldExtractIssuerFromValidToken() {
        var token = validSignedJWTWithClaims(SOME_SCOPES);
        var extractedIssuer = multiIssuerParser.extractIssuer(token);
        
        assertTrue(extractedIssuer.isPresent());
        assertEquals(ISSUER, extractedIssuer.get());
    }

    @Test
    void shouldHandleInvalidTokenForIssuerExtraction() {
        var extractedIssuer = multiIssuerParser.extractIssuer("invalid-token");
        assertFalse(extractedIssuer.isPresent());
    }

    @Test
    void shouldGetParserForKnownIssuer() {
        var parser = multiIssuerParser.getParserForIssuer(ISSUER);
        
        assertTrue(parser.isPresent());
        assertEquals(defaultParser, parser.get());
    }

    @Test
    void shouldReturnEmptyForUnknownIssuer() {
        var parser = multiIssuerParser.getParserForIssuer("unknown-issuer");
        assertFalse(parser.isPresent());
    }

    @Test
    void shouldGetParserForValidToken() {
        var token = validSignedJWTWithClaims(SOME_SCOPES);
        var parser = multiIssuerParser.getParserForToken(token);
        
        assertTrue(parser.isPresent());
        assertEquals(defaultParser, parser.get());
    }

    @Test
    void shouldHandleInvalidTokenForParserRetrieval() {
        var parser = multiIssuerParser.getParserForToken("invalid-token");
        assertFalse(parser.isPresent());
    }

    @Test
    void shouldHandleUnknownIssuerInToken() {
        var parser = multiIssuerParser.getParserForIssuer("unknown-issuer");
        assertFalse(parser.isPresent());
    }
}
