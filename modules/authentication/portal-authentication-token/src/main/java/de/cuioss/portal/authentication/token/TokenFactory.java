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

import de.cuioss.portal.authentication.token.util.MultiIssuerTokenParser;
import de.cuioss.tools.base.Preconditions;
import de.cuioss.tools.logging.CuiLogger;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Factory for creating different types of tokens with support for multiple issuers.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenFactory {

    private static final CuiLogger LOGGER = new CuiLogger(TokenFactory.class);

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
        var factory = new TokenFactory(builder.build());
        LOGGER.debug("Created TokenFactory with %s parser(s)", tokenParser.length);
        return factory;
    }

    /**
     * Creates an access token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed access token, which may be empty if the token is invalid or no parser is found
     */
    public Optional<ParsedAccessToken> createAccessToken(@NonNull String tokenString) {
        LOGGER.debug("Creating access token");
        var parser = tokenParser.getParserForToken(tokenString);
        if (parser.isPresent()) {
            LOGGER.debug("Found parser for token, attempting to create access token");
            return ParsedAccessToken.fromTokenString(tokenString, parser.get());
        }
        LOGGER.debug("No suitable parser found for token");
        return Optional.empty();
    }

    /**
     * Creates an ID token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed ID token, which may be empty if the token is invalid or no parser is found
     */
    public Optional<ParsedIdToken> createIdToken(@NonNull String tokenString) {
        LOGGER.debug("Creating ID token");
        var parser = tokenParser.getParserForToken(tokenString);
        if (parser.isPresent()) {
            LOGGER.debug("Found parser for token, attempting to create ID token");
            return ParsedIdToken.fromTokenString(tokenString, parser.get());
        }
        LOGGER.debug("No suitable parser found for token");
        return Optional.empty();
    }

    /**
     * Creates a refresh token from the given token string.
     *
     * @param tokenString The token string to parse, must not be null
     * @return The parsed refresh token, which may be empty if the token is invalid or no parser is found
     */
    public Optional<ParsedRefreshToken> createRefreshToken(@NonNull String tokenString) {
        LOGGER.debug("Creating refresh token");
        return tokenParser.getParserForToken(tokenString)
                .map(parser -> {
                    LOGGER.debug("Found parser for token, creating refresh token");
                    return ParsedRefreshToken.fromTokenString(tokenString);
                });
    }
}
