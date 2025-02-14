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

import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.logging.CuiLogger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Defines the kind of authorization needed for a certain connection.
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthenticationType {

    /**
     * No authentication required.
     */
    NONE("none", false),

    /**
     * Username and password.
     */
    BASIC("basic", false),

    /**
     * A certificate, usually chosen by name from a trust-store.
     */
    CERTIFICATE("certificate", false),

    /**
     * Defines a static token for all connections.
     */
    TOKEN_APPLICATION("token.application", true),

    /**
     * Defines a token read from a specified field in AuthenticatedUserInfo.
     * Caution: Currently Experimental / Not for productive use.
     */
    TOKEN_FROM_USER("token.user", true);

    private final String keyName;

    /**
     * Flag whether the concrete {@link AuthenticationType} needs a token for
     * Authorization.
     */
    @Getter
    private final boolean tokenType;

    private static final CuiLogger LOGGER = new CuiLogger(AuthenticationType.class);

    /**
     * Determines the authentication-type from a given map with connection related
     * properties. The map contents should contain information as described in
     * {@link ConfigAsConnectionMetadata} with 'basename' already been stripped,
     * usually this is done by using
     * {@link ConfigurationHelper#getFilteredPropertyMap(Map, String, boolean)}. The
     * getFilteredPropertyMap also removes properties with empty values.
     *
     * @param basename      for the properties, used for logging / tracing in case
     *                      of errors
     * @param configuration to be used for determining the concrete
     *                      {@link AuthenticationType}
     * @return the resolved {@link AuthenticationType} if it can be extracted,
     * {@link AuthenticationType#NONE} otherwise. The algorithm checks the
     * keys for containing corresponding token, saying in case of containing
     * the substring "authentication.certificate" it will return
     * {@link AuthenticationType#CERTIFICATE}, in case of containing the
     * substring "authentication.basic" it will return
     * {@link AuthenticationType#BASIC}. It will detect variants like
     * 'authentication=certificate' as well. The first match of enum
     * {@link AuthenticationType} is used.
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
        LOGGER.warn("""
                Portal-131: Unable to determine AuthenticationType for connection='%s' and properties, returning \
                AuthenticationType.NONE\
                """, basename, configuration);
        return AuthenticationType.NONE;
    }

}
