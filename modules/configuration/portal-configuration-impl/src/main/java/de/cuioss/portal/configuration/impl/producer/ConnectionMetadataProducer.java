package de.cuioss.portal.configuration.impl.producer;

import static de.cuioss.tools.string.MoreStrings.emptyToNull;
import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import de.cuioss.portal.configuration.connections.exception.ConnectionConfigurationException;
import de.cuioss.portal.configuration.connections.impl.AuthenticationType;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata.ConnectionMetadataBuilder;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadataKeys;
import de.cuioss.portal.configuration.connections.impl.ConnectionType;
import de.cuioss.portal.configuration.connections.impl.StaticTokenResolver;
import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.io.MorePaths;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.net.ssl.KeyStoreProvider;
import de.cuioss.tools.net.ssl.KeyStoreType;
import de.cuioss.tools.string.MoreStrings;
import de.cuioss.uimodel.application.LoginCredentials;

/**
 * {@linkplain ConnectionMetadata} producer that defaults to the
 * <code>javax.net.ssl</code> properties if the connection type is
 * {@linkplain AuthenticationType#CERTIFICATE} and no custom properties for
 * trust-store or key-store are defined.
 *
 * @author Oliver Wolff
 * @author Sven Haag
 */
@ApplicationScoped
public class ConnectionMetadataProducer {

    private static final String RETURNING_EMPTY_VALUE = "No '{}' is present, returning empty-value";

    private static final CuiLogger log = new CuiLogger(ConnectionMetadataProducer.class);

    private static final String UNABLE_TO_CONSTRUCT_MSG = "Portal-116: Unable to construct ConnectionMetadata, due to ";

    /** "Portal-117: Configuration setting for baseName is missing." */
    public static final String MISSING_BASENAME_MSG = "Portal-117: Configuration setting for baseName is missing.";
    private static final String MISSING_CONFIG_MSG = "Portal-119: Missing configuration for {} detected.";
    private static final String MISSING_BASICAUTH_CONFIG_MSG = "Portal-120: Configuration for basic authentication is incomplete. Missing: ";
    private static final String MISSING_TOKEN_CONFIG_MSG = "Portal-120: Configuration for token based authentication is incomplete. Missing: ";
    private static final String INVALID_NUMBER_VALUE = "Portal-526: Invalid content for '%s', expected a number but was '%s'";

    /**
     * Try to create {@linkplain ConnectionMetadata}<br>
     * <h2>Attention</h2> If configuration is invalid and validation should be done
     * the injection will failed with
     * {@linkplain javax.enterprise.inject.CreationException} caused by
     * {@linkplain IllegalArgumentException}
     *
     * @param injectionPoint {@linkplain InjectionPoint} must not be {@code null}
     *
     * @return {@linkplain ConnectionMetadata} if configuration is valid or
     *         validation is disabled by
     *         {@linkplain ConfigAsConnectionMetadata#failOnInvalidConfiguration()}
     * @throws IllegalArgumentException if
     *                                  {@linkplain ConfigAsConnectionMetadata#failOnInvalidConfiguration()}
     *                                  is active and any configuration is invalid.
     *                                  This is checked by calling
     *                                  {@link ConnectionMetadata#validate()}
     */
    @Produces
    @Dependent
    @ConfigAsConnectionMetadata(baseName = "unused")
    ConnectionMetadata produceConnectionMetadata(final InjectionPoint injectionPoint) {
        final var metaData = ConfigurationHelper.resolveAnnotation(injectionPoint, ConfigAsConnectionMetadata.class)
                .orElseThrow(() -> new IllegalStateException("Invalid usage"));
        log.trace("Producing configuration for '{}'", metaData.baseName());
        final var failOnInvalidConfiguration = metaData.failOnInvalidConfiguration();
        return createConnectionMetadata(metaData.baseName(), failOnInvalidConfiguration);
    }

    /**
     * Creates an {@link ConnectionMetadata} instance from the given properties.
     *
     * @param baseName                   to be used, must not be null nor empty
     * @param failOnInvalidConfiguration boolean indicating whether the method
     *                                   should throw an
     *                                   {@link IllegalArgumentException} in case
     *                                   the configuration contains errors. This is
     *                                   checked by calling
     *                                   {@link ConnectionMetadata#validate()}
     * @return the created {@link ConnectionMetadata}
     */
    public static ConnectionMetadata createConnectionMetadata(final String baseName,
            final boolean failOnInvalidConfiguration) {
        log.trace("Creating ConnectionMetadata for '{}'", baseName);
        final var builder = ConnectionMetadata.builder();
        // Basename must be present
        final var name = suffixNameWithDot(requireNonNull(emptyToNull(baseName), MISSING_BASENAME_MSG));

        var properties = ConfigurationHelper.resolveFilteredConfigProperties(name, true);

        builder.connectionId(properties.getOrDefault(ConnectionMetadataKeys.ID_KEY, nameWithoutDotSuffix(name)));
        builder.description(properties.get(ConnectionMetadataKeys.DESCRIPTION_KEY));
        builder.serviceUrl(properties.get(ConnectionMetadataKeys.URL_KEY));

        builder.disableHostNameVerification(parseBoolean(
                properties.getOrDefault(ConnectionMetadataKeys.TRANSPORT_DISABLE_HOSTNAME_VALIDATION, "false")));

        Optional.ofNullable(properties.get(ConnectionMetadataKeys.TRACING))
                .ifPresent(value -> builder.tracingEnabled(parseBoolean(value)));

        builder.connectionType(ConnectionType.resolveFrom(
                properties.getOrDefault(ConnectionMetadataKeys.TYPE_KEY, ConnectionType.UNDEFINED.name())));

        handleAuthentication(name, failOnInvalidConfiguration, builder, properties);

        builder.keyStoreInfo(getKeystoreInformation(properties).orElse(null));
        builder.trustStoreInfo(getTruststoreInformation(properties).orElse(null));

        getPositiveLong(name + ConnectionMetadataKeys.CONNECTION_TIMEOUT,
                properties.get(ConnectionMetadataKeys.CONNECTION_TIMEOUT), failOnInvalidConfiguration)
                .ifPresent(builder::connectionTimeout);

        getPositiveLong(name + ConnectionMetadataKeys.READ_TIMEOUT, properties.get(ConnectionMetadataKeys.READ_TIMEOUT),
                failOnInvalidConfiguration).ifPresent(builder::readTimeout);

        builder.proxyHost(properties.get(ConnectionMetadataKeys.PROXY_HOST));
        getPositiveInt(name + ConnectionMetadataKeys.PROXY_PORT, properties.get(ConnectionMetadataKeys.PROXY_PORT),
                failOnInvalidConfiguration).ifPresent(builder::proxyPort);

        final var meta = builder.build();
        meta.getContextMap().putAll(ConfigurationHelper.getFilteredPropertyMap(properties,
                suffixNameWithDot(ConnectionMetadataKeys.CONFIG_KEY), true));
        log.debug("Created Connection Metadata '{}'", meta);
        if (!failOnInvalidConfiguration) {
            return meta;
        }

        try {
            meta.validate();
            return meta;
        } catch (final ConnectionConfigurationException e) {
            log.warn(UNABLE_TO_CONSTRUCT_MSG + e.getMessage());
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    private static String nameWithoutDotSuffix(final String baseName) {
        if (baseName.endsWith(".")) {
            return baseName.substring(0, baseName.length() - 1);
        }
        return baseName;
    }

    @SuppressWarnings("squid:S1301") // We will use the switch soon, Delete this if the switch is
                                     // extended
    private static void handleAuthentication(final String baseName, final boolean failOnInvalidConfiguration,
            final ConnectionMetadataBuilder builder, final Map<String, String> filteredProperties) {
        log.trace("Determining AuthenticationType for '{}'", baseName);
        // Determine Authentication
        final var authenticationType = AuthenticationType.resolveFrom(baseName, filteredProperties);
        builder.authenticationType(authenticationType);
        log.debug("Detected AuthenticationType '{}' for baseName '{}'", authenticationType, baseName);
        switch (authenticationType) {
        case BASIC:
            final var userName = filteredProperties.get(ConnectionMetadataKeys.AUTH_BASIC_USER_NAME);
            if (MoreStrings.isEmpty(userName)) {
                handleMissingProperty(suffixNameWithDot(baseName) + ConnectionMetadataKeys.AUTH_BASIC_USER_NAME,
                        MISSING_BASICAUTH_CONFIG_MSG + "Username", failOnInvalidConfiguration);
            }
            final var password = filteredProperties.get(ConnectionMetadataKeys.AUTH_BASIC_USER_PASSWORD);
            if (MoreStrings.isEmpty(password)) {
                handleMissingProperty(suffixNameWithDot(baseName) + ConnectionMetadataKeys.AUTH_BASIC_USER_PASSWORD,
                        MISSING_BASICAUTH_CONFIG_MSG + "Password", failOnInvalidConfiguration);
            }
            builder.loginCredentials(LoginCredentials.builder().username(userName).password(password).build());
            break;
        case TOKEN_APPLICATION:
            final var key = filteredProperties.get(ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_KEY);
            if (MoreStrings.isEmpty(key)) {
                handleMissingProperty(suffixNameWithDot(baseName) + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_KEY,
                        MISSING_TOKEN_CONFIG_MSG + "Key", failOnInvalidConfiguration);
            }
            final var token = filteredProperties.get(ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_TOKEN);
            if (MoreStrings.isEmpty(token)) {
                handleMissingProperty(suffixNameWithDot(baseName) + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_TOKEN,
                        MISSING_TOKEN_CONFIG_MSG + "Token", failOnInvalidConfiguration);
            }
            builder.tokenResolver(new StaticTokenResolver(key, token));
            break;
        default:
            break;
        }
    }

    /**
     * @param name to be suffixed, must not be null
     * @return the given name suffixed with a dot
     */
    public static String suffixNameWithDot(final String name) {
        return name.endsWith(".") ? name : name + ".";
    }

    private static void handleMissingProperty(final String propertyName, final String exceptionMessage,
            final boolean failOnInvalidConfiguration) {
        log.warn(MISSING_CONFIG_MSG, propertyName);
        if (failOnInvalidConfiguration) {
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    private static Optional<KeyStoreProvider> getTruststoreInformation(final Map<String, String> filteredProperties) {
        log.trace("Resolving TruststoreInformation");
        final var truststoreLocation = filteredProperties.get(ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_LOCATION);
        final var truststorePassword = extractFirstKeyValue(filteredProperties,
                ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_PASSWORD);

        if (null == truststoreLocation || !truststorePassword.isPresent()) {
            if (log.isDebugEnabled()) {
                if (MoreStrings.isEmpty(truststoreLocation)) {
                    log.debug(RETURNING_EMPTY_VALUE, "trust-store-location");
                }
                if (!truststorePassword.isPresent()) {
                    log.debug(RETURNING_EMPTY_VALUE, "trust-store-password");
                }
            }
            return Optional.empty();
        }

        if (!MorePaths.checkReadablePath(MorePaths.getRealPathSafely(truststoreLocation), false, true)) {
            return Optional.empty();
        }

        return Optional.of(KeyStoreProvider.builder().keyStoreType(KeyStoreType.TRUST_STORE)
                .location(new File(truststoreLocation)).storePassword(truststorePassword.get()).build());
    }

    private static Optional<KeyStoreProvider> getKeystoreInformation(final Map<String, String> filteredProperties) {
        log.trace("Resolving KeystoreInformation");
        final var keystoreLocation = extractKeystoreLocation(filteredProperties);
        final var keystorePassword = extractFirstKeyValue(filteredProperties,
                ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_PASSWORD,
                ConnectionMetadataKeys.TRANSPORT_KEYSTORE_KEYPASSWORD);

        if (!keystoreLocation.isPresent() || !keystorePassword.isPresent()) {
            if (log.isDebugEnabled()) {
                if (!keystoreLocation.isPresent()) {
                    log.debug(RETURNING_EMPTY_VALUE, "key-store-location");
                }
                if (!keystorePassword.isPresent()) {
                    log.debug(RETURNING_EMPTY_VALUE, "key-store-password");
                }
            }
            return Optional.empty();
        }

        final var keyPassword = extractFirstKeyValue(filteredProperties,
                ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_KEYPASSWORD,
                ConnectionMetadataKeys.TRANSPORT_KEYSTORE_KEYPASSWORD).orElse(keystorePassword.get());

        return Optional.of(KeyStoreProvider.builder().keyStoreType(KeyStoreType.KEY_STORE)
                .location(new File(keystoreLocation.get())).storePassword(keystorePassword.get())
                .keyPassword(keyPassword).build());
    }

    private static Optional<String> extractKeystoreLocation(final Map<String, String> filteredProperties) {
        final var keystoreLocation = filteredProperties.get(ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_LOCATION);
        if (null != keystoreLocation) {
            return Optional.of(keystoreLocation);
        }

        return Optional.ofNullable(filteredProperties.get(ConnectionMetadataKeys.TRANSPORT_KEYSTORE_LOCATION));
    }

    private static Optional<String> extractFirstKeyValue(final Map<String, String> filteredProperties,
            final String... keys) {
        for (final String key : keys) {
            final var value = filteredProperties.get(key);
            if (!MoreStrings.isEmpty(value)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    /**
     * @param key                        entire configuration key
     * @param value                      to be parsed
     * @param failOnInvalidConfiguration boolean indicating if an
     *                                   {@link IllegalArgumentException} should be
     *                                   thrown, if the value cannot be parsed
     *
     * @return optional long
     * @throws IllegalArgumentException if the value cannot be parsed
     */
    private static Optional<Long> getPositiveLong(final String key, final String value,
            final boolean failOnInvalidConfiguration) {
        if (null != value) {
            try {
                return Optional.of(Long.parseUnsignedLong(value.trim()));
            } catch (final NumberFormatException e) {
                if (failOnInvalidConfiguration) {
                    throw new IllegalArgumentException(MoreStrings.lenientFormat(INVALID_NUMBER_VALUE, key, value), e);
                }
                log.error(e, INVALID_NUMBER_VALUE, key, value);
            }
        }
        return Optional.empty();
    }

    private static Optional<Integer> getPositiveInt(final String key, final String value,
            final boolean failOnInvalidConfiguration) {
        if (null != value) {
            try {
                return Optional.of(Integer.parseUnsignedInt(value.trim()));
            } catch (final NumberFormatException e) {
                if (failOnInvalidConfiguration) {
                    throw new IllegalArgumentException(MoreStrings.lenientFormat(INVALID_NUMBER_VALUE, key, value), e);
                }
                log.error(e, INVALID_NUMBER_VALUE, key, value);
            }
        }
        return Optional.empty();
    }
}
