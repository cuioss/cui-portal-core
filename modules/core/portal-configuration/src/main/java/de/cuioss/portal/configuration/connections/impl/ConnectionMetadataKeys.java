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
 * Holder class for keys used in the context of {@link ConnectionMetadata}
 *
 * @author Oliver Wolff
 *
 */
@UtilityClass
@SuppressWarnings("squid:S2068") // owolff: These are not hardcoded passwords but keys for the
                                 // lookup
public class ConnectionMetadataKeys {

    private static final String TRUSTSTORE_PREFIX = "truststore.";
    private static final String KEYSTORE_PREFIX = "keystore.";
    private static final String KEYPASSWORD = "keypassword";
    private static final String PASSWORD = "password";
    private static final String LOCATION = "location";
    private static final String AUTH_BASE = "authentication.";
    private static final String AUTH_BASIC_BASE = AUTH_BASE + "basic.";
    private static final String AUTH_CERTIFICATE_BASE = AUTH_BASE + "certificate.";
    private static final String AUTH_TOKEN_BASE = AUTH_BASE + "token.";
    private static final String AUTH_CERTIFICATE_KEYSTORE_BASE = AUTH_CERTIFICATE_BASE + KEYSTORE_PREFIX;

    /**
     * Prefix for configuring token based authentication:
     * "authentication.token.application."
     */
    private static final String AUTH_TOKEN_APPLICATION_BASE = AUTH_TOKEN_BASE + "application.";

    /** Prefix for configuring token based authentication */
    private static final String AUTH_TOKEN_USER_BASE = AUTH_TOKEN_BASE + "user.";

    /** "authentication.token.application.token". */
    public static final String AUTH_TOKEN_APPLICATION_TOKEN = AUTH_TOKEN_APPLICATION_BASE + "token";

    /** "authentication.token.application.key". */
    public static final String AUTH_TOKEN_APPLICATION_KEY = AUTH_TOKEN_APPLICATION_BASE + "key";

    /**
     * User password for BASIC authentication type ("authentication.basic.password")
     */
    public static final String AUTH_BASIC_USER_PASSWORD = AUTH_BASIC_BASE + PASSWORD;

    /** Username for BASIC authentication type ("authentication.basic.username") */
    public static final String AUTH_BASIC_USER_NAME = AUTH_BASIC_BASE + "username";

    /**
     * Key store location for CERTIFICATE authentication type
     * ("authentication.certificate.keystore.location")
     */
    public static final String AUTH_CERTIFICATE_KEYSTORE_LOCATION = AUTH_CERTIFICATE_KEYSTORE_BASE + LOCATION;

    /**
     * Key store password for CERTIFICATE authentication type
     * ("authentication.certificate.keystore.password")
     */
    public static final String AUTH_CERTIFICATE_KEYSTORE_PASSWORD = AUTH_CERTIFICATE_KEYSTORE_BASE + PASSWORD;

    /**
     * Private key password for accessing the key-store keys using CERTIFICATE
     * authentication type ("authentication.certificate.keystore.keypassword")
     */
    public static final String AUTH_CERTIFICATE_KEYSTORE_KEYPASSWORD = AUTH_CERTIFICATE_KEYSTORE_BASE + KEYPASSWORD;

    static final String TRANSPORT_BASE = "transport.secure.";

    private static final String TRANSPORT_KEYSTORE_BASE = TRANSPORT_BASE + KEYSTORE_PREFIX;

    private static final String TRANSPORT_TRUSTSTORE_BASE = TRANSPORT_BASE + TRUSTSTORE_PREFIX;

    /**
     * Key store location for Transport-level ("transport.secure.keystore.location")
     */
    public static final String TRANSPORT_KEYSTORE_LOCATION = TRANSPORT_KEYSTORE_BASE + LOCATION;

    /**
     * Key store password for Transport-level ("transport.secure.keystore.password")
     */
    public static final String TRANSPORT_KEYSTORE_PASSWORD = TRANSPORT_KEYSTORE_BASE + PASSWORD;

    /**
     * Key store key-password for Transport-level
     * ("transport.secure.keystore.keypassword")
     */
    public static final String TRANSPORT_KEYSTORE_KEYPASSWORD = TRANSPORT_KEYSTORE_BASE + KEYPASSWORD;

    /**
     * Trust-store location for Transport-level
     * ("transport.secure.truststore.location")
     */
    public static final String TRANSPORT_TRUSTSTORE_LOCATION = TRANSPORT_TRUSTSTORE_BASE + LOCATION;

    /**
     * Trust-store password for Transport-level
     * ("transport.secure.truststore.password")
     */
    public static final String TRANSPORT_TRUSTSTORE_PASSWORD = TRANSPORT_TRUSTSTORE_BASE + PASSWORD;

    /**
     * Disable Verification of hostnames
     * ("transport.secure.disableHostNameVerification")
     */
    public static final String TRANSPORT_DISABLE_HOSTNAME_VALIDATION = TRANSPORT_BASE + "disableHostNameVerification";

    /** Authentication type */
    public static final String AUTHENTICATION_TYPE = "authentication";

    /** URL to connect to */
    public static final String URL_KEY = "url";

    /** Connection type */
    public static final String TYPE_KEY = "type";

    /** Description for the connection configuration */
    public static final String DESCRIPTION_KEY = "description";

    /**
     * ID for this connection configuration. Usually the name of the service that is
     * called, e.g. <code>provider-directory</code> or <code>app-gateway</code>.
     * This ID is also eventually used in distributed tracing.
     */
    public static final String ID_KEY = "id";

    /** Prefix for additional config-elements */
    public static final String CONFIG_KEY = "config";

    /** Prefix for configuring the REST-client */
    public static final String CONFIG_CLIENT_KEY = "client";

    /** Value for configuring the REST-client: okclient */
    public static final String CONFIG_CLIENT_OKHTTP = "okclient";

    private static final String TIMEOUT_BASE = "timeout.";

    /** Timeout in seconds to establish a connection ("timeout.connection") */
    public static final String CONNECTION_TIMEOUT = TIMEOUT_BASE + "connection";

    /** Timeout in seconds to read a response ("timeout.read") */
    public static final String READ_TIMEOUT = TIMEOUT_BASE + "read";

    /** Enable|Disable distributed tracing */
    public static final String TRACING = "tracing";

    public static final String PROXY_HOST = "proxyHost";

    public static final String PROXY_PORT = "proxyPort";
}
