package de.cuioss.portal.authentication.token;

import de.cuioss.tools.logging.CuiLogger;
import io.smallrye.jwt.auth.principal.JWTParser;
import lombok.ToString;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;

/**
 * Variant of {@link ParsedToken} representing an id-token
 *
 * @author Oliver Wolff
 */
@ToString
public class ParsedIdToken extends ParsedToken {

    private static final CuiLogger LOGGER = new CuiLogger(ParsedIdToken.class);

    private ParsedIdToken(JsonWebToken jsonWebToken) {
        super(jsonWebToken);
    }

    /**
     * @param tokenString to be passed
     * @param tokenParser to be passed
     * @return an {@link ParsedIdToken} if given Token can be parsed correctly,
     * otherwise {@link ParsedAccessToken#EMPTY_WEB_TOKEN}}
     */
    public static ParsedIdToken fromTokenString(String tokenString, JWTParser tokenParser) {
        return new ParsedIdToken(jsonWebTokenFrom(tokenString, tokenParser, LOGGER));
    }

    /**
     * Resolves the email from the token. Only available, if the current token is an ID token.
     *
     * @return email if present
     */
    public Optional<String> getEmail() {
        return jsonWebToken.claim("email");
    }

}
