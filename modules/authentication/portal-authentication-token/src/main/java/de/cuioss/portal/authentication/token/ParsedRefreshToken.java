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
package de.cuioss.portal.authentication.token;

import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * Variant of {@link ParsedToken} representing a refresh-token.
 * <p>
 * <em>Caution:</em> This is only tested for keycloak.
 * The usage of JWTs for a refresh-token is not from the oauth spec.
 * <p>
 * This class provides a simple wrapper around refresh tokens with basic validation
 * and type information.
 * It is immutable and thread-safe.
 *
 * @author Oliver Wolff
 */
@ToString
public class ParsedRefreshToken implements Serializable {

    private static final CuiLogger LOGGER = new CuiLogger(ParsedRefreshToken.class);

    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final String tokenString;

    private ParsedRefreshToken(String tokenString) {
        this.tokenString = tokenString;
    }

    /**
     * Creates a new {@link ParsedRefreshToken} from the given token string.
     * <p>
     * Note: This method does not validate the token's signature or format.
     * It only wraps the string for type-safety purposes.
     *
     * @param tokenString The raw refresh token string, may be null or empty
     * @return a new {@link ParsedRefreshToken} instance wrapping the given token
     */
    public static ParsedRefreshToken fromTokenString(String tokenString) {
        if (MoreStrings.isEmpty(tokenString)) {
            LOGGER.debug("Creating empty refresh token");
        }
        return new ParsedRefreshToken(tokenString);
    }

    /**
     * Indicates whether the token is empty (null or blank string).
     *
     * @return {@code true} if the token is null or empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return MoreStrings.isEmpty(tokenString);
    }

    /**
     * Returns the type of this token.
     *
     * @return always {@link TokenType#REFRESH_TOKEN}
     */
    public TokenType getType() {
        return TokenType.REFRESH_TOKEN;
    }
}
