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

import java.io.Serializable;
import java.util.List;

/**
 * Defines the configuration contract for OAuth2 client integration.
 * This interface encapsulates all necessary configuration properties required for
 * establishing and maintaining OAuth2-based authentication and authorization.
 *
 * <p>Implementations must ensure thread-safety and proper serialization support.
 * All getters should return immutable or defensively copied objects to maintain
 * configuration integrity.
 *
 * <p>The configuration includes:
 * <ul>
 *   <li>OAuth2 endpoints (authorize, token, userinfo)</li>
 *   <li>Client credentials (ID and secret)</li>
 *   <li>Role mapping configuration</li>
 *   <li>Host and redirect URI settings</li>
 * </ul>
 */
public interface Oauth2Configuration extends Serializable {

    /**
     * Retrieves the OAuth2 authorization endpoint URI.
     * 
     * @return The fully qualified URI where users are redirected to authenticate
     *         and authorize the application. Must be an HTTPS URL for production use.
     */
    String getAuthorizeUri();

    /**
     * Retrieves the OAuth2 client identifier.
     * 
     * @return The client ID assigned by the OAuth2 authorization server during
     *         application registration. This ID uniquely identifies the application
     *         to the authorization server.
     */
    String getClientId();

    /**
     * Retrieves the OAuth2 client secret.
     * 
     * @return The client secret assigned by the OAuth2 authorization server during
     *         application registration. This secret should be kept secure and is used
     *         for client authentication.
     */
    String getClientSecret();

    /**
     * Retrieves the OAuth2 token endpoint URI.
     * 
     * @return The fully qualified URI where the application exchanges authorization
     *         codes for access tokens. Must be an HTTPS URL for production use.
     */
    String getTokenUri();

    /**
     * Retrieves the OAuth2 userinfo endpoint URI.
     * 
     * @return The fully qualified URI where the application can retrieve authenticated
     *         user information using a valid access token. Must be an HTTPS URL for
     *         production use.
     */
    String getUserInfoUri();

    /**
     * Retrieves the list of claim names used for role mapping from the OAuth2 provider's
     * tokens/userinfo to application roles.
     * 
     * @return An immutable list of claim names that define how OAuth2 claims or scopes
     *         are mapped to application roles. Returns an empty list if no mappings are
     *         configured.
     */
    List<String> getRoleMapperClaims();

    /**
     * Retrieves the current external host name used to calculate the complete redirect URI for
     * the user's browser.
     * 
     * @return The base URL of the application, used for constructing redirect URIs
     *         and other application URLs. Should include protocol, host, and port
     *         if non-standard.
     */
    String getExternalContextPath();

    /**
     * Retrieves the default scopes to be requested by the client. The individual
     * scopes are separated by whitespaces.
     * 
     * @return An immutable list of OAuth2 scopes that the application requires for
     *         proper operation. These scopes will be requested during the authorization
     *         flow. Returns an empty list if no specific scopes are required.
     */
    String getInitialScopes();

    /**
     * Retrieves the logout endpoint URI used for terminating the user's session
     * 
     * @return The fully qualified URI where users are redirected to end their session
     *         with the OAuth2 server. May be null if the server does not support
     *         RP-initiated logout.
     */
    String getLogoutUri();

    /**
     * Retrieves the parameter name to transport the URL to redirect after logout.
     * 
     * @return The name of the parameter used to specify the post-logout redirect URI.
     */
    String getLogoutRedirectParamName();

    /**
     * Retrieves the full URI to be used for the {@code post_logout_redirect_uri}
     * parameter.
     * 
     * @return The fully qualified URI where users should be redirected after successful
     *         logout from the OAuth2 server. Will be combined with the host URL to
     *         form the complete post-logout redirect URI.
     */
    String getPostLogoutRedirectUri();

    /**
     * Checks if logout with ID token hint is enabled.
     * 
     * @return true if logout with ID token hint should be used to enhance the security
     *         of the logout flow, false if standard logout flow should be used.
     */
    boolean isLogoutWithIdTokenHintEnabled();

    /**
     * Validate oauth2 config.
     * 
     * @throws IllegalStateException if e.g. a required config param is missing or invalid.
     */
    void validate();
}
