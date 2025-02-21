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

import de.cuioss.portal.authentication.oauth.Oauth2Configuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

import static de.cuioss.tools.base.Preconditions.checkState;

/**
 * Default implementation of {@link Oauth2Configuration}.
 * This class provides a complete implementation of the OAuth2 configuration
 * contract, supporting all standard OAuth2 configuration properties.
 * 
 * <p>Features:
 * <ul>
 *   <li>Immutable after construction</li>
 *   <li>Builder pattern for flexible instantiation</li>
 *   <li>Serializable for configuration storage</li>
 *   <li>Validation of required configuration properties</li>
 * </ul>
 * 
 * <p>The configuration includes all standard OAuth2 endpoints and properties:
 * <ul>
 *   <li>Client credentials (ID and secret)</li>
 *   <li>Authorization endpoint</li>
 *   <li>Token endpoint</li>
 *   <li>User info endpoint</li>
 *   <li>Role mapping configuration</li>
 *   <li>Redirect URI handling</li>
 * </ul>
 * 
 * @see Oauth2Configuration
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2ConfigurationImpl implements Oauth2Configuration {

    @Serial
    private static final long serialVersionUID = 6666334248327168722L;

    /** OAuth2 client identifier assigned by the authorization server */
    private String clientId;

    /** OAuth2 client secret for client authentication */
    private String clientSecret;

    /**
     * Authorization endpoint URI used by the client to obtain authorization
     * from the resource owner via user-agent redirection.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6749#section-3.1">
     *      OAuth 2.0 Authorization Endpoint</a>
     */
    private String authorizeUri;

    private String userInfoUri;

    /**
     * Used by the client to exchange an authorization grant for an access token,
     * typically with client authentication.
     */
    private String tokenUri;

    private String externalContextPath;

    private String initialScopes;

    private String logoutUri;

    private List<String> roleMapperClaims;

    private String logoutRedirectParamName;

    private String postLogoutRedirectUri;

    private boolean logoutWithIdTokenHintEnabled;

    public void validate() {
        validateRequiredAttributes();
    }

    private void validateRequiredAttributes() {
        checkState(null != clientId, "OAuth clientId missing");
        checkState(null != authorizeUri, "OAuth authorizeUri missing");
        checkState(null != tokenUri, "OAuth tokenUri missing");
    }
}
