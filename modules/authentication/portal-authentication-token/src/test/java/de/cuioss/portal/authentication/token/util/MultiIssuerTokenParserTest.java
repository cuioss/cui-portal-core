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
package de.cuioss.portal.authentication.token.util;

import static de.cuioss.portal.authentication.token.TestTokenProducer.*;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.authentication.token.JwksAwareTokenParser;
import de.cuioss.portal.authentication.token.JwksAwareTokenParserTest;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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

}
