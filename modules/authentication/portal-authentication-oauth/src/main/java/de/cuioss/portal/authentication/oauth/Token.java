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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an OAuth2 token response containing standard OAuth2 token fields.
 * This class is designed to be JSON-deserializable and handles the standard
 * OAuth2 token response format.
 *
 * <p>The token response includes:
 * <ul>
 *   <li>ID Token (OpenID Connect)</li>
 *   <li>Access Token</li>
 *   <li>Refresh Token</li>
 *   <li>Token Type</li>
 *   <li>Expiration Information</li>
 *   <li>State (for flow validation)</li>
 * </ul>
 *
 * <p>This class is immutable after construction and thread-safe.
 * Unknown JSON properties are ignored during deserialization.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token implements Serializable {

    @Serial
    private static final long serialVersionUID = 1814898874197817661L;

    /**
     * The OpenID Connect ID Token, represented as a JWT (JSON Web Token).
     * Contains claims about the authentication event and user identity.
     * Only present when using OpenID Connect flows.
     */
    private String id_token;

    /**
     * The OAuth2 access token used to access protected resources.
     * Required for all OAuth2 flows and must be included in resource requests
     * using the specified token type (usually "Bearer").
     */
    private String access_token;

    /**
     * The OAuth2 refresh token used to obtain new access tokens.
     * Optional and only present when the refresh_token scope was requested
     * and granted. Should be securely stored for token renewal.
     */
    private String refresh_token;

    /**
     * The type of access token issued, typically "Bearer".
     * Specifies how the access token must be used in resource requests,
     * usually in the HTTP Authorization header.
     */
    private String token_type;

    /**
     * Time in seconds until the access token expires.
     * Applications should refresh the token before expiration
     * using the refresh token if available.
     */
    private String expires_in;

    /**
     * The state parameter used for CSRF protection.
     * Must match the state sent in the authorization request
     * to prevent CSRF attacks.
     */
    private String state;
}
