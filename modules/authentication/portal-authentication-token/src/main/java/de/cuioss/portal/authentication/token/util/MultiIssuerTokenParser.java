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
package de.cuioss.portal.authentication.token.util;

import de.cuioss.portal.authentication.token.JwksAwareTokenParser;
import de.cuioss.tools.logging.CuiLogger;
import io.smallrye.jwt.auth.principal.JWTParser;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages multiple JWT token parsers for different token issuers in a multi-tenant environment.
 * This class provides functionality to inspect JWT tokens, determine their issuer, and select
 * the appropriate parser based on the issuer information.
 * <p>
 * Key features:
 * <ul>
 *   <li>Multi-issuer support for token validation</li>
 *   <li>Safe token inspection without signature validation</li>
 *   <li>Dynamic parser selection based on token issuer</li>
 *   <li>Thread-safe implementation</li>
 * </ul>
 * <p>
 * Usage example:
 * <pre>
 * MultiIssuerTokenParser parser = MultiIssuerTokenParser.builder()
 *     .addParser(issuer1Parser)
 *     .addParser(issuer2Parser)
 *     .build();
 * 
 * Optional&lt;JWTParser&gt; selectedParser = parser.getParserForToken(tokenString);
 * </pre>
 * <p>
 * The class uses {@link NonValidatingJwtTokenParser} internally for initial token inspection
 * to determine the issuer before selecting the appropriate validating parser.
 *
 * @author Oliver Wolff
 */
@ToString
@EqualsAndHashCode
public class MultiIssuerTokenParser {

    private static final CuiLogger LOGGER = new CuiLogger(MultiIssuerTokenParser.class);

    private final Map<String, JWTParser> issuerToParser;
    private final NonValidatingJwtTokenParser inspectionParser;

    /**
     * Constructor taking a map of issuer URLs to their corresponding parsers.
     *
     * @param issuerToParser Map containing issuer URLs as keys and their corresponding
     *                       {@link JwksAwareTokenParser} instances as values. Must not be null.
     */
    public MultiIssuerTokenParser(@NonNull Map<String, JWTParser> issuerToParser) {
        this.issuerToParser = new HashMap<>(issuerToParser);
        this.inspectionParser = new NonValidatingJwtTokenParser();
        LOGGER.debug("Initialized MultiIssuerTokenParser with %s parser(s)", issuerToParser.size());
    }

    /**
     * Creates a new builder for {@link MultiIssuerTokenParser}
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Inspects a JWT token to determine its issuer without validating the signature.
     *
     * @param token the JWT token to inspect, must not be null
     * @return the issuer of the token if present
     */
    public Optional<String> extractIssuer(@NonNull String token) {
        LOGGER.debug("Extracting issuer from token");
        var issuer = inspectionParser.unsecured(token)
                .map(JsonWebToken::getIssuer);
        LOGGER.debug("Extracted issuer: %s", issuer.orElse("<none>"));
        return issuer;
    }

    /**
     * Retrieves the appropriate {@link JWTParser} for a given issuer.
     *
     * @param issuer the issuer URL to find the parser for
     * @return an Optional containing the parser if found, empty otherwise
     */
    public Optional<JWTParser> getParserForIssuer(@NonNull String issuer) {
        LOGGER.debug("Looking up parser for issuer: %s", issuer);
        var parser = Optional.ofNullable(issuerToParser.get(issuer));
        if (parser.isEmpty()) {
            LOGGER.debug("No parser found for issuer: %s", issuer);
        }
        return parser;
    }

    /**
     * Retrieves the appropriate {@link JWTParser} for a given token by first extracting
     * its issuer.
     *
     * @param token the JWT token to find the parser for
     * @return an Optional containing the parser if found, empty otherwise
     */
    public Optional<JWTParser> getParserForToken(@NonNull String token) {
        LOGGER.debug("Getting parser for token");
        return extractIssuer(token).flatMap(this::getParserForIssuer);
    }

    /**
     * Builder for {@link MultiIssuerTokenParser}
     */
    public static class Builder {
        private final Map<String, JWTParser> issuerToParser = new HashMap<>();

        /**
         * Adds a parser for a specific issuer
         *
         * @param parser the parser for that issuer
         * @return this builder instance
         */
        public Builder addParser(@NonNull JwksAwareTokenParser parser) {
            LOGGER.debug("Adding parser for issuer: %s", parser.getJwksIssuer());
            issuerToParser.put(parser.getJwksIssuer(), parser);
            return this;
        }

        /**
         * Builds the {@link MultiIssuerTokenParser}
         *
         * @return a new instance of {@link MultiIssuerTokenParser}
         */
        public MultiIssuerTokenParser build() {
            return new MultiIssuerTokenParser(issuerToParser);
        }
    }
}
