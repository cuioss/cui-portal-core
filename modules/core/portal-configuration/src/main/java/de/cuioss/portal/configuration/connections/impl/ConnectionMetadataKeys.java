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

import lombok.experimental.UtilityClass;

/**
 * Defines the configuration property keys used in {@link ConnectionMetadata}.
 * This class provides a centralized registry of all valid configuration keys
 * for connection settings, including authentication, transport security, and
 * general connection properties.
 * <p>
 * The keys are organized into several categories:
 * <ul>
 *   <li>Authentication (basic, certificate, token)</li>
 *   <li>Transport Security (keystore, truststore)</li>
 *   <li>Connection Properties (timeout, proxy)</li>
 *   <li>Client Configuration</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
@UtilityClass
@SuppressWarnings("squid:S2068") // These are not hardcoded passwords but configuration property keys
public class ConnectionMetadataKeys {

    private static final String TRUSTSTORE_PREFIX = "truststore.";
    private static final String KEYSTORE_PREFIX = "keystore.";
    private static final String KEY_PASSWORD = "keypassword";
    private static final String PASSWORD = "password";
    private static final String LOCATION = "location";
    private static final String AUTH_BASE = "authentication.";
    private static final String AUTH_BASIC_BASE = AUTH_BASE + "basic.";
    private static final String AUTH_CERTIFICATE_BASE = AUTH_BASE + "certificate.";
    private static final String AUTH_TOKEN_BASE = AUTH_BASE + "token.";
    private static final String AUTH_CERTIFICATE_KEYSTORE_BASE = AUTH_CERTIFICATE_BASE + KEYSTORE_PREFIX;

    /**
     * Base prefix for application-wide token authentication configuration.
     * Complete prefix: "authentication.token.application."
     */
    private static final String AUTH_TOKEN_APPLICATION_BASE = AUTH_TOKEN_BASE + "application.";

    /**
     * Base prefix for user-specific token authentication configuration.
     * Complete prefix: "authentication.token.user."
     */
    private static final String AUTH_TOKEN_USER_BASE = AUTH_TOKEN_BASE + "user.";

    /**
     * Property key for the application-wide authentication token.
     * Complete key: "authentication.token.application.token"
     */
    public static final String AUTH_TOKEN_APPLICATION_TOKEN = AUTH_TOKEN_APPLICATION_BASE + "token";

    /**
     * Property key for the token key name in application-wide authentication.
     * Complete key: "authentication.token.application.key"
     */
    public static final String AUTH_TOKEN_APPLICATION_KEY = AUTH_TOKEN_APPLICATION_BASE + "key";

    /**
     * Property key for the basic authentication password.
     * Complete key: "authentication.basic.password"
     */
    public static final String AUTH_BASIC_USER_PASSWORD = AUTH_BASIC_BASE + PASSWORD;

    /**
     * Property key for the basic authentication username.
     * Complete key: "authentication.basic.username"
     */
    public static final String AUTH_BASIC_USER_NAME = AUTH_BASIC_BASE + "username";

    /**
     * Property key for the certificate keystore location.
     * Complete key: "authentication.certificate.keystore.location"
     */
    public static final String AUTH_CERTIFICATE_KEYSTORE_LOCATION = AUTH_CERTIFICATE_KEYSTORE_BASE + LOCATION;

    /**
     * Property key for the certificate keystore password.
     * Complete key: "authentication.certificate.keystore.password"
     */
    public static final String AUTH_CERTIFICATE_KEYSTORE_PASSWORD = AUTH_CERTIFICATE_KEYSTORE_BASE + PASSWORD;

    /**
     * Property key for the certificate keystore's private key password.
     * Complete key: "authentication.certificate.keystore.keypassword"
     */
    public static final String AUTH_CERTIFICATE_KEYSTORE_KEY_PASSWORD = AUTH_CERTIFICATE_KEYSTORE_BASE + KEY_PASSWORD;

    /** Base prefix for transport security configuration */
    static final String TRANSPORT_BASE = "transport.secure.";

    private static final String TRANSPORT_KEYSTORE_BASE = TRANSPORT_BASE + KEYSTORE_PREFIX;

    private static final String TRANSPORT_TRUSTSTORE_BASE = TRANSPORT_BASE + TRUSTSTORE_PREFIX;

    /**
     * Property key for the transport security keystore location.
     * Used for client certificates in SSL/TLS connections.
     * Complete key: "transport.secure.keystore.location"
     */
    public static final String TRANSPORT_KEYSTORE_LOCATION = TRANSPORT_KEYSTORE_BASE + LOCATION;

    /**
     * Property key for the transport security keystore password.
     * Used to access the keystore file.
     * Complete key: "transport.secure.keystore.password"
     */
    public static final String TRANSPORT_KEYSTORE_PASSWORD = TRANSPORT_KEYSTORE_BASE + PASSWORD;

    /**
     * Property key for the transport security keystore's private key password.
     * Used to access individual keys within the keystore.
     * Complete key: "transport.secure.keystore.keypassword"
     */
    public static final String TRANSPORT_KEYSTORE_KEY_PASSWORD = TRANSPORT_KEYSTORE_BASE + KEY_PASSWORD;

    /**
     * Property key for the transport security truststore location.
     * Used to verify server certificates in SSL/TLS connections.
     * Complete key: "transport.secure.truststore.location"
     */
    public static final String TRANSPORT_TRUSTSTORE_LOCATION = TRANSPORT_TRUSTSTORE_BASE + LOCATION;

    /**
     * Property key for the transport security truststore password.
     * Used to access the truststore file.
     * Complete key: "transport.secure.truststore.password"
     */
    public static final String TRANSPORT_TRUSTSTORE_PASSWORD = TRANSPORT_TRUSTSTORE_BASE + PASSWORD;

    /**
     * Property key to disable hostname verification in SSL/TLS connections.
     * <p>
     * <strong>Warning:</strong> Disabling hostname verification reduces security
     * and should only be used in controlled environments.
     * Complete key: "transport.secure.disableHostNameVerification"
     */
    public static final String TRANSPORT_DISABLE_HOSTNAME_VALIDATION = TRANSPORT_BASE + "disableHostNameVerification";

    /**
     * Property key for the authentication type setting.
     * Valid values are defined in {@link AuthenticationType}.
     */
    public static final String AUTHENTICATION_TYPE = "authentication";

    /**
     * Property key for the service endpoint URL.
     * Should be a valid URL including protocol and path.
     */
    public static final String URL_KEY = "url";

    /**
     * Property key for the connection type setting.
     * Valid values are defined in {@link ConnectionType}.
     */
    public static final String TYPE_KEY = "type";

    /**
     * Property key for the human-readable connection description.
     * Used for documentation and logging purposes.
     */
    public static final String DESCRIPTION_KEY = "description";

    /**
     * Property key for the connection identifier.
     * Used to uniquely identify the connection in logs, metrics, and tracing.
     * Examples: "provider-directory", "app-gateway"
     */
    public static final String ID_KEY = "id";

    /**
     * Base prefix for additional configuration properties.
     * Used to namespace connection-specific settings.
     */
    public static final String CONFIG_KEY = "config";

    /**
     * Property key prefix for REST client configuration.
     * Used to specify client-specific settings.
     */
    public static final String CONFIG_CLIENT_KEY = "client";

    /**
     * Value indicating the use of OkHttp client implementation.
     * Used with {@link #CONFIG_CLIENT_KEY}.
     */
    public static final String CONFIG_CLIENT_OKHTTP = "okclient";

    private static final String TIMEOUT_BASE = "timeout.";

    /**
     * Property key for connection timeout in seconds.
     * Specifies the maximum time to establish a connection.
     * Complete key: "timeout.connection"
     */
    public static final String CONNECTION_TIMEOUT = TIMEOUT_BASE + "connection";

    /**
     * Property key for read timeout in seconds.
     * Specifies the maximum time to wait for a response.
     * Complete key: "timeout.read"
     */
    public static final String READ_TIMEOUT = TIMEOUT_BASE + "read";

    /**
     * Property key to enable/disable distributed tracing.
     * When enabled, generates trace data for monitoring and debugging.
     */
    public static final String TRACING = "tracing";

    /**
     * Property key for HTTP proxy host.
     * Used when connections need to be routed through a proxy server.
     */
    public static final String PROXY_HOST = "proxyHost";

    /**
     * Property key for HTTP proxy port.
     * Used in conjunction with {@link #PROXY_HOST} to configure proxy settings.
     */
    public static final String PROXY_PORT = "proxyPort";
}
