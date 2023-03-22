package de.cuioss.portal.configuration.impl.producer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.connections.TokenResolver;
import de.cuioss.portal.configuration.connections.impl.AuthenticationType;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadataKeys;
import de.cuioss.portal.configuration.connections.impl.ConnectionType;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfiguration;
import de.cuioss.portal.configuration.impl.support.PortalConfigurationMock;
import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;

@EnableAutoWeld
@EnablePortalConfiguration
@AddBeanClasses({
    ConnectionMetadataProducer.class,
    ConnectionMetadata.class })
@EnableTestLogger
class ConnectionMetadataProducerTest {

    private static final String TOKEN_VALUE = "tokenValue";
    private static final String TOKEN_KEY = "tokenKey";
    private static final String BASE_NAME = "test.metadata";
    private static final String BASE_NAME_SUFFIXED = BASE_NAME + ".";

    private static final String TRUSTSTORE_PASSWORD = "initinit";
    private static final String KEYSTORE_PASSWORD = "initinit";
    private static final String PASSWORD3 = "password3";
    private static final String USER = "user";
    private static final String CONNECTION_URL = "http://de.icw-global.com/";
    private static final String DESCRIPTION = "Some description";
    private String truststoreLocation;
    private String keystoreLocation;

    private static final String PROPERTY_TRUSTSTORE_LOCATION = "javax.net.ssl.trustStore";
    private static final String PROPERTY_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";

    private static final String PROPERTY_KEYSTORE_LOCATION = "javax.net.ssl.keyStore";
    private static final String PROPERTY_KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";

    @Inject
    @ConfigAsConnectionMetadata(baseName = BASE_NAME)
    private Provider<ConnectionMetadata> metadataProvider;

    @Inject
    @ConfigAsConnectionMetadata(baseName = BASE_NAME, failOnInvalidConfiguration = false)
    private Provider<ConnectionMetadata> metadataNotFailProvider;

    @Inject
    @PortalConfigurationSource
    private PortalConfigurationMock configuration;

    @BeforeEach
    void before() {
        configuration.initializeConfigurationSystem();
        final var testResources = Paths.get("src/test/resources");
        truststoreLocation = testResources.resolve("host.keystore").toFile().getAbsolutePath();
        keystoreLocation = testResources.resolve("ca.keystore").toFile().getAbsolutePath();
        systemKeystores();
    }

    @AfterEach
    void after() {
        configuration.clear();
        configuration.fireEvent();
    }

    @Test
    void shouldNotFailOnEmptyConfiguration() {
        assertNotNull(metadataNotFailProvider.get());
    }

    @Test
    void shouldFailOnEmptyConfiguration() {
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
    }

    @Test
    void shouldNotFailWithDefaultConfiguration() {
        defaultConfig();
        configuration.fireEvent();
        assertDoesNotThrow(() -> metadataProvider.get());
    }

    @Test
    void shouldHandleCertificationConfiguration() {
        certificateConfig();
        configuration.fireEvent();
        final var metadata = metadataProvider.get();
        assertEquals(AuthenticationType.CERTIFICATE, metadata.getAuthenticationType());
    }

    @Test
    void ambiguousConfig() {
        certificateConfig();
        basicAuthConfig();
        applicationTokenConfig();
        configuration.fireEvent();

        // config contains basic auth, certificate and token
        var metadata = metadataProvider.get();

        // highest prio is token.
        assertEquals(AuthenticationType.TOKEN_APPLICATION, metadata.getAuthenticationType());
        assertTokenConfig(metadata.getTokenResolver());
        assertNull(metadata.getLoginCredentials(), "no basic auth should be provided");
        assertEquals(TRUSTSTORE_PASSWORD, metadata.getTrustStoreInfo().getStorePassword());
        assertEquals(KEYSTORE_PASSWORD, metadata.getKeyStoreInfo().getStorePassword());

        // removing token config
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_KEY);
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_TOKEN);
        configuration.fireEvent();

        // now, basic and certificate config should be provided
        metadata = metadataProvider.get();
        assertNull(metadata.getTokenResolver());
        assertEquals(AuthenticationType.BASIC, metadata.getAuthenticationType());
        assertNotNull(metadata.getLoginCredentials());
        assertEquals(USER, metadata.getLoginCredentials().getUsername());
        assertEquals(PASSWORD3, metadata.getLoginCredentials().getPassword());
    }

    @Test
    void certAndEmptyBasicAuth() {
        certificateConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_NAME, "");
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_PASSWORD, "");
        configuration.fireEvent();
        final var metadata = metadataProvider.get();
        assertEquals(AuthenticationType.CERTIFICATE, metadata.getAuthenticationType());
    }

    @Test
    void basicAndEmptyCertAuth() {
        basicAuthConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_LOCATION, "");
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_PASSWORD, "");
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_KEYPASSWORD, "");
        configuration.fireEvent();
        final var metadata = metadataProvider.get();
        assertEquals(AuthenticationType.BASIC, metadata.getAuthenticationType());
    }

    @Test
    void shouldConstructOnHappyCase() {
        defaultConfig();
        configuration.fireEvent();
        final var connectionMetadata = metadataProvider.get();
        assertNotNull(connectionMetadata);
        assertEquals(AuthenticationType.BASIC,
                connectionMetadata.getAuthenticationType());
        assertEquals(USER, connectionMetadata.getLoginCredentials().getUsername());
        assertEquals(PASSWORD3, connectionMetadata.getLoginCredentials().getPassword());
        assertEquals(CONNECTION_URL, connectionMetadata.getServiceUrl());
        assertEquals(DESCRIPTION, connectionMetadata.getDescription());
        assertEquals(BASE_NAME, connectionMetadata.getConnectionId());
        assertEquals(ConnectionType.UNDEFINED, connectionMetadata.getConnectionType());
        assertTrue(connectionMetadata.isLoginCredentialsNecessary());
    }

    @Test
    void shouldHandleValidApplicationToken() {
        applicationTokenConfig();
        configuration.fireEvent();
        final var connectionMetadata = metadataProvider.get();
        assertNotNull(connectionMetadata);
        assertEquals(AuthenticationType.TOKEN_APPLICATION, connectionMetadata.getAuthenticationType());
        assertTokenConfig(connectionMetadata.getTokenResolver());
    }

    @Test
    void shouldFailApplicationTokenWithMissingKey() {
        applicationTokenConfig();
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_KEY);
        configuration.fireEvent();
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
    }

    @Test
    void shouldNotFailOnMissingKeyStoreConfigLocation() {
        certificateConfig();
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_LOCATION);
        configuration.fireEvent();
        assertDoesNotThrow(() -> metadataProvider.get());
        assertNull(metadataProvider.get().getKeyStoreInfo());
    }

    @Test
    void shouldNotFailOnMissingAuthorizationType() {
        authTypeConfig();
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE);
        configuration.fireEvent();
        assertDoesNotThrow(() -> {
            metadataProvider.get();
        }, "Should fall back to auth type NONE instead");
    }

    @Test
    void shouldNotFailOnInvalidAuthorizationType() {
        authTypeConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE, "bla");
        configuration.fireEvent();
        assertDoesNotThrow(() -> {
            metadataProvider.get();
        }, "Should fall back to auth type NONE instead");
    }

    @Test
    void shouldHandleInvalidAuthorizationType() {
        authTypeConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE, "bla");
        configuration.fireEvent();
        assertEquals(AuthenticationType.NONE, metadataNotFailProvider.get().getAuthenticationType());
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN, "Portal-131");
    }

    @Test
    void shouldUseDefaultOnMissingConnectionType() {
        defaultConfig();
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TYPE_KEY);
        configuration.fireEvent();
        assertEquals(ConnectionType.UNDEFINED, metadataProvider.get().getConnectionType());
    }

    @Test
    void shouldUseDefaultOnInvalidConnectionType() {
        defaultConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TYPE_KEY, "bla");
        configuration.fireEvent();
        assertEquals(ConnectionType.UNDEFINED, metadataProvider.get().getConnectionType());
        LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN, "Portal-131");
    }

    @Test
    void shouldFailOnInvalidAuthorizationConfiguration() {
        defaultConfig();
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_NAME);
        configuration.fireEvent();
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
    }

    @Test
    void shouldHandleInvalidAuthorizationConfiguration() {
        defaultConfig();
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_NAME);
        configuration.fireEvent();
        assertEquals(AuthenticationType.BASIC, metadataNotFailProvider.get().getAuthenticationType());
    }

    @Test
    void shouldFailOnMissingServiceUrl() {
        defaultConfig();
        configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY);
        configuration.fireEvent();
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
    }

    @Test
    void shouldHandleInvalidSystemTruststoreLocation() {
        defaultConfig();
        configuration.remove(PROPERTY_TRUSTSTORE_LOCATION);
        configuration.fireEvent();
        var connectionMetadata = metadataProvider.get();
        assertNotNull(connectionMetadata);
        assertNull(connectionMetadata.getTrustStoreInfo());
        assertNull(connectionMetadata.getKeyStoreInfo());

        configuration.put(PROPERTY_TRUSTSTORE_LOCATION, "not/there");
        configuration.fireEvent();
        connectionMetadata = metadataProvider.get();
        assertNotNull(connectionMetadata);
        assertNull(connectionMetadata.getTrustStoreInfo());
        assertNull(connectionMetadata.getKeyStoreInfo());

        configuration.put(PROPERTY_TRUSTSTORE_LOCATION, "target");
        configuration.fireEvent();
        connectionMetadata = metadataProvider.get();
        assertNotNull(connectionMetadata);
        assertNull(connectionMetadata.getTrustStoreInfo());
        assertNull(connectionMetadata.getKeyStoreInfo());
    }

    @Test
    void shouldHandleDisableHostnameVerification() {
        final var key = BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRANSPORT_DISABLE_HOSTNAME_VALIDATION;
        defaultConfig();
        configuration.fireEvent(key, "true");
        assertTrue(metadataProvider.get().isDisableHostNameVerification());

        configuration.fireEvent(key, "false");
        assertFalse(metadataProvider.get().isDisableHostNameVerification());

        // Should default to false
        configuration.remove(key);
        configuration.fireEvent();
        assertFalse(metadataProvider.get().isDisableHostNameVerification());
    }

    @Test
    void shouldHandleInvalidConnectionType() {
        defaultConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TYPE_KEY, "bla");
        configuration.fireEvent();
        assertEquals(ConnectionType.UNDEFINED, metadataNotFailProvider.get().getConnectionType());
    }

    @Test
    void shouldHandleInvalidConnectionTimeout() {
        defaultConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.CONNECTION_TIMEOUT, "b00m");
        configuration.fireEvent();
        assertEquals(0, metadataNotFailProvider.get().getConnectionTimeout());
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.CONNECTION_TIMEOUT, "-666");
        configuration.fireEvent();
        assertEquals(0, metadataNotFailProvider.get().getConnectionTimeout());
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
    }

    @Test
    void shouldHandleValidConnectionTimeout() {
        defaultConfig();
        assertEquals(0, metadataNotFailProvider.get().getConnectionTimeout());
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.CONNECTION_TIMEOUT, "666666");
        configuration.fireEvent();
        assertEquals(666666, metadataProvider.get().getConnectionTimeout());
        assertEquals(TimeUnit.SECONDS, metadataProvider.get().getConnectionTimeoutUnit());
    }

    @Test
    void shouldHandleValidReadTimeout() {
        defaultConfig();
        assertEquals(0, metadataNotFailProvider.get().getReadTimeout());
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.READ_TIMEOUT, "666666");
        configuration.fireEvent();
        assertEquals(666666, metadataProvider.get().getReadTimeout());
        assertEquals(TimeUnit.SECONDS, metadataProvider.get().getReadTimeoutUnit());
    }

    @Test
    void shouldHandleInvalidReadTimeout() {
        defaultConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.READ_TIMEOUT, "b00m");
        configuration.fireEvent();
        assertEquals(0, metadataNotFailProvider.get().getReadTimeout());
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.READ_TIMEOUT, "-666");
        configuration.fireEvent();
        assertEquals(0, metadataNotFailProvider.get().getReadTimeout());
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
    }

    @Test
    void shouldHandleConfigProperties() {
        defaultConfig();
        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE,
                AuthenticationType.NONE.name());
        configuration.fireEvent();
        assertTrue(metadataProvider.get().getContextMap().isEmpty());

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.CONFIG_KEY + ".hello", "world");
        configuration.fireEvent();
        assertFalse(metadataProvider.get().getContextMap().isEmpty());
        assertEquals("world", metadataProvider.get().getContextMap().get("hello"));
    }

    @Test
    void shouldDetermineAuthenticationTypeForSubkey() {
        defaultConfig();
        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE, "");
        configuration.fireEvent();

        assertEquals(AuthenticationType.BASIC, metadataProvider.get().getAuthenticationType());
    }

    @Test
    void shouldHandleTracing() {
        defaultConfig();
        configuration.fireEvent();
        assertTrue(metadataProvider.get().isTracingEnabled());

        configuration.fireEvent(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRACING, "false");
        assertFalse(metadataProvider.get().isTracingEnabled());
    }

    @Test
    void shouldHandleProxy() {
        defaultConfig();
        configuration.fireEvent();
        assertNull(metadataProvider.get().getProxyHost());

        configuration.fireEvent(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.PROXY_HOST, "proxy.example.com");
        configuration.fireEvent(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.PROXY_PORT, "123");
        assertEquals("proxy.example.com", metadataProvider.get().getProxyHost());
        assertEquals(123, metadataProvider.get().getProxyPort());
    }

    @Test
    void shouldHandleInvalidProxy() {
        defaultConfig();
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.PROXY_HOST, "proxy.example.com");
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.PROXY_PORT, "b00m");
        configuration.fireEvent();
        assertNull(metadataNotFailProvider.get().getProxyPort());
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.PROXY_PORT, "-66");
        configuration.fireEvent();
        assertNull(metadataNotFailProvider.get().getProxyPort());
        assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
    }

    private void authTypeConfig() {
        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY,
                CONNECTION_URL);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY,
                DESCRIPTION);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE,
                "none");
    }

    private void defaultConfig() {

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_NAME,
                USER);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_PASSWORD,
                PASSWORD3);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY,
                CONNECTION_URL);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY,
                DESCRIPTION);
    }

    private void applicationTokenConfig() {

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_KEY,
                TOKEN_KEY);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_TOKEN,
                TOKEN_VALUE);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY,
                CONNECTION_URL);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY,
                DESCRIPTION);
    }

    private void certificateConfig() {

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_LOCATION,
                truststoreLocation);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_PASSWORD,
                TRUSTSTORE_PASSWORD);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_LOCATION,
                keystoreLocation);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_PASSWORD,
                KEYSTORE_PASSWORD);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_KEYPASSWORD,
                PASSWORD3);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY,
                CONNECTION_URL);

        configuration.put(
                BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY,
                DESCRIPTION);
    }

    private void basicAuthConfig() {
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_NAME, USER);
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_PASSWORD, PASSWORD3);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY, CONNECTION_URL);
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY, DESCRIPTION);
    }

    private void systemKeystores() {
        configuration.put(PROPERTY_TRUSTSTORE_LOCATION, truststoreLocation);
        configuration.put(PROPERTY_TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSWORD);
        configuration.put(PROPERTY_KEYSTORE_LOCATION, keystoreLocation);
        configuration.put(PROPERTY_KEYSTORE_PASSWORD, KEYSTORE_PASSWORD);
    }

    void assertTokenConfig(final TokenResolver tokenResolver) {
        assertNotNull(tokenResolver);
        assertEquals(TOKEN_KEY, tokenResolver.getKey());
        assertEquals(TOKEN_VALUE, tokenResolver.resolve());
    }
}
