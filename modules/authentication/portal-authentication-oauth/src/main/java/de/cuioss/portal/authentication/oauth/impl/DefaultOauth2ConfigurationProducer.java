package de.cuioss.portal.authentication.oauth.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.cuioss.portal.authentication.oauth.DeprecatedOauth2ConfigurationKeys;
import de.cuioss.portal.configuration.OAuthConfigKeys;
import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.string.MoreStrings;
import lombok.Getter;

/**
 * @author Matthias Walliczek
 * @deprecated Please use {@link Oauth2DiscoveryConfigurationProducer} instead
 */
@ApplicationScoped
@Deprecated
public class DefaultOauth2ConfigurationProducer {

    private static final CuiLogger log = new CuiLogger(DefaultOauth2ConfigurationProducer.class);

    @Getter
    private Oauth2ConfigurationImpl configuration;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.CLIENT_ID)
    private Provider<Optional<String>> oauth2clientId;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.CLIENT_SECRET)
    private Provider<Optional<String>> oauth2clientSecret;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.OAUTH2_SERVER_AUTHENTICATION_URI)
    private Provider<Optional<String>> oauth2authorize;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.CURRENT_DEPLOYMENT_EXTERNAL_CONTEXT_PATH)
    private Provider<Optional<String>> externalContextPath;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.OAUTH2_SERVER_REST_URI)
    private Provider<Optional<String>> oauth2uri;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.OAUTH2INITIAL_SCOPES)
    private Provider<Optional<String>> oauth2initialScopes;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.OAUTH2LOGOUT_URI)
    private Provider<Optional<String>> oauth2logoutUri;

    @Inject
    @ConfigProperty(name = DeprecatedOauth2ConfigurationKeys.OAUTH2LOGOUT_URI_REDIRECT_PARAMETER)
    private Provider<Optional<String>> oauth2logoutUriRedirectParameter;

    @Inject
    @ConfigProperty(name = OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_ADD_ID_TOKEN_HINT)
    private Provider<Optional<Boolean>> logoutWithIdTokenHintProvider;

    @PostConstruct
    public void init() {
        if (MoreStrings.isEmpty(oauth2clientId.get().orElse(null))) {
            log.error("Oauth config key '" + DeprecatedOauth2ConfigurationKeys.CLIENT_ID + "' not set!");
            return;
        }
        log.warn("The oauth2 config is deprecated, consider migration.");

        var configBuilder = Oauth2ConfigurationImpl.builder();

        oauth2clientId.get().ifPresent(configBuilder::clientId);
        oauth2clientSecret.get().ifPresent(configBuilder::clientSecret);
        externalContextPath.get().ifPresent(configBuilder::externalContextPath);
        oauth2authorize.get().ifPresent(configBuilder::authorizeUri);
        oauth2uri.get().ifPresent(uri -> {
            configBuilder.tokenUri(uri + "/token");
            configBuilder.userInfoUri(uri + "/userinfo");
        });
        oauth2initialScopes.get().ifPresent(configBuilder::initialScopes);
        oauth2logoutUri.get().ifPresent(configBuilder::logoutUri);
        oauth2logoutUriRedirectParameter.get().ifPresent(configBuilder::logoutRedirectParamName);
        logoutWithIdTokenHintProvider.get().ifPresent(configBuilder::logoutWithIdTokenHintEnabled);

        configBuilder.roleMapperClaims(Collections.emptyList());

        configuration = configBuilder.build();
    }

    /**
     * Listener for {@link PortalConfigurationChangeEvent}s. Reconfigures the
     * default-resource-configuration
     *
     * @param deltaMap
     */
    void configurationChangeEventListener(
        @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        if (deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.CLIENT_ID)
            || deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.CLIENT_SECRET)
            || deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.OAUTH2_SERVER_AUTHENTICATION_URI)
            || deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.OAUTH2INITIAL_SCOPES)
            || deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.CURRENT_DEPLOYMENT_EXTERNAL_CONTEXT_PATH)
            || deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.OAUTH2LOGOUT_URI)
            || deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.OAUTH2LOGOUT_URI_REDIRECT_PARAMETER)
            || deltaMap.containsKey(DeprecatedOauth2ConfigurationKeys.OAUTH2_SERVER_REST_URI)
            || deltaMap.containsKey(OAuthConfigKeys.OPEN_ID_CLIENT_LOGOUT_ADD_ID_TOKEN_HINT)) {
            log.info("Change in oauth2 configuration found, reconfiguring ...");
            init();
        }
    }
}
