package de.cuioss.portal.authentication.token;

import de.cuioss.test.generator.Generators;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenTypeTest {

    @Test
    void shouldHandleHappyCase() {
        for (TokenType tokenType : TokenType.values()) {
            assertEquals(tokenType, TokenType.fromTypClaim(tokenType.getTypeClaimName()));
        }
    }

    @Test
    void shouldDefaultToUnknown() {
        assertEquals(TokenType.UNKNOWN, TokenType.fromTypClaim(""));
        assertEquals(TokenType.UNKNOWN, TokenType.fromTypClaim(null));
        assertEquals(TokenType.UNKNOWN, TokenType.fromTypClaim(Generators.letterStrings().next()));
    }
}