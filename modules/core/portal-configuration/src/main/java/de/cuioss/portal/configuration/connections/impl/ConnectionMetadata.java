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
package de.cuioss.portal.configuration.connections.impl;

import de.cuioss.portal.configuration.connections.TokenResolver;
import de.cuioss.portal.configuration.connections.exception.ConnectionConfigurationException;
import de.cuioss.portal.configuration.connections.exception.ErrorReason;
import de.cuioss.tools.logging.CuiLogger;
import de.cuioss.tools.net.ssl.KeyStoreProvider;
import de.cuioss.tools.string.MoreStrings;
import de.cuioss.uimodel.application.LoginCredentials;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.Serial;
import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static de.cuioss.portal.configuration.PortalConfigurationMessages.ERROR.SSL_CONTEXT_CREATION_FAILED;

/**
 * Represents metadata for configuring and managing a connection to an external service.
 * This class provides configuration for various aspects of a connection including:
 * <ul>
 *   <li>Authentication (basic auth, certificates, tokens)</li>
 *   <li>SSL/TLS configuration with custom keystores and truststores</li>
 *   <li>Connection timeouts and proxy settings</li>
 *   <li>Context data storage for runtime information</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@EqualsAndHashCode(exclude = {"loginCredentials"})
@ToString(exclude = {"loginCredentials"})
@Builder(toBuilder = true)
public class ConnectionMetadata implements Serializable {

    /**
     * Builder stub to make Lombok-generated builder visible to Javadoc.
     * Lombok will augment this class with the generated builder methods.
     */
    @SuppressWarnings("java:S1610") // Not a functional interface
    public static class ConnectionMetadataBuilder {
    }

    private static final CuiLogger LOGGER = new CuiLogger(ConnectionMetadata.class);

    @Serial
    private static final long serialVersionUID = -8168073688801716947L;

    /**
     * Runtime context information associated with this connection.
     * This map is thread-safe and can store any serializable key-value pairs.
     */
    @Builder.Default
    @Getter
    private final Map<Serializable, Serializable> contextMap = new ConcurrentHashMap<>();

    /**
     * Credentials for basic authentication.
     * Only used when {@link #authenticationType} is {@link AuthenticationType#BASIC}.
     */
    @Getter
    @Setter
    private LoginCredentials loginCredentials;

    /**
     * Token resolver for token-based authentication.
     * Required when {@link #authenticationType} is a token type.
     */
    @Getter
    @Setter
    private TokenResolver tokenResolver;

    /**
     * Keystore configuration for client certificates.
     * Used for client authentication when {@link #authenticationType} is
     * {@link AuthenticationType#CERTIFICATE} or for SSL/TLS client authentication.
     */
    @Getter
    @Setter
    private KeyStoreProvider keyStoreInfo;

    /**
     * Truststore configuration for SSL/TLS server authentication.
     * Used to validate server certificates in secure connections.
     */
    @Getter
    @Setter
    private KeyStoreProvider trustStoreInfo;

    /**
     * Cached SSL context created from keystore and truststore configurations.
     * Lazily initialized when first requested via {@link #resolveSSLContext()}.
     */
    private transient SSLContext sslContext;

    /**
     * The URL of the service to connect to.
     * This is a required field and must be set before calling {@link #validate()}.
     */
    @Getter
    @Setter
    private String serviceUrl;

    /**
     * The type of authentication to use for this connection.
     * This is a required field and must be set before calling {@link #validate()}.
     */
    @Getter
    @Setter
    private AuthenticationType authenticationType;

    /**
     * The type of connection (e.g., REST, JMX, DATABASE).
     * This is a required field and must be set before calling {@link #validate()}.
     */
    @Getter
    @Setter
    private ConnectionType connectionType;

    /**
     * Human-readable description of this connection's purpose.
     * Optional field for documentation purposes.
     */
    @Getter
    @Setter
    private String description;

    /**
     * Unique identifier for this connection configuration.
     * This is a required field and must be set before calling {@link #validate()}.
     */
    @Getter
    @Setter
    private String connectionId;

    /**
     * When true, hostname verification in SSL/TLS connections will be disabled.
     * WARNING: This should only be used in testing environments as it reduces security.
     */
    @Getter
    @Setter
    @Builder.Default
    private boolean disableHostNameVerification = false;

    /**
     * The amount of time to wait when establishing a connection.
     * Used in conjunction with {@link #connectionTimeoutUnit}.
     */
    @Getter
    @Setter
    private long connectionTimeout;

    /**
     * The time unit for the connection timeout value.
     * Defaults to {@link TimeUnit#SECONDS}.
     */
    @Getter
    @Setter
    @Builder.Default
    private TimeUnit connectionTimeoutUnit = TimeUnit.SECONDS;

    /**
     * The amount of time to wait when reading from an established connection.
     * Used in conjunction with {@link #readTimeoutUnit}.
     */
    @Getter
    @Setter
    private long readTimeout;

    /**
     * The time unit for the read timeout value.
     * Defaults to {@link TimeUnit#SECONDS}.
     */
    @Getter
    @Setter
    @Builder.Default
    private TimeUnit readTimeoutUnit = TimeUnit.SECONDS;

    /**
     * The hostname or IP address of the proxy server.
     * Only used if {@link #proxyPort} is also set to a valid port number.
     */
    @Getter
    @Setter
    private String proxyHost;

    /**
     * The port number of the proxy server.
     * Must be a positive integer if {@link #proxyHost} is set.
     */
    @Getter
    @Setter
    private Integer proxyPort;

    /**
     * Determines if basic authentication credentials are required for this connection.
     *
     * @return true if {@link #authenticationType} is {@link AuthenticationType#BASIC},
     *         false otherwise
     */
    public boolean isLoginCredentialsNecessary() {
        return AuthenticationType.BASIC.equals(getAuthenticationType());
    }

    /**
     * Adds a key-value pair to the connection's context map.
     * This method is thread-safe as it uses a {@link ConcurrentHashMap}.
     *
     * @param key   the key for the context entry, must not be null
     * @param value the value to store, must not be null
     * @return this instance for method chaining
     */
    public ConnectionMetadata contextMapElement(final Serializable key, final Serializable value) {
        contextMap.put(key, value);
        return this;
    }

    /**
     * Creates or retrieves a cached SSL context based on the configured keystores.
     * If creation fails, falls back to the platform default SSL context.
     *
     * @return an SSLContext configured with the connection's keystore and truststore,
     *         or the platform default if configuration fails
     * @throws IllegalStateException if the platform default SSL context cannot be obtained
     */
    public SSLContext resolveSSLContext() {
        if (null == sslContext) {
            try {
                sslContext = resolveOptionalSSLContext().orElse(SSLContext.getDefault());
            } catch (final NoSuchAlgorithmException e) {
                throw new IllegalStateException("Unable to obtain default SSLContext from platform", e);
            }
        }
        return sslContext;
    }

    /**
     * Attempts to create an SSL context from the configured keystores without falling back
     * to platform defaults.
     * This method is useful when you need to handle SSL context
     * creation failures explicitly.
     *
     * @return an Optional containing the created SSLContext, or empty if creation fails
     *         or no keystore/truststore is configured
     */
    public Optional<SSLContext> resolveOptionalSSLContext() {
        LOGGER.debug("Resolving optional SSLContext for connection '%s'", getConnectionId());
        if (null == getTrustStoreInfo() && null == getKeyStoreInfo()) {
            LOGGER.debug("SslTrustStoreInfo is null, using platform-default");
            return Optional.empty();
        }
        LOGGER.debug("Create custom SSLContext for connection '%s'", getConnectionId());
        try {
            final var contextBuilder = SSLContexts.custom();
            if (null != getTrustStoreInfo()) {
                var trustStore = getTrustStoreInfo().resolveKeyStore();
                if (trustStore.isPresent()) {
                    contextBuilder.loadTrustMaterial(trustStore.get(), null);
                    LOGGER.debug("truststore '%s' set for connection: %s", getTrustStoreInfo(), getConnectionId());
                }
            }
            if (null != getKeyStoreInfo()) {
                var keyStore = getKeyStoreInfo().resolveKeyStore();
                if (keyStore.isPresent()) {
                    contextBuilder.loadKeyMaterial(keyStore.get(), getKeyStoreInfo().getKeyOrStorePassword());
                    LOGGER.debug("keystore %s set for connection: %s", getKeyStoreInfo().getLocation(), getConnectionId());
                }
            }
            return Optional.of(contextBuilder.build());
        } catch (final NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException
                | KeyManagementException e) {
            LOGGER.error(e, SSL_CONTEXT_CREATION_FAILED, connectionId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Validates that all required configuration is present and correctly set.
     * The following must be valid:
     * <ul>
     *   <li>serviceUrl must not be null</li>
     *   <li>authenticationType must not be null</li>
     *   <li>connectionType must not be null</li>
     *   <li>connectionId must not be null</li>
     *   <li>if basic auth, loginCredentials must be complete</li>
     *   <li>if token auth, tokenResolver must be present</li>
     *   <li>if proxy host set, port must be > 0 and vice versa</li>
     * </ul>
     *
     * @throws ConnectionConfigurationException with a specific {@link ErrorReason}
     *         if any validation fails
     */
    public void validate() throws ConnectionConfigurationException {
        if (null == serviceUrl) {
            throw new ConnectionConfigurationException(ErrorReason.INVALID_URL);
        }
        if (null == authenticationType) {
            throw new ConnectionConfigurationException(ErrorReason.INVALID_AUTHENTICATION_TYPE);
        }
        if (null == connectionType) {
            throw new ConnectionConfigurationException(ErrorReason.INVALID_CONNECTION_TYPE);
        }
        if (null == connectionId) {
            throw new ConnectionConfigurationException(ErrorReason.INVALID_CONNECTION_ID);
        }
        if (isLoginCredentialsNecessary() && (null == loginCredentials || !loginCredentials.isComplete())) {
            throw new ConnectionConfigurationException(ErrorReason.INVALID_CREDENTIALS);
        }
        if (authenticationType.isTokenType() && null == tokenResolver) {
            throw new ConnectionConfigurationException(ErrorReason.INVALID_TOKEN);
        }
        if (!MoreStrings.isBlank(proxyHost) && (null == proxyPort || proxyPort <= 0)
                || null != proxyPort && proxyPort > 0 && MoreStrings.isBlank(proxyHost)) {
            throw new ConnectionConfigurationException(ErrorReason.INVALID_PROXY);
        }
    }
}
