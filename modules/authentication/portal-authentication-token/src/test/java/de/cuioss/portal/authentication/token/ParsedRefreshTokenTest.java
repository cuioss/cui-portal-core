package de.cuioss.portal.authentication.token;

import org.junit.jupiter.api.Test;

import static de.cuioss.portal.authentication.token.TestTokenProducer.REFRESH_TOKEN;
import static de.cuioss.portal.authentication.token.TestTokenProducer.validSignedJWTWithClaims;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ParsedRefreshTokenTest {

    @Test
    void shouldHandleHappyCase() {

        String initialToken = validSignedJWTWithClaims(REFRESH_TOKEN);
        var parsedRefreshToken = ParsedRefreshToken.fromTokenString(initialToken);

        assertEquals(initialToken, parsedRefreshToken.getTokenString());
        assertFalse(parsedRefreshToken.isEmpty());

        assertEquals(TokenType.REFRESH_TOKEN, parsedRefreshToken.getType() );
    }
}