package de.cuioss.portal.authentication.token;

import io.smallrye.jwt.auth.principal.JWTParser;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages multiple {@link JwksAwareTokenParser} instances for different issuers.
 * Provides functionality to inspect JWT tokens and determine the appropriate parser
 * based on the issuer.
 *
 * @author Generated
 */
@ToString
@EqualsAndHashCode
public class MultiIssuerTokenParser {

    private final Map<String, JWTParser> issuerToParser;
    private final JwtTokenParser inspectionParser;

    /**
     * Constructor taking a map of issuer URLs to their corresponding parsers.
     *
     * @param issuerToParser Map containing issuer URLs as keys and their corresponding
     *                       {@link JwksAwareTokenParser} instances as values. Must not be null.
     */
    public MultiIssuerTokenParser(@NonNull Map<String, JWTParser> issuerToParser) {
        this.issuerToParser = new HashMap<>(issuerToParser);
        this.inspectionParser = new JwtTokenParser();
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
        return inspectionParser.unsecured(token)
                .map(JsonWebToken::getIssuer);
    }

    /**
     * Retrieves the appropriate {@link JWTParser} for a given issuer.
     *
     * @param issuer the issuer URL to find the parser for
     * @return an Optional containing the parser if found, empty otherwise
     */
    public Optional<JWTParser> getParserForIssuer(@NonNull String issuer) {
        return Optional.ofNullable(issuerToParser.get(issuer));
    }

    /**
     * Retrieves the appropriate {@link JWTParser} for a given token by first extracting
     * its issuer.
     *
     * @param token the JWT token to find the parser for
     * @return an Optional containing the parser if found, empty otherwise
     */
    public Optional<JWTParser> getParserForToken(@NonNull String token) {
        return extractIssuer(token)
                .flatMap(this::getParserForIssuer);
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
