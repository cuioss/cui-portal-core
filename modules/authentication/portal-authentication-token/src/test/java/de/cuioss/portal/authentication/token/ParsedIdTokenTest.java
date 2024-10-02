package de.cuioss.portal.authentication.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParsedIdTokenTest {

    @Test
    void shouldHandleValidToken() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_ID_TOKEN);

        ParsedIdToken parsedIdToken = ParsedIdToken.fromTokenString(initialTokenString, TestTokenProducer.DEFAULT_TOKEN_PARSER);
        assertEquals(parsedIdToken.getTokenString(), initialTokenString);
    }

    @Test
    void shouldHandleEmail() {
        String initialTokenString = TestTokenProducer.validSignedJWTWithClaims(TestTokenProducer.SOME_ID_TOKEN);

        ParsedIdToken parsedIdToken = ParsedIdToken.fromTokenString(initialTokenString, TestTokenProducer.DEFAULT_TOKEN_PARSER);
        assertEquals("hello@world.com", parsedIdToken.getEmail().get());
    }

}
