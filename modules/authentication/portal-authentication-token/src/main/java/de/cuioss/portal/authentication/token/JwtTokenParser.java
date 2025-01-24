package de.cuioss.portal.authentication.token;

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
import java.util.HashSet;
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
public class JwtTokenParser {

    private static final CuiLogger LOGGER = new CuiLogger(JwtTokenParser.class);

    /**
     * Maximum size of a JWT token in bytes to prevent overflow attacks.
     * 8KB should be more than enough for any reasonable JWT token.
     */
    private static final int MAX_TOKEN_SIZE = 8 * 1024;

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
            LOGGER.info("Token is empty or null");
            return Optional.empty();
        }

        if (token.getBytes(StandardCharsets.UTF_8).length > MAX_TOKEN_SIZE) {
            LOGGER.warn("Token exceeds maximum size limit of %s bytes", MAX_TOKEN_SIZE);
            return Optional.empty();
        }
        var parts = Splitter.on('.').splitToList(token);
        if (parts.size() != 3) {
            LOGGER.info("Invalid JWT token format: expected 3 parts but got %s", parts.size());
            return Optional.empty();
        }

        try {
            JsonObject claims = parsePayload(parts.get(1));
            return Optional.of(new UnsecuredJsonWebToken(claims));
        } catch (Exception e) {
            LOGGER.info(e, "Failed to parse token: %s", e.getMessage());
            LOGGER.debug(e, "Detailed parse error");
            return Optional.empty();
        }
    }

    private JsonObject parsePayload(String payload) {
        byte[] decoded = Base64.getUrlDecoder().decode(payload);

        if (decoded.length > MAX_PAYLOAD_SIZE) {
            LOGGER.info("Decoded payload exceeds maximum size limit of %s bytes", MAX_PAYLOAD_SIZE);
            throw new IllegalStateException("Decoded payload exceeds maximum size limit");
        }

        try (var reader = Json.createReader(new StringReader(new String(decoded, StandardCharsets.UTF_8)))) {
            return reader.readObject();
        }
    }

    /**
     * Simple implementation of JsonWebToken that holds claims without validation.
     */
    private static class UnsecuredJsonWebToken implements JsonWebToken {
        private final JsonObject claims;

        UnsecuredJsonWebToken(JsonObject claims) {
            this.claims = claims;
        }

        @Override
        public String getName() {
            return getStringClaim("name");
        }

        @Override
        public Set<String> getClaimNames() {
            return claims.keySet();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getClaim(String claimName) {
            JsonValue value = claims.get(claimName);
            if (value == null) {
                return null;
            }

            return (T) switch (value.getValueType()) {
                case STRING -> ((JsonString) value).getString();
                case NUMBER -> claims.getJsonNumber(claimName).longValue();
                case ARRAY -> {
                    Set<String> result = new HashSet<>();
                    claims.getJsonArray(claimName).forEach(item -> {
                        if (item instanceof JsonString) {
                            result.add(((JsonString) item).getString());
                        }
                    });
                    yield result;
                }
                default -> null;
            };
        }

        private String getStringClaim(String name) {
            if (!claims.containsKey(name)) {
                return null;
            }
            JsonValue value = claims.get(name);
            return value.getValueType() == JsonValue.ValueType.STRING
                    ? ((JsonString) value).getString()
                    : null;
        }

        @Override
        public String getRawToken() {
            return null; // Not needed for inspection
        }

        @Override
        public String getIssuer() {
            return getStringClaim("iss");
        }

        @Override
        public String getSubject() {
            return getStringClaim("sub");
        }

        @Override
        public Set<String> getAudience() {
            return Collections.emptySet(); // Not needed for inspection
        }

        @Override
        public long getExpirationTime() {
            return claims.containsKey("exp") ? claims.getJsonNumber("exp").longValue() : 0;
        }

        @Override
        public long getIssuedAtTime() {
            return claims.containsKey("iat") ? claims.getJsonNumber("iat").longValue() : 0;
        }

        @Override
        public String getTokenID() {
            return getStringClaim("jti");
        }

        @Override
        public Set<String> getGroups() {
            return Set.of(); // Not needed for inspection
        }
    }
}
