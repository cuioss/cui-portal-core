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

import de.cuioss.tools.net.UrlParameter;
import lombok.experimental.UtilityClass;

import java.util.Optional;

/**
 * Defines the standard parameters for OpenID Connect RP-Initiated Logout 1.0.
 * This utility class provides constants and helper methods for handling logout
 * parameters according to the OpenID Connect RP-Initiated Logout 1.0 specification.
 *
 * <p>Supported parameters include:
 * <ul>
 *   <li>{@code id_token_hint} - Previously issued ID Token</li>
 *   <li>{@code logout_hint} - Hint about the End-User's preferred logout mechanism</li>
 *   <li>{@code client_id} - OAuth 2.0 Client Identifier</li>
 *   <li>{@code post_logout_redirect_uri} - URL to redirect to after logout</li>
 *   <li>{@code state} - Opaque value for maintaining state</li>
 *   <li>{@code ui_locales} - End-User's preferred languages for the UI</li>
 * </ul>
 *
 * @see <a href="https://openid.net/specs/openid-connect-rpinitiated-1_0.html">
 * OpenID Connect RP-Initiated Logout 1.0</a>
 */
@UtilityClass
public class OidcRpInitiatedLogoutParams {

    /**
     * Parameter name for the ID Token passed to the logout endpoint
     */
    public static final String ID_TOKEN_HINT = "id_token_hint";

    /**
     * Parameter name for hints about the End-User's preferred logout mechanism
     */
    public static final String LOGOUT_HINT = "logout_hint";

    /**
     * Parameter name for the OAuth 2.0 Client Identifier
     */
    public static final String CLIENT_ID = "client_id";

    /**
     * Parameter name for the URL to redirect to after logout completion
     */
    public static final String POST_LOGOUT_REDIRECT_URI = "post_logout_redirect_uri";

    /**
     * Parameter name for maintaining state between the request and callback
     */
    public static final String STATE = "state";

    /**
     * Parameter name for the End-User's preferred languages for the UI
     */
    public static final String UI_LOCALES = "ui_locales";

    /**
     * Creates a URL parameter for the ID token hint.
     * The ID token is required for the IDP to validate the post-logout redirect URI against the OAuth client's registered
     * redirect URIs.
     *
     * @param idTokenHint The ID token to include in the logout request
     * @return Optional containing the ID token parameter if the token is present,
     * empty Optional otherwise
     */
    public static Optional<UrlParameter> getIdTokenHintUrlParam(String idTokenHint) {
        return Optional.ofNullable(idTokenHint).map(value -> new UrlParameter(ID_TOKEN_HINT, value));
    }

    /**
     * @param logoutHint to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getLogoutHintUrlParam(String logoutHint) {
        return Optional.ofNullable(logoutHint).map(value -> new UrlParameter(LOGOUT_HINT, value));
    }

    /**
     * @param clientId to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getClientIdUrlParam(String clientId) {
        return Optional.ofNullable(clientId).map(value -> new UrlParameter(CLIENT_ID, value));
    }

    /**
     * @param redirectUri to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getPostLogoutRedirectUriUrlParam(String redirectUri) {
        return Optional.ofNullable(redirectUri).map(value -> new UrlParameter(POST_LOGOUT_REDIRECT_URI, value));
    }

    /**
     * @param state to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getStateUrlParam(String state) {
        return Optional.ofNullable(state).map(value -> new UrlParameter(STATE, value));
    }

    /**
     * @param locales to be created
     * @return The correctly prefixed parameter
     */
    public static Optional<UrlParameter> getUiLocalesUrlParam(String locales) {
        return Optional.ofNullable(locales).map(value -> new UrlParameter(UI_LOCALES, value));
    }
}
