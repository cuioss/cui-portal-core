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
