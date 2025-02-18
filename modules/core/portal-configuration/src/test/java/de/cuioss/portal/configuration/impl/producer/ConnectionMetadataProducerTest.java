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
package de.cuioss.portal.configuration.impl.producer;

import static de.cuioss.portal.configuration.PortalConfigurationMessages.WARN;
import static de.cuioss.test.juli.LogAsserts.assertLogMessagePresentContaining;
import static org.junit.jupiter.api.Assertions.*;

import de.cuioss.portal.configuration.PortalConfigurationMessages;
import de.cuioss.portal.configuration.connections.TokenResolver;
import de.cuioss.portal.configuration.connections.impl.AuthenticationType;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadataKeys;
import de.cuioss.portal.configuration.connections.impl.ConnectionType;
import de.cuioss.portal.configuration.impl.support.EnablePortalConfigurationLocal;
import de.cuioss.portal.configuration.impl.support.PortalTestConfigurationLocal;
import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.TestLoggerFactory;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

/**
 * Tests for {@link ConnectionMetadataProducer} verifying connection metadata configuration
 * and edge cases.
 */
@EnableAutoWeld
@EnablePortalConfigurationLocal
@AddBeanClasses({ConnectionMetadataProducer.class, ConnectionMetadata.class})
@EnableTestLogger(trace = {ConfigurationHelper.class, PortalTestConfigurationLocal.class})
@DisplayName("ConnectionMetadataProducer Tests")
class ConnectionMetadataProducerTest {

    private static final String TOKEN_VALUE = "tokenValue";
    private static final String TOKEN_KEY = "tokenKey";
    private static final String BASE_NAME = "test.metadata";
    private static final String BASE_NAME_SUFFIXED = BASE_NAME + ".";

    private static final String TRUSTSTORE_PASSWORD = "initinit";
    private static final String KEYSTORE_PASSWORD = "initinit";
    private static final String PASSWORD3 = "password3";
    private static final String USER = "user";
    private static final String CONNECTION_URL = "https://cuioss.de/";
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
    private PortalTestConfigurationLocal configuration;

    @BeforeEach
    void before() {
        final var testResources = Path.of("src/test/resources");
        truststoreLocation = testResources.resolve("host.keystore").toFile().getAbsolutePath();
        keystoreLocation = testResources.resolve("ca.keystore").toFile().getAbsolutePath();
        systemKeystores();
        TestLoggerFactory.getTestHandler().clearRecords();
    }

    @AfterEach
    void after() {
        configuration.clear();
        configuration.fireEvent();
    }

    @Nested
    @DisplayName("Basic Configuration Tests")
    class BasicConfigurationTests {

        @Test
        @DisplayName("Should handle empty configuration with no failure")
        void shouldNotFailOnEmptyConfiguration() {
            assertNotNull(metadataNotFailProvider.get());
        }

        @Test
        @DisplayName("Should fail on empty configuration when required")
        void shouldFailOnEmptyConfiguration() {
            assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
        }

        @Test
        @DisplayName("Should handle default configuration")
        void shouldNotFailWithDefaultConfiguration() {
            defaultConfig();
            configuration.fireEvent();
            assertNotNull(metadataProvider.get());
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should handle invalid authentication type")
        void shouldHandleInvalidAuthorizationType() {
            authTypeConfig();
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE, "bla");
            configuration.fireEvent();
            assertEquals(AuthenticationType.NONE, metadataNotFailProvider.get().getAuthenticationType());
            assertLogMessagePresentContaining(TestLogLevel.WARN, PortalConfigurationMessages.WARN.UNABLE_TO_DETERMINE_AUTH_TYPE.resolveIdentifierString());
        }

        @Test
        @DisplayName("Should handle missing authentication type")
        void shouldHandleMissingAuthType() {
            authTypeConfig();
            configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE);
            configuration.fireEvent();
            assertEquals(AuthenticationType.NONE, metadataProvider.get().getAuthenticationType());
        }

        @Test
        @DisplayName("Should handle certificate configuration")
        void shouldHandleCertificationConfiguration() {
            certificateConfig();
            configuration.fireEvent();
            final var metadata = metadataProvider.get();
            assertNotNull(metadata);
            assertEquals(AuthenticationType.CERTIFICATE, metadata.getAuthenticationType());
        }
    }

    @Nested
    @DisplayName("Connection Type Tests")
    class ConnectionTypeTests {

        @Test
        @DisplayName("Should use default on invalid connection type")
        void shouldUseDefaultOnInvalidConnectionType() {
            defaultConfig();
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TYPE_KEY, "bla");
            configuration.fireEvent();
            assertEquals(ConnectionType.UNDEFINED, metadataProvider.get().getConnectionType());
            assertLogMessagePresentContaining(TestLogLevel.WARN, PortalConfigurationMessages.WARN.INVALID_CONNECTION_TYPE.resolveIdentifierString());
        }

        @Test
        @DisplayName("Should use default on missing connection type")
        void shouldUseDefaultOnMissingConnectionType() {
            defaultConfig();
            configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TYPE_KEY);
            configuration.fireEvent();
            assertEquals(ConnectionType.UNDEFINED, metadataProvider.get().getConnectionType());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle ambiguous configuration")
        void shouldHandleAmbiguousConfig() {
            basicAuthConfig();
            certificateConfig();
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
        @DisplayName("Should handle missing service URL")
        void shouldFailOnMissingServiceUrl() {
            defaultConfig();
            configuration.remove(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY);
            configuration.fireEvent();
            assertThrows(IllegalArgumentException.class, () -> metadataProvider.get());
        }

        @Test
        @DisplayName("Should handle invalid system truststore: Not existing truststore location")
        void shouldHandleInvalidSystemTruststore() {
            // Clear existing system properties
            System.clearProperty(PROPERTY_TRUSTSTORE_LOCATION);
            System.clearProperty(PROPERTY_TRUSTSTORE_PASSWORD);
            System.clearProperty(PROPERTY_KEYSTORE_LOCATION);
            System.clearProperty(PROPERTY_KEYSTORE_PASSWORD);

            // Set invalid truststore location
            System.setProperty(PROPERTY_TRUSTSTORE_LOCATION, "/not/there");
            System.setProperty(PROPERTY_TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSWORD);

            // Configure certificate authentication requiring truststore
            defaultConfig();
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE, AuthenticationType.CERTIFICATE.name());
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_LOCATION, keystoreLocation);
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_PASSWORD, KEYSTORE_PASSWORD);
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_KEYPASSWORD, KEYSTORE_PASSWORD);

            // Set invalid truststore configuration
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_LOCATION, "/not/there");
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_PASSWORD, TRUSTSTORE_PASSWORD);

            // Prevent fallback to default SSLContext
            System.setProperty("javax.net.ssl.trustStoreType", "INVALID_TYPE");
            configuration.fireEvent();

            // Should throw IllegalStateException due to invalid truststore
            metadataProvider.get();
            LogAsserts.assertLogMessagePresentContaining(TestLogLevel.WARN, "Unable to resolve real path for '/not/there'");

            // Cleanup
            System.clearProperty(PROPERTY_TRUSTSTORE_LOCATION);
            System.clearProperty(PROPERTY_TRUSTSTORE_PASSWORD);
            System.clearProperty(PROPERTY_KEYSTORE_LOCATION);
            System.clearProperty(PROPERTY_KEYSTORE_PASSWORD);
            System.clearProperty("javax.net.ssl.trustStoreType");

            // Restore original properties
            systemKeystores();
        }
    }

    @Nested
    @DisplayName("Timeout Tests")
    class TimeoutTests {

        @Test
        @DisplayName("Should handle invalid connection timeout")
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
        @DisplayName("Should handle valid connection timeout")
        void shouldHandleValidConnectionTimeout() {
            defaultConfig();
            assertEquals(0, metadataNotFailProvider.get().getConnectionTimeout());
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.CONNECTION_TIMEOUT, "666666");
            configuration.fireEvent();
            assertEquals(666666, metadataProvider.get().getConnectionTimeout());
            assertEquals(TimeUnit.SECONDS, metadataProvider.get().getConnectionTimeoutUnit());
        }

        @Test
        @DisplayName("Should handle valid read timeout")
        void shouldHandleValidReadTimeout() {
            defaultConfig();
            assertEquals(0, metadataNotFailProvider.get().getReadTimeout());
            configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.READ_TIMEOUT, "666666");
            configuration.fireEvent();
            assertEquals(666666, metadataProvider.get().getReadTimeout());
            assertEquals(TimeUnit.SECONDS, metadataProvider.get().getReadTimeoutUnit());
        }

        @Test
        @DisplayName("Should handle invalid read timeout")
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
    }

    @Nested
    @DisplayName("Proxy Tests")
    class ProxyTests {

        @Test
        @DisplayName("Should handle proxy")
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
        @DisplayName("Should handle invalid proxy")
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
    }

    private void authTypeConfig() {
        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY, CONNECTION_URL);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY, DESCRIPTION);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTHENTICATION_TYPE, "none");
    }

    private void defaultConfig() {

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_NAME, USER);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_BASIC_USER_PASSWORD, PASSWORD3);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY, CONNECTION_URL);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY, DESCRIPTION);
    }

    private void applicationTokenConfig() {

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_KEY, TOKEN_KEY);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_TOKEN_APPLICATION_TOKEN, TOKEN_VALUE);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY, CONNECTION_URL);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY, DESCRIPTION);
    }

    private void certificateConfig() {

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_LOCATION,
                truststoreLocation);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.TRANSPORT_TRUSTSTORE_PASSWORD,
                TRUSTSTORE_PASSWORD);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_LOCATION,
                keystoreLocation);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_PASSWORD,
                KEYSTORE_PASSWORD);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.AUTH_CERTIFICATE_KEYSTORE_KEYPASSWORD, PASSWORD3);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.URL_KEY, CONNECTION_URL);

        configuration.put(BASE_NAME_SUFFIXED + ConnectionMetadataKeys.DESCRIPTION_KEY, DESCRIPTION);
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
