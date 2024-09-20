package de.cuioss.portal.authentication.token;

import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Set;

import static de.cuioss.tools.string.MoreStrings.trimOrNull;

/**
 * Wrapper around {@link JsonWebToken}
 * 
 * @author Oliver Wolff
 *
 */
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class ParsedToken {

    protected static final String EMPTY_NAME = "EMPTY";

    /** <code>null</code> token. */
    public static final JsonWebToken EMPTY_WEB_TOKEN = new JsonWebToken() {

        @Override
        public String getName() {
            return EMPTY_NAME;
        }

        @Override
        public Set<String> getClaimNames() {
            return Set.of();
        }

        @Override
        public <T> T getClaim(String claimName) {
            return null;
        }
    };

    /**
     * @return true, if the token could not be parsed.
     */
    public boolean isEmpty() {
        return EMPTY_NAME.equals(jsonWebToken.getName());
    }

    /**
     * @return the token as encoded String.
     */
    public String getTokenString() {
        return jsonWebToken.getRawToken();
    }

    protected static JsonWebToken jsonWebTokenFrom(String tokenString, JWTParser tokenParser, CuiLogger logger) {
        logger.trace("Parsing token '%s'", tokenString);
        if (MoreStrings.isEmpty(trimOrNull(tokenString))) {
            logger.warn(LogMessages.TOKEN_IS_EMPTY.format());
            return EMPTY_WEB_TOKEN;
        }
        try {
            return tokenParser.parse(tokenString);
        } catch (ParseException e) {
            logger.warn(e, LogMessages.COULD_NOT_PARSE_TOKEN.format());
            logger.trace(() -> LogMessages.COULD_NOT_PARSE_TOKEN_TRACE.format(tokenString));
            return EMPTY_WEB_TOKEN;
        }
    }

    @Getter
    @EqualsAndHashCode.Include
    protected final JsonWebToken jsonWebToken;

    /**
     * @return boolean indicating whether the token is already expired. Shorthand for
     * {@link #willExpireInSeconds(int)}
     * with '0'.
     */
    public boolean isExpired() {
        return willExpireInSeconds(0);
    }

    public boolean isValid() {
        return !(isEmpty() || isExpired());
    }

    /**
     * @param seconds maybe {@code 0}. Calling it with a negative number is not defined.
     * @return boolean indicating whether the token will expired within the given number of seconds.
     */
    public boolean willExpireInSeconds(int seconds) {
        return OffsetDateTime.now().plusSeconds(seconds).isAfter(getExpirationTime());
    }

    /**
     * @return {@link OffsetDateTime} representation of the expiration-Time
     */
    public OffsetDateTime getExpirationTime() {
        return OffsetDateTime
                .ofInstant(Instant.ofEpochSecond(jsonWebToken.getExpirationTime()), ZoneId.systemDefault());
    }
}
