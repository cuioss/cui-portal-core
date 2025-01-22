package de.cuioss.portal.authentication.token;

import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Factory for creating different types of tokens.
 */
@ApplicationScoped
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class TokenFactory {

    private final JWTParser tokenParser;

    /**
     * Creates a new token factory using the given parser.
     *
     * @param tokenParser The parser to use for token validation, must not be null
     * @return A new TokenFactory instance
     */
    public static TokenFactory of(@NonNull JWTParser tokenParser) {
        return new TokenFactory(tokenParser);
    }

    /**
     * Creates an access token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed access token
     */
    public ParsedAccessToken createAccessToken(@NonNull String tokenString) {
        return ParsedAccessToken.fromTokenString(tokenString, tokenParser);
    }

    /**
     * Creates an ID token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed ID token
     */
    public ParsedIdToken createIdToken(@NonNull String tokenString) {
        return ParsedIdToken.fromTokenString(tokenString, tokenParser);
    }

    /**
     * Creates a refresh token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed refresh token
     */
    public ParsedRefreshToken createRefreshToken(@NonNull String tokenString) {
        return ParsedRefreshToken.fromTokenString(tokenString);
    }

}
