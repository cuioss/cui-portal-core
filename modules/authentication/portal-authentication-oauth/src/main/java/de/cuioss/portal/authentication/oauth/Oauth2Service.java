/*
 * Copyright © 2025 CUI-OpenSource-Software (info@cuioss.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.authentication.oauth;

import de.cuioss.portal.authentication.AuthenticatedUserInfo;
import de.cuioss.portal.authentication.oauth.impl.OauthAuthenticatedUserInfo;
import de.cuioss.tools.net.UrlParameter;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Core service interface for handling OAuth2 authentication operations.
 * This service manages the OAuth2 authentication flow, token handling,
 * and user information retrieval.
 * 
 * <p>Primary responsibilities include:
 * <ul>
 *   <li>Managing OAuth2 login flow and token requests</li>
 *   <li>Retrieving and processing user information</li>
 *   <li>Handling OAuth2 redirect URLs and state management</li>
 *   <li>Converting OAuth2 responses into authenticated user objects</li>
 * </ul>
 * 
 * <p>The service integrates with standard OAuth2 endpoints:
 * <ul>
 *   <li>/authorize - For initiating authentication</li>
 *   <li>/token - For obtaining access tokens</li>
 *   <li>/userinfo - For retrieving user details</li>
 * </ul>
 * 
 * <p>Implementation notes:
 * <ul>
 *   <li>All implementations must be thread-safe</li>
 *   <li>Token and state handling must follow OAuth2 security best practices</li>
 *   <li>Error handling should use {@link OauthAuthenticationException}</li>
 * </ul>
 * 
 * @see Oauth2Configuration
 * @see OauthAuthenticatedUserInfo
 */
public interface Oauth2Service {

    /**
     * Creates an authenticated user info object by performing the OAuth2 token exchange
     * and retrieving user data from the userinfo endpoint.
     * 
     * <p>The returned {@link AuthenticatedUserInfo} will contain:
     * <ul>
     *   <li>Display Name: From OAuth2 'preferred_username' claim</li>
     *   <li>Identifier: From OAuth2 'sub' claim</li>
     *   <li>Context Map: Additional userinfo data</li>
     * </ul>
     *
     * @param servletRequest The current HTTP request containing session information
     * @param code The OAuth2 authorization code from the callback request
     * @param state The state parameter from the callback for CSRF protection
     * @param scopes Space-separated list of OAuth2 scopes to request
     * @param codeVerifier PKCE code verifier for enhanced security
     * @return An authenticated user info object containing user details and tokens
     * @throws OauthAuthenticationException if authentication fails or user info cannot be retrieved
     * @throws IllegalArgumentException if any required parameter is null or invalid
     */
    AuthenticatedUserInfo createAuthenticatedUserInfo(HttpServletRequest servletRequest, UrlParameter code,
                                                      UrlParameter state, String scopes, final String codeVerifier);

    /**
     * Calculates the URL-encoded redirect URL for the OAuth2 authorization request.
     * This URL is sent to the OAuth2 server as a parameter in the authorization request.
     *
     * @param url The base redirect URL to encode
     * @return The URL-encoded redirect URL string
     * @throws IllegalArgumentException if the URL is null or invalid
     */
    String calcEncodedRedirectUrl(String url);

    /**
     * Retrieves user information from the OAuth2 server and creates an authenticated user.
     * This method fetches user details using the provided access token and combines them
     * with the requested scopes and timestamp information.
     *
     * @param scopes Space-separated list of OAuth2 scopes associated with the token
     * @param token The OAuth2 access token response containing tokens and related data
     * @param tokenTimestamp Timestamp when the token was issued or retrieved
     * @return An authenticated user info object containing user details and token information
     * @throws OauthAuthenticationException if user info cannot be retrieved or is invalid
     * @throws IllegalArgumentException if the token is null or expired
     */
    AuthenticatedUserInfo retrieveAuthenticatedUser(String scopes, Token token, int tokenTimestamp);

    /**
     * Retrieves an access token for the client using the client credentials flow.
     * This method performs client authentication and requests a token with the
     * specified scopes.
     *
     * @param scopes Space-separated list of OAuth2 scopes to request, or null for all registered scopes
     * @return A token object containing the access token and related metadata
     * @throws OauthAuthenticationException if token retrieval fails
     * @throws IllegalArgumentException if the scopes are invalid
     */
    String retrieveClientToken(String scopes);

    /**
     * Refreshes an existing access token for the authenticated user.
     * This method uses the refresh token to obtain a new access token
     * when the current one is expired or about to expire.
     *
     * @param currentUser The currently authenticated user containing the refresh token
     * @return A new token object containing the refreshed access token and metadata
     * @throws OauthAuthenticationException if token refresh fails
     * @throws IllegalArgumentException if the user or refresh token is invalid
     */
    String refreshToken(OauthAuthenticatedUserInfo currentUser);
}
