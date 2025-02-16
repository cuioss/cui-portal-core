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
     * otherwise {@link Optional#empty()}
     */
    public static Optional<ParsedIdToken> fromTokenString(String tokenString, JWTParser tokenParser) {
        LOGGER.debug("Creating ID token from token string");
        Optional<JsonWebToken> rawToken = jsonWebTokenFrom(tokenString, tokenParser, LOGGER);
        if (rawToken.isEmpty()) {
            LOGGER.debug("Failed to create ID token from string");
            return Optional.empty();
        }
        LOGGER.debug("Successfully created ID token");
        return rawToken.map(ParsedIdToken::new);
    }

    /**
     * Resolves the email from the token. Only available, if the current token is an ID token.
     *
     * @return email if present
     */
    public Optional<String> getEmail() {
        LOGGER.debug("Retrieving email from ID token");
        Optional<String> email = jsonWebToken.claim("email");
        if (email.isEmpty()) {
            LOGGER.debug("No email claim found in ID token");
        } else {
            LOGGER.debug("Found email in ID token: %s", email.get());
        }
        return email;
    }
}
