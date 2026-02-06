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
package de.cuioss.portal.authentication.oauth.impl;

import de.cuioss.portal.authentication.oauth.OAuthConfigKeys;
import de.cuioss.portal.authentication.oauth.Oauth2Configuration;
import de.cuioss.portal.restclient.CuiRestClientBuilder;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.GET;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.cuioss.portal.authentication.oauth.OAuthConfigKeys.*;
import static de.cuioss.portal.authentication.oauth.PortalAuthenticationOauthLogMessages.ERROR;
import static de.cuioss.portal.authentication.oauth.PortalAuthenticationOauthLogMessages.WARN;
import static de.cuioss.tools.net.UrlHelper.addTrailingSlashToUrl;
import static de.cuioss.tools.string.MoreStrings.isBlank;

/**
 * CDI producer for {@link Oauth2Configuration} that supports OpenID Connect Discovery.
 * This producer automatically configures OAuth2 settings by fetching configuration
 * from the OpenID Connect provider's discovery endpoint.
 * 
 * <p>Configuration sources in order of precedence:
 * <ul>
 *   <li>OpenID Connect Discovery document (.well-known/openid-configuration)</li>
 *   <li>Fallback to manual configuration via application properties</li>
 * </ul>
 * 
 * <p>Required configuration properties:
 * <ul>
 *   <li>{@code authentication.oidc.server.baseUrl} - Base URL of the OIDC provider</li>
 *   <li>{@code authentication.oidc.client.id} - OAuth2 client ID</li>
 *   <li>{@code authentication.oidc.client.secret} - OAuth2 client secret</li>
 * </ul>
 * 
 * <p>Optional configuration:
 * <ul>
 *   <li>{@code authentication.oidc.server.discoveryPath} - Custom discovery path</li>
 *   <li>{@code authentication.oidc.client.role_mapper_claim} - Role mapping claim</li>
 * </ul>
 * 
 * @see OAuthConfigKeys
 * @see Oauth2Configuration
 */
@ApplicationScoped
public class Oauth2DiscoveryConfigurationProducer {

    private static final CuiLogger LOGGER = new CuiLogger(Oauth2DiscoveryConfigurationProducer.class);

    @Getter
    @Produces
    @Dependent
    private Oauth2Configuration configuration;

    private final Provider<Optional<String>> oauth2clientId;

    private final Provider<Optional<String>> oauth2clientSecret;

    private final Provider<Optional<String>> serverBaseUrl;

    private final Provider<Optional<String>> oauth2discoveryUri;

    private final Provider<Optional<String>> externalContextPath;

    private final Provider<Optional<String>> oauth2initialScopes;

    private final Provider<Optional<List<String>>> roleMapperClaim;

    private final Provider<Optional<String>> logoutRedirectParameter;

    private final Provider<Optional<Boolean>> logoutWithIdTokenHintProvider;

    private final Provider<Optional<String>> postLogoutRedirectUri;

    private final Provider<Optional<String>> internalTokenUrl;

    private final Provider<Optional<String>> internalUserInfoUrl;

    private final Provider<Boolean> configValidationEnabled;

    @Inject
    public Oauth2DiscoveryConfigurationProducer(
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_ID) Provider<Optional<String>> oauth2clientId,
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_SECRET) Provider<Optional<String>> oauth2clientSecret,
            @ConfigProperty(name = OPEN_ID_SERVER_BASE_URL) Provider<Optional<String>> serverBaseUrl,
            @ConfigProperty(name = OPEN_ID_DISCOVER_PATH) Provider<Optional<String>> oauth2discoveryUri,
            @ConfigProperty(name = OAuthConfigKeys.EXTERNAL_HOSTNAME) Provider<Optional<String>> externalContextPath,
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_DEFAULT_SCOPES) Provider<Optional<String>> oauth2initialScopes,
            @ConfigProperty(name = OPEN_ID_ROLE_MAPPER_CLAIM) Provider<Optional<List<String>>> roleMapperClaim,
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER) Provider<Optional<String>> logoutRedirectParameter,
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_ADD_ID_TOKEN_HINT) Provider<Optional<Boolean>> logoutWithIdTokenHintProvider,
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI) Provider<Optional<String>> postLogoutRedirectUri,
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_SERVER_TOKEN_URL) Provider<Optional<String>> internalTokenUrl,
            @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_SERVER_USER_INFO_URL) Provider<Optional<String>> internalUserInfoUrl,
            @ConfigProperty(name = OAuthConfigKeys.CONFIG_VALIDATION_ENABLED) Provider<Boolean> configValidationEnabled) {
        this.oauth2clientId = oauth2clientId;
        this.oauth2clientSecret = oauth2clientSecret;
        this.serverBaseUrl = serverBaseUrl;
        this.oauth2discoveryUri = oauth2discoveryUri;
        this.externalContextPath = externalContextPath;
        this.oauth2initialScopes = oauth2initialScopes;
        this.roleMapperClaim = roleMapperClaim;
        this.logoutRedirectParameter = logoutRedirectParameter;
        this.logoutWithIdTokenHintProvider = logoutWithIdTokenHintProvider;
        this.postLogoutRedirectUri = postLogoutRedirectUri;
        this.internalTokenUrl = internalTokenUrl;
        this.internalUserInfoUrl = internalUserInfoUrl;
        this.configValidationEnabled = configValidationEnabled;
    }

    /**
     * The request to retrieve information about the current authenticated user.
     */
    public interface RequestDiscovery extends Closeable {

        @GET
        Map<String, Object> getDiscovery();
    }

    @PostConstruct
    public void init() {

        configuration = null;

        final var settingServerBaseUrl = serverBaseUrl.get().orElse(null);
        final var settingOauth2discoveryUri = oauth2discoveryUri.get().orElse(null);

        if (!isBlank(settingServerBaseUrl) && !isBlank(settingOauth2discoveryUri)) {
            final var builder = new CuiRestClientBuilder(LOGGER);
            final var discoveryURI = addTrailingSlashToUrl(settingServerBaseUrl) + settingOauth2discoveryUri;
            LOGGER.debug("Using discovery URI: %s", discoveryURI);
            builder.url(discoveryURI);
            try (final var discoveryEndpoint = builder.build(RequestDiscovery.class)) {
                final var discovery = discoveryEndpoint.getDiscovery();
                configuration = createConfiguration(discovery);
            } catch (final IOException | RuntimeException e) {
                LOGGER.error(e, ERROR.DISCOVERY_FAILED);
            }
        } else {
            LOGGER.warn(WARN.CONFIG_KEYS_NOT_SET, OPEN_ID_SERVER_BASE_URL, OPEN_ID_DISCOVER_PATH);
        }

        LOGGER.debug("Configuration created: %s", configuration);

        if (null != configuration && configValidationEnabled.get()) {
            configuration.validate();
        }
    }

    private Oauth2Configuration createConfiguration(final Map<String, Object> discovery) {
        final var newConfiguration = new Oauth2ConfigurationImpl();

        // fill dto with data from a configuration system

        oauth2clientId.get().ifPresent(newConfiguration::setClientId);
        oauth2clientSecret.get().ifPresent(newConfiguration::setClientSecret);
        externalContextPath.get().ifPresent(newConfiguration::setExternalContextPath);
        oauth2initialScopes.get().ifPresent(newConfiguration::setInitialScopes);
        logoutRedirectParameter.get().ifPresent(newConfiguration::setLogoutRedirectParamName);
        postLogoutRedirectUri.get().ifPresent(newConfiguration::setPostLogoutRedirectUri);
        Optional<List<String>> roleMapperClaims = roleMapperClaim.get();
        if (roleMapperClaims.isPresent()) {
            newConfiguration.setRoleMapperClaims(roleMapperClaims.get());
        } else {
            newConfiguration.setRoleMapperClaims(Collections.emptyList());
        }
        logoutWithIdTokenHintProvider.get().ifPresent(newConfiguration::setLogoutWithIdTokenHintEnabled);

        // fill dto with data from discovery endpoint.
        // endpoints are usually pointing to cluster-external URLs, i.e., reachable by web-browser.
        for (final Map.Entry<String, Object> entry : discovery.entrySet()) {
            switch (entry.getKey()) {
                case "authorization_endpoint":
                    newConfiguration.setAuthorizeUri((String) entry.getValue());
                    break;
                case "userinfo_endpoint":
                    newConfiguration.setUserInfoUri((String) entry.getValue());
                    break;
                case "token_endpoint":
                    newConfiguration.setTokenUri((String) entry.getValue());
                    break;
                case "end_session_endpoint":
                    newConfiguration.setLogoutUri((String) entry.getValue());
                    break;
                default:
            }
        }

        // overwrite well-known config, if present

        internalTokenUrl.get().ifPresent(url -> {
            LOGGER.debug("Overwriting token URL from %s to %s", newConfiguration.getTokenUri(), url);
            newConfiguration.setTokenUri(url);
        });
        internalUserInfoUrl.get().ifPresent(url -> {
            LOGGER.debug("Overwriting userinfo URL from %s to %s", newConfiguration.getUserInfoUri(), url);
            newConfiguration.setUserInfoUri(url);
        });

        return newConfiguration;
    }
}
