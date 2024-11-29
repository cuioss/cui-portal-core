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
import de.cuioss.tools.string.Splitter;
import io.smallrye.jwt.auth.principal.JWTParser;
import jakarta.json.JsonArray;
import jakarta.json.JsonString;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static java.util.stream.Collectors.toSet;

/**
 * Represents an Access Token with corresponding information. In essence, it is a convenience type
 * for accessing concrete instances of {@link JsonWebToken}
 *
 * @author Oliver Wolff
 */
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class ParsedAccessToken extends ParsedToken {

    private static final CuiLogger LOGGER = new CuiLogger(ParsedAccessToken.class);

    /**
     * The name for the scopes-claim.
     */
    public static final String CLAIM_NAME_SCOPE = "scope";
    private static final String CLAIM_NAME_NAME = "name";
    private static final String CLAIM_NAME_ROLES = "roles";

    /**
     * @param tokenString to be parsed
     * @param tokenParser the actual parser to be used
     * @return an {@link ParsedAccessToken} if given Token can be parsed correctly,
     * otherwise {@link ParsedAccessToken#EMPTY_WEB_TOKEN}
     */
    public static ParsedAccessToken fromTokenString(String tokenString, @NonNull JWTParser tokenParser) {
        return fromTokenString(tokenString, null, tokenParser);
    }

    /**
     * @param tokenString to be parsed
     * @param email       email address or null
     * @param tokenParser the actual parser to be used
     * @return an {@link ParsedAccessToken} if given Token can be parsed correctly,
     * {@code Optional#empty()} otherwise.
     */
    public static ParsedAccessToken fromTokenString(String tokenString, String email, JWTParser tokenParser) {
        return new ParsedAccessToken(jsonWebTokenFrom(tokenString, tokenParser, LOGGER), email);
    }

    private ParsedAccessToken(JsonWebToken jsonWebToken, String email) {
        super(jsonWebToken);
        this.email = email;
    }

    @EqualsAndHashCode.Include
    private final String email;

    /**
     * @return a {@link Set} representing all scopes. If none can be found, it returns an empty set
     */
    public Set<String> getScopes() {
        if (!jsonWebToken.containsClaim(CLAIM_NAME_SCOPE)) {
            LOGGER.debug("No Scopes available");
            return Set.of();
        }

        var result = Splitter.on(' ').splitToList(jsonWebToken.getClaim(CLAIM_NAME_SCOPE));
        LOGGER.debug("Extracted scopes: '%s'", result);
        return new TreeSet<>(result);
    }

    /**
     * @param expectedScopes to be checked
     * @return boolean indicating whether the token provides all given Scopes
     */
    public boolean providesScopes(Collection<String> expectedScopes) {
        return getScopes().containsAll(expectedScopes);
    }

    /**
     * @param expectedScopes to be checked
     * @param logContext     Usually
     * @return boolean indicating whether the token provides all given Scopes. In contrast to
     * {@link #providesScopes(Collection)} it log on debug the corresponding scopes
     */
    public boolean providesScopesAndDebugIfScopesAreMissing(Collection<String> expectedScopes, String logContext,
                                                            CuiLogger logger) {
        Set<String> delta = determineMissingScopes(expectedScopes);
        if (delta.isEmpty()) {
            logger.trace("All expected scopes are present: {}, {}", expectedScopes, logContext);
            return true;
        }
        logger.debug(
                "Current Token does not provide all needed scopes:\nMissing in token='{}',\nExpected='{}'\nPresent in Token='{}', {}",
                delta, expectedScopes, getScopes(), logContext);
        return false;
    }

    /**
     * @param expectedScopes to be checked
     * @return an empty-Set in case the token provides all expectedScopes, otherwise a
     * {@link TreeSet} containing all missing scopes.
     */
    public Set<String> determineMissingScopes(Collection<String> expectedScopes) {
        if (providesScopes(expectedScopes)) {
            return Collections.emptySet();
        }
        Set<String> scopeDelta = new TreeSet<>(expectedScopes);
        scopeDelta.removeAll(getScopes());
        return scopeDelta;
    }

    /**
     * @return the roles defined in the 'roles' claim of the token
     */
    public Set<String> getRoles() {
        if (!jsonWebToken.containsClaim(CLAIM_NAME_ROLES)) {
            return Set.of();
        }

        return jsonWebToken.<JsonArray>getClaim(CLAIM_NAME_ROLES)
                .getValuesAs(JsonString.class)
                .stream()
                .map(JsonString::getString).collect(toSet());
    }

    /**
     * Checks if the expected role is present within the 'roles' claim of the token.
     *
     * @param expectedRole the expected role
     * @return if the role is present
     */
    public boolean hasRole(String expectedRole) {
        return getRoles().contains(expectedRole);
    }

    /**
     * @return the subject id from the underlying token
     */
    public String getSubjectId() {
        return jsonWebToken.getSubject();
    }

    /**
     * Resolves the email address. Either given or extracted from the token.
     *
     * @return an optional containing the potential email
     */
    public Optional<String> getEmail() {
        return Optional
                .ofNullable(email)
                .or(() -> Optional.ofNullable(jsonWebToken.getClaim(Claims.email)));
    }

    /**
     * Resolves the name from the token.
     *
     * @return an optional containing the potential name
     */
    public Optional<String> getName() {
        return Optional.ofNullable(jsonWebToken.getClaim(CLAIM_NAME_NAME));
    }


    /**
     * Resolves the preferred username from the token.
     *
     * @return an optional containing the potential preferred username
     */
    public Optional<String> getPreferredUsername() {
        return Optional.ofNullable(jsonWebToken.getClaim(Claims.preferred_username));
    }

}
