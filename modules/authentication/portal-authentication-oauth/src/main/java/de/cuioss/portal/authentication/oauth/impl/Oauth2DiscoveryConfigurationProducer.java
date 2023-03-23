package de.cuioss.portal.authentication.oauth.impl;

import static de.cuioss.portal.configuration.OAuthConfigKeys.AUTHENTICATION_BASE;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_DISCOVER_PATH;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_ROLE_MAPPER_CLAIM;
import static de.cuioss.portal.configuration.OAuthConfigKeys.OPEN_ID_SERVER_BASE_URL;
import static de.cuioss.tools.net.UrlHelper.addTrailingSlashToUrl;
import static de.cuioss.tools.string.MoreStrings.isBlank;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.GET;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.cuioss.portal.authentication.oauth.Oauth2Configuration;
import de.cuioss.portal.configuration.OAuthConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.portal.configuration.PortalConfigurationChangeInterceptor;
import de.cuioss.portal.restclient.CuiRestClientBuilder;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Getter;

/**
 * Produces {@link Oauth2Configuration} using the new config params ({@see OAuthConfigKeys}).
 *
 * @author Matthias Walliczek
 */
@ApplicationScoped
public class Oauth2DiscoveryConfigurationProducer {

    private static final CuiLogger LOGGER = new CuiLogger(Oauth2DiscoveryConfigurationProducer.class);

    @Getter
    @Produces
    @Dependent
    private Oauth2Configuration configuration;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_ID)
    private Provider<Optional<String>> oauth2clientId;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_SECRET)
    private Provider<Optional<String>> oauth2clientSecret;

    @Inject
    @ConfigProperty(name = OPEN_ID_SERVER_BASE_URL)
    private Provider<Optional<String>> serverBaseUrl;

    @Inject
    @ConfigProperty(name = OPEN_ID_DISCOVER_PATH)
    private Provider<Optional<String>> oauth2discoveryUri;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.EXTERNAL_HOSTNAME)
    private Provider<Optional<String>> externalContextPath;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_DEFAULT_SCOPES)
    private Provider<Optional<String>> oauth2initialScopes;

    @Inject
    @ConfigProperty(name = OPEN_ID_ROLE_MAPPER_CLAIM)
    private Provider<Optional<List<String>>> roleMapperClaim;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_REDIRECT_PARAMETER)
    private Provider<Optional<String>> logoutRedirectParameter;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_ADD_ID_TOKEN_HINT)
    private Provider<Optional<Boolean>> logoutWithIdTokenHintProvider;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_POST_LOGOUT_REDIRECT_URI)
    private Provider<Optional<String>> postLogoutRedirectUri;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_SERVER_TOKEN_URL)
    private Provider<Optional<String>> internalTokenUrl;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_SERVER_USER_INFO_URL)
    private Provider<Optional<String>> internalUserInfoUrl;

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
            LOGGER.debug("Using discoveryURI {}", discoveryURI);
            try {
                final var discovery = builder
                        .url(discoveryURI)
                        .build(RequestDiscovery.class)
                        .getDiscovery();
                configuration = createConfiguration(discovery);
            } catch (final Exception e) {
                LOGGER.error(e, "Auto discovery of oauth config failed, using URI: {}", discoveryURI);
            }
        } else {
            LOGGER.warn("Oauth config key '{}' and/or '{}' not set, trying fallback",
                    OPEN_ID_SERVER_BASE_URL, OPEN_ID_DISCOVER_PATH);
        }

        LOGGER.debug("oauth config: {}", configuration);
    }

    private Oauth2Configuration createConfiguration(final Map<String, Object> discovery) {
        final var newConfiguration = new Oauth2ConfigurationImpl();

        // fill dto with data from configuration system

        oauth2clientId.get().ifPresent(newConfiguration::setClientId);
        oauth2clientSecret.get().ifPresent(newConfiguration::setClientSecret);
        externalContextPath.get().ifPresent(newConfiguration::setExternalContextPath);
        oauth2initialScopes.get().ifPresent(newConfiguration::setInitialScopes);
        logoutRedirectParameter.get().ifPresent(newConfiguration::setLogoutRedirectParamName);
        postLogoutRedirectUri.get().ifPresent(newConfiguration::setPostLogoutRedirectUri);
        roleMapperClaim.get().ifPresent(newConfiguration::setRoleMapperClaims);
        logoutWithIdTokenHintProvider.get().ifPresent(newConfiguration::setLogoutWithIdTokenHintEnabled);

        // fill dto with data from discovery endpoint.
        // endpoints are usually pointing to cluster-external URLs, i.e. reachable by web-browser.
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
            LOGGER.debug("overwrite well-known token-url '{}' with: {}",
                    newConfiguration.getTokenUri(), url);
            newConfiguration.setTokenUri(url);
        });
        internalUserInfoUrl.get().ifPresent(url -> {
            LOGGER.debug("overwrite well-known userinfo-url '{}' with: {}",
                    newConfiguration.getUserInfoUri(), url);
            newConfiguration.setUserInfoUri(url);
        });

        return newConfiguration;
    }

    /**
     * Listener for {@link PortalConfigurationChangeEvent}s. Reconfigures the
     * default-resource-configuration
     *
     * @param deltaMap include difference to current configuration settings
     */
    @PortalConfigurationChangeInterceptor(keyPrefix = AUTHENTICATION_BASE)
    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        LOGGER.info("Change in oauth2 configuration found, reconfigure");
        init();
    }
}
