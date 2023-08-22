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

import java.io.Serializable;
import java.util.List;

/**
 * Contains all configuration properties for the oauth2 client
 */
public interface Oauth2Configuration extends Serializable {

    /**
     * @return
     */
    String getAuthorizeUri();

    /**
     * @return The client-id of the portal-application. This is provided /
     *         maintained by the corresponding SSO-Server. Must be set by the
     *         installation.
     */
    String getClientId();

    /**
     * @return The client-secret of the portal-application. This is provided /
     *         maintained by the corresponding SSO-Server. Must be set by the
     *         installation.
     */
    String getClientSecret();

    String getTokenUri();

    String getUserInfoUri();

    List<String> getRoleMapperClaims();

    /**
     * @return The current external host name used to calculate the redirect uri for
     *         the browser of the external user.
     */
    String getExternalContextPath();

    /**
     * @return The default scopes to be requested by the client. The individual
     *         scopes are separated by whitespaces.
     */
    String getInitialScopes();

    String getLogoutUri();

    /**
     * @return The parameter name to transport the url to redirect after logout.
     */
    String getLogoutRedirectParamName();

    /**
     * @return full URI to be used for the {@code post_logout_redirect_uri}
     *         parameter.
     */
    String getPostLogoutRedirectUri();

    boolean isLogoutWithIdTokenHintEnabled();
}
