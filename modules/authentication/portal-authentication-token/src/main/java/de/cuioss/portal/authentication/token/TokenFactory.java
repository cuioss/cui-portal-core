package de.cuioss.portal.authentication.token;

import de.cuioss.tools.base.Preconditions;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Factory for creating different types of tokens with support for multiple issuers.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class TokenFactory {

    private final MultiIssuerTokenParser tokenParser;

    /**
     * Creates a new token factory using the given parser.
     *
     * @param tokenParser The parser to use for token validation, must not be null
     * @return A new TokenFactory instance
     */
    public static TokenFactory of(@NonNull JwksAwareTokenParser... tokenParser) {

        Preconditions.checkArgument(tokenParser.length > 0, "tokenParser must be set");
        var builder = MultiIssuerTokenParser.builder();
        for (JwksAwareTokenParser jwksAwareTokenParser : tokenParser) {
            builder.addParser(jwksAwareTokenParser);
        }
        return new TokenFactory(builder.build());
    }

    /**
     * Creates an access token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed access token, which may be empty if the token is invalid or no parser is found
     */
    public Optional<ParsedAccessToken> createAccessToken(@NonNull String tokenString) {
        var parser = tokenParser.getParserForToken(tokenString);
        if (parser.isPresent()) {
            return ParsedAccessToken.fromTokenString(tokenString, parser.get());
        }
        return Optional.empty();
    }

    /**
     * Creates an ID token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed ID token, which may be empty if the token is invalid or no parser is found
     */
    public Optional<ParsedIdToken> createIdToken(@NonNull String tokenString) {
        var parser = tokenParser.getParserForToken(tokenString);
        if (parser.isPresent()) {
            return ParsedIdToken.fromTokenString(tokenString, parser.get());
        }
        return Optional.empty();
    }

    /**
     * Creates a refresh token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed refresh token, which may be empty if the token is invalid or no parser is found
     */
    public Optional<ParsedRefreshToken> createRefreshToken(@NonNull String tokenString) {

        return tokenParser.getParserForToken(tokenString)
                .map(parser -> ParsedRefreshToken.fromTokenString(tokenString));
    }
}
