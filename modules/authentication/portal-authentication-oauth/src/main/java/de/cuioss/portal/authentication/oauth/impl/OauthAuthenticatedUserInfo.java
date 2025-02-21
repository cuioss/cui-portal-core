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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.Token;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.io.Serial;
import java.util.Optional;

/**
 * OAuth2-specific implementation of {@link AuthenticatedUserInfo}.
 * This class extends the standard authenticated user information with
 * OAuth2-specific data and functionality.
 *
 * <p>Additional OAuth2 context data includes:
 * <ul>
 *   <li>Access Token - Stored under key 'token'</li>
 *   <li>Token Scopes - Stored under key 'tokenScopes'</li>
 *   <li>Token Timestamp - Stored under key 'tokenTimestamp'</li>
 * </ul>
 *
 * <p>This implementation uses the decorator pattern to wrap a standard
 * {@link AuthenticatedUserInfo} instance while adding OAuth2-specific
 * functionality. All standard user info methods are delegated to the
 * wrapped instance.
 *
 * <p>Implementation notes:
 * <ul>
 *   <li>Thread-safe and immutable</li>
 *   <li>Serializable for session storage</li>
 *   <li>Null-safe factory method provided via {@link #createOf}</li>
 * </ul>
 *
 * @see AuthenticatedUserInfo
 * @see Token
 */
@RequiredArgsConstructor
public class OauthAuthenticatedUserInfo implements AuthenticatedUserInfo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Key for storing the OAuth2 access token in the user context map.
     *
     * @see AuthenticatedUserInfo#getContextMap()
     */
    static final String TOKEN_KEY = "token";

    /**
     * Key for storing the OAuth2 token scopes in the user context map.
     */
    static final String TOKEN_SCOPES_KEY = "tokenScopes";

    /**
     * Key for storing the OAuth2 token timestamp in the user context map.
     */
    static final String TOKEN_TIMESTAMP_KEY = "tokenTimestamp";

    @Delegate
    private final AuthenticatedUserInfo wrapped;

    /**
     * Creates a new OAuth2 authenticated user info instance.
     *
     * @param wrapped The base authenticated user info to wrap
     * @return A new instance or null if the input is null
     */
    public static OauthAuthenticatedUserInfo createOf(AuthenticatedUserInfo wrapped) {
        if (null == wrapped) {
            return null;
        }
        return new OauthAuthenticatedUserInfo(wrapped);
    }

    /**
     * Retrieves the OAuth2 token associated with this user.
     *
     * @return The OAuth2 token stored in the context map
     */
    public Token getToken() {
        return (Token) wrapped.getContextMap().get(TOKEN_KEY);
    }

    /**
     * Retrieves the OAuth2 token scopes associated with this user.
     *
     * @return The OAuth2 token scopes stored in the context map
     */
    public String getScopes() {
        return (String) wrapped.getContextMap().get(TOKEN_SCOPES_KEY);
    }

    /**
     * Retrieves the OAuth2 token timestamp associated with this user.
     *
     * @return The OAuth2 token timestamp stored in the context map
     */
    public int getTokenTimestamp() {
        return (Integer) wrapped.getContextMap().get(TOKEN_TIMESTAMP_KEY);
    }

    /**
     * Retrieves the id-token associated with this user.
     *
     * @return The id-token stored in the context map
     */
    public Optional<String> getIdToken() {
        var token = getToken();
        if (null != token) {
            return Optional.ofNullable(token.getId_token());
        }
        return Optional.empty();
    }
}
