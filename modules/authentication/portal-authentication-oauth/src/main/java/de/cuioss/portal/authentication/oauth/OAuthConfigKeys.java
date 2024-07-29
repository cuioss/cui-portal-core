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

import lombok.experimental.UtilityClass;

/**
 * Provide the keys for the application.properties to configure oauth2.
 * <p>
 *
 * @author Matthias Walliczek
 */
@UtilityClass
public class OAuthConfigKeys {

    /**
     * Base key for constructing key root at 'authentication.'.
     */
    public static final String AUTHENTICATION_BASE = "authentication.";

    public static final String OPEN_ID_BASE = AUTHENTICATION_BASE + "oidc.";

    private static final String OPEN_ID_CLIENT_BASE = OPEN_ID_BASE + "client.";
    private static final String OPEN_ID_SERVER_BASE = OPEN_ID_BASE + "server.";

    private static final String OPEN_ID_CLIENT_LOGOUT_BASE = OPEN_ID_CLIENT_BASE + "logout.";

    /**
     * <p>
     * The external host name of the authentication service provider. This hostname
     * can be used to e.g. calculate the redirect uri for the web-browser.
     * </p>
     */
    public static final String EXTERNAL_HOSTNAME = AUTHENTICATION_BASE + "externalHostname";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #OPEN_ID_CLIENT_ID}
     * <p>
     * Defines the client-id of the portal-application. This is provided /
     * maintained by the corresponding SSO-Server. Must be set by the installation.
     * </p>
     */
    public static final String OPEN_ID_CLIENT_ID = OPEN_ID_CLIENT_BASE + "id";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #OPEN_ID_ROLE_MAPPER_CLAIM}
     * <p>
     * Defines the name of the claim / attribute that is used for mapping the roles
     * to the user, multiple values are allowed, separated by comma. Defaults to
     * 'ehealth-suite-roles'
     * </p>
     */
    public static final String OPEN_ID_ROLE_MAPPER_CLAIM = OPEN_ID_CLIENT_BASE + "role_mapper_claim";

    /**
     * <p>
     * The (cluster-internal) url to the oauth2 servers token endpoint. For
     * cluster-internal communication with the server, e.g.
     * https://xyz/member-account-facade/oidc.
     * </p>
     * <p>
     * If this config property is not set, the value served form the well-know
     * endpoint will be used.
     * ({@link #OPEN_ID_SERVER_BASE_URL}/{@link #OPEN_ID_DISCOVER_PATH})
     * </p>
     */
    public static final String OPEN_ID_SERVER_TOKEN_URL = OPEN_ID_SERVER_BASE + "token_endpoint_url";

    /**
     * <p>
     * The (cluster-internal) url to the oauth2 servers userinfo endpoint. For
     * cluster-internal communication with the server, e.g.
     * https://xyz/member-account-facade/oidc/userinfo.
     * </p>
     * <p>
     * If this config property is not set, the value served form the well-know
     * endpoint will be used.
     * ({@link #OPEN_ID_SERVER_BASE_URL}/{@link #OPEN_ID_DISCOVER_PATH})
     * </p>
     */
    public static final String OPEN_ID_SERVER_USER_INFO_URL = OPEN_ID_SERVER_BASE + "userinfo_endpoint_url";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #OPEN_ID_CLIENT_DEFAULT_SCOPES}
     * <p>
     * Defines the default scopes to be requested by the client. The individual
     * scopes are separated by whitespaces. Defaults to 'openid profile'
     * </p>
     */
    public static final String OPEN_ID_CLIENT_DEFAULT_SCOPES = OPEN_ID_CLIENT_BASE + "default_scopes";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #OPEN_ID_CLIENT_SECRET}
     * <p>
     * Defines the client-secret of the portal-application. This is provided /
     * maintained by the corresponding SSO-Server. Must be set by the installation.
     * </p>
     */
    public static final String OPEN_ID_CLIENT_SECRET = OPEN_ID_CLIENT_BASE + "password";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER}
     * <p>
     * Defines the parameter name to transport the url to redirect after logout. It
     * is not the URL, but the URL-Parameter name.
     * </p>
     */
    public static final String OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER = OPEN_ID_CLIENT_BASE
            + "logout_redirect_parameter";

    /**
     * <p>
     * The fully valid and absolute URI to redirect to after logout. This is used in
     * conjunction with {@link #OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER}. The URI
     * must also be configured in the oauth client config (IDP).
     * </p>
     */
    public static final String OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI = OPEN_ID_CLIENT_LOGOUT_BASE + "redirect_uri";

    /**
     * {@value #OPEN_ID_CLIENT_LOGOUT_ADD_ID_TOKEN_HINT}
     * <p>
     * Enable or disable the addition of the id_token_hint parameter to the logout
     * URL.
     * </p>
     */
    public static final String OPEN_ID_CLIENT_LOGOUT_ADD_ID_TOKEN_HINT = OPEN_ID_CLIENT_LOGOUT_BASE
            + "params.add_id_token_hint";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #OPEN_ID_SERVER_BASE_URL}
     * <p>
     * The url of the server that provides the authentication endpoints. It is
     * interpreted as a complete url including context path, e.g.
     * </p>
     */
    public static final String OPEN_ID_SERVER_BASE_URL = OPEN_ID_SERVER_BASE + "url";

    /**
     * Context parameter within configuration-subsystem with the name
     * {@value #OPEN_ID_DISCOVER_PATH}
     * <p>
     * The url relative to {@link #OPEN_ID_SERVER_BASE_URL} defining the OpenID
     * Connect Discovery endpoint, defaults to .well-known/openid-configuration.
     * </p>
     */
    public static final String OPEN_ID_DISCOVER_PATH = OPEN_ID_SERVER_BASE + "discovery_path";

    /**
     * Ensure that the final config is valid, i.e. required attributes are present.
     */
    public static final String CONFIG_VALIDATION_ENABLED = OPEN_ID_BASE + "validation.enabled";
}
