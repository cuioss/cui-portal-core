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

import static de.cuioss.portal.configuration.PortalConfigurationMessages.WARN;

import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.logging.CuiLogger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Defines the authentication methods available for external service connections.
 * Each type represents a different authentication strategy with its own configuration
 * requirements and security implications.
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthenticationType {

    /**
     * No authentication required.
     * Use this for public services or internal services in secure networks.
     */
    NONE("none", false),

    /**
     * HTTP Basic Authentication using username and password.
     * Requires loginCredentials to be set in the connection configuration.
     */
    BASIC("basic", false),

    /**
     * Client certificate authentication using X.509 certificates.
     * Requires a keystore configuration with the client certificate.
     */
    CERTIFICATE("certificate", false),

    /**
     * Static token authentication using a predefined application token.
     * The token is configured in the connection properties and remains
     * constant for all requests.
     */
    TOKEN_APPLICATION("token.application", true),

    /**
     * Dynamic token authentication using the authenticated user's token.
     * The token is retrieved from the user's authentication context for
     * each request.
     * <p>
     * <strong>Note:</strong> This feature is experimental and not recommended
     * for production use.
     */
    TOKEN_FROM_USER("token.user", true);

    private final String keyName;

    /**
     * Indicates whether this authentication type uses token-based authentication.
     * Token types require a {@link de.cuioss.portal.configuration.connections.TokenResolver} to be configured.
     */
    @Getter
    private final boolean tokenType;

    private static final CuiLogger LOGGER = new CuiLogger(AuthenticationType.class);

    /**
     * Resolves the appropriate authentication type from a configuration map.
     * The resolution process follows these steps:
     * <ol>
     *   <li>Check for null/empty configuration - returns {@link #NONE}</li>
     *   <li>Look for token-specific configurations first</li>
     *   <li>Extract authentication type from property keys</li>
     *   <li>Fall back to direct 'authentication' property value</li>
     *   <li>Default to {@link #NONE} if no match is found</li>
     * </ol>
     *
     * @param basename      identifier for the connection, used for logging
     * @param configuration map of configuration properties, should be filtered to
     *                      connection-specific properties
     * @return resolved authentication type, never null
     * @see ConfigurationHelper#getFilteredPropertyMap(Map, String, boolean)
     */
    public static AuthenticationType resolveFrom(String basename, Map<String, String> configuration) {
        if (null == configuration || configuration.isEmpty()) {
            LOGGER.trace("No Properties given connection='%s', returning AuthenticationType.NONE", basename);
            return AuthenticationType.NONE;
        }
        LOGGER.trace("Determining AuthenticationType for connection='%s' from properties %s", basename, configuration);
        var names = ConfigurationHelper.getFilteredPropertyMap(configuration, "authentication.", true);
        Set<String> elements = new TreeSet<>();
        for (String key : names.keySet()) {
            var lowerCaseKey = key.toLowerCase();
            if (lowerCaseKey.startsWith(TOKEN_APPLICATION.keyName)) {
                LOGGER.trace("Determined AuthenticationType=%s", TOKEN_APPLICATION);
                return AuthenticationType.TOKEN_APPLICATION;
            }
            if (lowerCaseKey.startsWith(TOKEN_FROM_USER.keyName)) {
                LOGGER.trace("Determined AuthenticationType=%s", TOKEN_FROM_USER);
                return AuthenticationType.TOKEN_FROM_USER;
            }
            if (lowerCaseKey.contains(".")) {
                // This filtering is needed to catch elements that are part of a longer key,
                // e.g.:
                // "authentication.certificate.keyStore.location"
                elements.add(lowerCaseKey.substring(0, lowerCaseKey.indexOf('.')).trim());
            } else {
                // This is for cases where there is only the name of the Authentication-type
                // without nested elements, e.g. 'authentication.certificate'
                elements.add(lowerCaseKey.trim());
            }

        }
        if (elements.isEmpty()) {
            LOGGER.trace("Check whether there is a key / value like 'authentication=certificate'. Otherwise add NONE");
            elements.add(
                    configuration.getOrDefault("authentication", AuthenticationType.NONE.name()).trim().toLowerCase());
        }
        LOGGER.debug("Connection='%s' extracted names %s", basename, elements);
        for (AuthenticationType type : AuthenticationType.values()) {
            if (elements.contains(type.keyName)) {
                LOGGER.debug("Determined Authentication-Type '%s' for connection '%s'", type, basename);
                return type;
            }
        }
        LOGGER.warn(WARN.UNABLE_TO_DETERMINE_AUTH_TYPE.format(basename));
        return AuthenticationType.NONE;
    }

}
