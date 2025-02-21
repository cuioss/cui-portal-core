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
package de.cuioss.portal.authentication.oauth;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.facade.AuthenticationFacade;
import de.cuioss.tools.net.UrlParameter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Extends the {@link AuthenticationFacade} to provide OAuth2-specific authentication capabilities.
 * This facade manages the OAuth2 authentication flow, token handling, and user session management.
 *
 * <p>The facade handles:
 * <ul>
 *   <li>OAuth2 authentication flow initiation and completion</li>
 *   <li>Token management (access, refresh, ID tokens)</li>
 *   <li>User session state management</li>
 *   <li>OAuth2 logout procedures</li>
 * </ul>
 *
 * <p>Implementation notes:
 * <ul>
 *   <li>All implementations must be thread-safe</li>
 *   <li>Token storage should follow security best practices</li>
 *   <li>State validation must be enforced during the authentication flow</li>
 * </ul>
 */
public interface Oauth2AuthenticationFacade extends AuthenticationFacade {

    /**
     * Key for storing the authenticated user's email in the
     * {@link AuthenticatedUserInfo#getContextMap()}.
     */
    String EMAIL_KEY = "email";

    /**
     * Prefix for storing additional userinfo response attributes in
     * {@link AuthenticatedUserInfo#getContextMap()}.
     * All attributes from the userinfo endpoint will be prefixed with this value.
     */
    String USERINFO_PREFIX_KEY = "userinfo_";

    /**
     * Initiates the OAuth2 authentication flow by preparing the necessary parameters
     * and redirecting to the authorization endpoint.
     * Uses the currently configured scopes for the authorization request.
     */
    void sendRedirect();

    /**
     * Initiates the OAuth2 authentication flow with specific scopes.
     *
     * @param scopes Space-separated list of OAuth2 scopes to request
     */
    void sendRedirect(String scopes);

    /**
     * Retrieves the OAuth2 redirect URL for the given scopes and optional ID token.
     *
     * @param scopes  The space-separated list of OAuth2 scopes to request
     * @param idToken Optional ID token for authentication continuation
     * @return The complete OAuth2 redirect URL including all necessary parameters
     */
    String retrieveOauth2RedirectUrl(String scopes, String idToken);

    /**
     * Retrieves the URL for renewing the current authentication session.
     * This is typically used for silent token refresh.
     *
     * @return The OAuth2 renewal URL or null if renewal is not possible
     */
    String retrieveOauth2RenewUrl();

    /**
     * Retrieves the interval in milliseconds until the current token expires.
     * Used for proactive token renewal scheduling.
     *
     * @return String representation of milliseconds until token expiration
     */
    String retrieveRenewInterval();

    /**
     * Retrieves a client token for the specified scopes.
     *
     * @param scopes Space-separated list of OAuth2 scopes to request
     * @return The client token or null if not available
     */
    String retrieveClientToken(String scopes);

    /**
     * Retrieves a token for the specified scopes from the current session.
     *
     * @param scopes Space-separated list of OAuth2 scopes to request
     * @return The access token or null if not available
     */
    String retrieveToken(String scopes);

    /**
     * Retrieves a token for the specified scopes from the current session.
     *
     * @param currentUser The authenticated user
     * @param scopes      Space-separated list of OAuth2 scopes to request
     * @return The access token or null if not available
     */
    String retrieveToken(AuthenticatedUserInfo currentUser, String scopes);

    /**
     * Invalidates the current token, forcing a new token retrieval on next request.
     */
    void invalidateToken();

    /**
     * Retrieves the claims from the ID token for the given user.
     *
     * @param user The authenticated user containing the ID token
     * @return Map of claims from the ID token or empty map if not available
     */
    Map<String, Object> retrieveIdToken(AuthenticatedUserInfo user);

    /**
     * Retrieves the OAuth2 logout URL with optional additional parameters.
     *
     * @param additionalParameter Optional set of additional parameters to include in the logout URL
     * @return The complete logout URL
     */
    String retrieveClientLogoutUrl(Set<UrlParameter> additionalParameter);

    /**
     * Refreshes the user info properties from the oauth server
     */
    AuthenticatedUserInfo refreshUserinfo();

    /**
     * the configured login url
     */
    String getLoginUrl();

    /**
     * Check if this request was caused by the oauth2 server after successful
     * authentication and the parameters contain valid authentication data. If so,
     * retrieve the username and subject and create an authenticated
     * {@link AuthenticatedUserInfo} to be stored in the session and returned.
     * Otherwise, an unauthenticated {@link AuthenticatedUserInfo} will be returned.
     *
     * @param parameters
     * @param scopes     the scopes as space separated list
     * @return an {@link AuthenticatedUserInfo} that can be authenticated or
     * unauthenticated.
     */
    AuthenticatedUserInfo testLogin(List<UrlParameter> parameters, final String scopes);
}
