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
package de.cuioss.portal.configuration.connections.impl;

import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.http.ssl.SSLContexts;

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

/**
 * Helper class that provides metadata regarding a connection.
 *
 * @author Oliver Wolff
 */
@EqualsAndHashCode(exclude = { "loginCredentials" })
@ToString(exclude = { "loginCredentials" })
@Builder(toBuilder = true)
public class ConnectionMetadata implements Serializable {

    private static final CuiLogger log = new CuiLogger(ConnectionMetadata.class);

    private static final String PORTAL_510 = "Portal-510: Unable to create SSLContext for connection '{}', due to '{}', defaulting to default ssl configuration";

    private static final long serialVersionUID = -8168073688801716947L;

    /**
     * context map containing additional runtime information belonging to the
     * {@link ConnectionMetadata}
     */
    @Builder.Default
    @Getter
    private final Map<Serializable, Serializable> contextMap = new ConcurrentHashMap<>();

    /**
     * Wrapper for username / password. Only to be used if
     * {@link #isLoginCredentialsNecessary()}
     */
    @Getter
    @Setter
    private LoginCredentials loginCredentials;

    /** Resolver for tokens in case of {@link AuthenticationType#isTokenType()}. */
    @Getter
    @Setter
    private TokenResolver tokenResolver;

    /**
     * Key store configuration. Only used if {@linkplain #authenticationType} is
     * {@linkplain AuthenticationType#CERTIFICATE} and/or connection is secured.
     */
    @Getter
    @Setter
    private KeyStoreProvider keyStoreInfo;

    /**
     * Trust store configuration. Used if connection is secured.
     */
    @Getter
    @Setter
    private KeyStoreProvider trustStoreInfo;

    /**
     * Defines the {@link SSLContext} derived from {@link #getKeyStoreInfo()} and
     * {@link #getTrustStoreInfo()} or the Default-context, if at least one of the
     * other contexts is {@code null}
     */
    private transient SSLContext sslContext;

    /** The serviceUrl to be used. */
    @Getter
    @Setter
    private String serviceUrl;

    /** Defines how to authenticate the connection. */
    @Getter
    @Setter
    private AuthenticationType authenticationType;

    /** Defines the technical layer of the connection. */
    @Getter
    @Setter
    private ConnectionType connectionType;

    @Getter
    @Setter
    private String description;

    /** The technical identifier for this connection. */
    @Getter
    @Setter
    private String connectionId;

    @Getter
    @Setter
    @Builder.Default
    private boolean disableHostNameVerification = false;

    /** Connection timeout value */
    @Getter
    @Setter
    private long connectionTimeout;

    /** Connection timeout time unit, defaults to {@link TimeUnit#SECONDS} */
    @Getter
    @Setter
    @Builder.Default
    private TimeUnit connectionTimeoutUnit = TimeUnit.SECONDS;

    /** Response read timeout value */
    @Getter
    @Setter
    private long readTimeout;

    /** Response read timeout time unit, defaults to {@link TimeUnit#SECONDS} */
    @Getter
    @Setter
    @Builder.Default
    private TimeUnit readTimeoutUnit = TimeUnit.SECONDS;

    /**
     * Enable or disable distributed tracing for this connection. Only effective if
     * {@link de.cuioss.portal.configuration.TracingConfigKeys#PORTAL_TRACING_ENABLED}
     * is enabled. Defaults to <code>true</code>.
     */
    @Getter
    @Setter
    @Builder.Default
    private boolean tracingEnabled = true;

    @Getter
    @Setter
    private String proxyHost;

    @Getter
    @Setter
    private Integer proxyPort;

    /**
     * @return boolean indicating whether credentials are necessary. It is true if
     *         {@link #getAuthenticationType()} is {@link AuthenticationType#BASIC}
     */
    public boolean isLoginCredentialsNecessary() {
        return AuthenticationType.BASIC.equals(getAuthenticationType());
    }

    /**
     * Adds an entry to the contained contextMap
     *
     * @param key   to be added, must not be null
     * @param value to be added, must not be null
     * @return The {@link ConnectionMetadata} itself providing fluent-style-api
     */
    public ConnectionMetadata contextMapElement(final Serializable key, final Serializable value) {
        contextMap.put(key, value);
        return this;
    }

    /**
     * Creates an {@link SSLContext} created from the information derived by
     * {@link #getTrustStoreInfo()} and {@link #getKeyStoreInfo()}. If this fail it
     * will default to the platform defaults.
     *
     * @return the created {@link SSLContext}
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
     * Creates an {@link SSLContext} created from the information derived by
     * {@link #getTrustStoreInfo()} and {@link #getKeyStoreInfo()}. If this fail it
     * will return {@link Optional#empty()}.
     *
     * @return the created {@link SSLContext}
     */
    public Optional<SSLContext> resolveOptionalSSLContext() {
        log.debug("Resolving optional SSLContext for connection '{}'", getConnectionId());
        if (null == getTrustStoreInfo() && null == getKeyStoreInfo()) {
            log.debug("SslTrustStoreInfo is null, using platform-default");
            return Optional.empty();
        }
        log.debug("Create custom SSLContext for connection '{}'", getConnectionId());
        try {
            final var contextBuilder = SSLContexts.custom();
            if (null != getTrustStoreInfo()) {
                var trustStore = getTrustStoreInfo().resolveKeyStore();
                if (trustStore.isPresent()) {
                    contextBuilder.loadTrustMaterial(trustStore.get(), null);
                    log.debug("truststore '{}' set for connection: {}", getTrustStoreInfo(), getConnectionId());
                }
            }
            if (null != getKeyStoreInfo()) {
                var keyStore = getKeyStoreInfo().resolveKeyStore();
                if (keyStore.isPresent()) {
                    contextBuilder.loadKeyMaterial(keyStore.get(), getKeyStoreInfo().getKeyOrStorePassword());
                    log.debug("keystore {} set for connection: {}", getKeyStoreInfo().getLocation(), getConnectionId());
                }
            }
            return Optional.of(contextBuilder.build());
        } catch (final NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException
                | KeyManagementException e) {
            log.error(e, PORTAL_510, connectionId, e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks whether this object is correctly configured. If not it will throw an
     * {@link ConnectionConfigurationException} communicating what is not configured
     * correctly.
     *
     * @throws ConnectionConfigurationException if a mandatory property is not set.
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
