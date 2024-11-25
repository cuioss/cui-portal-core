package de.cuioss.portal.authentication.token;

import de.cuioss.tools.string.MoreStrings;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Variant of {@link ParsedToken} representing a refresh-token
 * <em>Caution:</em> This is only tested for keycloak.
 * The usage of JWTs for a refresh-token is not from the oauth spec.
 *
 * @author Oliver Wolff
 */
@ToString
public class ParsedRefreshToken implements Serializable {

    @Getter
    private final String tokenString;

    private ParsedRefreshToken(String tokenString) {
        this.tokenString = tokenString;
    }

    /**
     * @param tokenString to be passed
     * @return an {@link ParsedRefreshToken} if given Token can be parsed correctly,
     * otherwise {@link ParsedAccessToken#EMPTY_WEB_TOKEN}}
     */
    public static ParsedRefreshToken fromTokenString(String tokenString) {
        return new ParsedRefreshToken(tokenString);
    }

    /**
     * Indicates, whether the token is (not) present
     */
    public boolean isEmpty() {
        return MoreStrings.isEmpty(tokenString);
    }

    /**
     * The type o contained token.
     */
    public TokenType getType() {
        return TokenType.REFRESH_TOKEN;
    }
}
