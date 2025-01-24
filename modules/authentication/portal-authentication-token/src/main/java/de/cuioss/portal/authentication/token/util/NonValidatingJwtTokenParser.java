package de.cuioss.portal.authentication.token.util;

import de.cuioss.portal.authentication.token.LogMessages;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import de.cuioss.tools.string.Splitter;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * A simplified JWT parser that can extract claims from a token without validating
 * its signature. This is useful for inspecting token content, like the issuer,
 * before deciding which actual validator to use.
 *
 * <p>Security considerations:
 * - Implements size checks to prevent overflow attacks
 * - Uses standard Java Base64 decoder
 * - Does not validate signatures, only for inspection
 *
 * @author Generated
 */
@ToString
@EqualsAndHashCode
public class NonValidatingJwtTokenParser {

    private static final CuiLogger LOGGER = new CuiLogger(NonValidatingJwtTokenParser.class);

    /**
     * Maximum size of a JWT token in bytes to prevent overflow attacks.
     * 16KB should be more than enough for any reasonable JWT token.
     */
    private static final int MAX_TOKEN_SIZE = 16 * 1024;

    /**
     * Maximum size of decoded JSON payload in bytes.
     * 16KB should be more than enough for any reasonable JWT claims.
     */
    private static final int MAX_PAYLOAD_SIZE = 16 * 1024;

    /**
     * Parses a JWT token without validating its signature and returns a JsonWebToken.
     *
     * @param token the JWT token string to parse
     * @return an Optional containing the JsonWebToken if parsing is successful,
     * or empty if the token is invalid or cannot be parsed
     */
    public Optional<JsonWebToken> unsecured(String token) {
        if (MoreStrings.isEmpty(token)) {
            LOGGER.info(LogMessages.TOKEN_EMPTY.format());
            return Optional.empty();
        }

        if (token.getBytes(StandardCharsets.UTF_8).length > MAX_TOKEN_SIZE) {
            LOGGER.warn(LogMessages.TOKEN_SIZE_EXCEEDED.format(MAX_TOKEN_SIZE));
            return Optional.empty();
        }
        var parts = Splitter.on('.').splitToList(token);
        if (parts.size() != 3) {
            LOGGER.info(LogMessages.INVALID_TOKEN_FORMAT.format(parts.size()));
            return Optional.empty();
        }

        try {
            JsonObject claims = parsePayload(parts.get(1));
            return Optional.of(new NotValidatedJsonWebToken(claims));
        } catch (Exception e) {
            LOGGER.info(e, LogMessages.TOKEN_PARSE_FAILED.format(e.getMessage()));
            LOGGER.debug(e, "Detailed parse error");
            return Optional.empty();
        }
    }

    private JsonObject parsePayload(String payload) {
        byte[] decoded = Base64.getUrlDecoder().decode(payload);

        if (decoded.length > MAX_PAYLOAD_SIZE) {
            LOGGER.info(LogMessages.PAYLOAD_SIZE_EXCEEDED.format(MAX_PAYLOAD_SIZE));
            throw new IllegalStateException("Decoded payload exceeds maximum size limit");
        }

        try (var reader = Json.createReader(new StringReader(new String(decoded, StandardCharsets.UTF_8)))) {
            return reader.readObject();
        }
    }

    /**
     * Simple implementation of JsonWebToken that holds claims without validation.
     */
    private static class NotValidatedJsonWebToken implements JsonWebToken {
        private final JsonObject claims;

        NotValidatedJsonWebToken(JsonObject claims) {
            this.claims = claims;
        }

        @Override
        public String getName() {
            return getClaim("name");
        }

        @Override
        public Set<String> getClaimNames() {
            return claims.keySet();
        }

        @Override
        public <T> T getClaim(String claimName) {
            JsonValue value = claims.get(claimName);
            if (value == null) {
                return null;
            }

            return (T) switch (value.getValueType()) {
                case STRING -> ((JsonString) value).getString();
                case NUMBER -> claims.getJsonNumber(claimName).longValue();
                default -> null;
            };
        }

        @Override
        public String getRawToken() {
            return null; // Not needed for inspection
        }

        @Override
        public String getIssuer() {
            return getClaim("iss");
        }

        @Override
        public String getSubject() {
            return getClaim("sub");
        }

        @Override
        public Set<String> getAudience() {
            return Collections.emptySet(); // Not needed for inspection
        }

        @Override
        public long getExpirationTime() {
            Long exp = getClaim("exp");
            if (exp == null) {
                return 0;
            }
            return exp;
        }

        @Override
        public long getIssuedAtTime() {
            Long iat = getClaim("iat");
            if (iat == null) {
                return 0;
            }
            return iat;
        }

        @Override
        public String getTokenID() {
            return getClaim("jti");
        }

        @Override
        public Set<String> getGroups() {
            return Set.of(); // Not needed for inspection
        }
    }
}
