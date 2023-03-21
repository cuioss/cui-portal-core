package de.cuioss.portal.configuration.connections.impl;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata;
import de.cuioss.portal.configuration.util.ConfigurationHelper;
import de.cuioss.tools.logging.CuiLogger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Defines the kind of authorization needed for a certain connection.
 *
 * @author Oliver Wolff
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthenticationType {

    /** No authentication required. */
    NONE("none", false),

    /** User name and password. */
    BASIC("basic", false),

    /** A certificate, usually chosen by name from a trust-store. */
    CERTIFICATE("certificate", false),

    /** Defines a static token for all connections. */
    TOKEN_APPLICATION("token.application", true),

    /**
     * Defines a token read from a specified field in AuthenticatedUserInfo.
     * Caution: Currently Experimental / Not for productive use.
     */
    TOKEN_FROM_USER("token.user", true);

    private final String keyName;

    /** Flag whether the concrete {@link AuthenticationType} needs a token for Authorization. */
    @Getter
    private final boolean tokenType;

    private static final CuiLogger log = new CuiLogger(AuthenticationType.class);

    /**
     * Determines the authentication-type from a given map with connection related properties. The
     * map contents should contain information as described in {@link ConfigAsConnectionMetadata}
     * with 'basename' already been stripped, usually this is done by using
     * {@link ConfigurationHelper#getFilteredPropertyMap(Map, String, boolean)}.
     * The getFilteredPropertyMap also removes properties with empty values.
     *
     * @param basename for the properties, used for logging / tracing in case of errors
     * @param configuration to be used for determining the concrete {@link AuthenticationType}
     * @return the resolved {@link AuthenticationType} if it can be extracted,
     *         {@link AuthenticationType#NONE} otherwise. The algorithm checks the keys for
     *         containing corresponding token, saying in case of containing the substring
     *         "authentication.certificate" it will return {@link AuthenticationType#CERTIFICATE},
     *         in case of containing the substring "authentication.basic" it will return
     *         {@link AuthenticationType#BASIC}. It will detect variants like
     *         'authentication=certificate' as well. The first match of enum {@link AuthenticationType} is used.
     */
    public static AuthenticationType resolveFrom(String basename, Map<String, String> configuration) {
        if (null == configuration || configuration.isEmpty()) {
            log.trace("No Properties given connection='{}', returning AuthenticationType.NONE", basename);
            return AuthenticationType.NONE;
        }
        log.trace("Determining AuthenticationType for connection='{}' from properties {}", basename, configuration);
        var names = ConfigurationHelper.getFilteredPropertyMap(configuration, "authentication.", true);
        Set<String> elements = new TreeSet<>();
        for (String key : names.keySet()) {
            var lowerCaseKey = key.toLowerCase();
            if (lowerCaseKey.startsWith(TOKEN_APPLICATION.keyName)) {
                log.trace("Determined AuthenticationType={}", TOKEN_APPLICATION);
                return AuthenticationType.TOKEN_APPLICATION;
            }
            if (lowerCaseKey.startsWith(TOKEN_FROM_USER.keyName)) {
                log.trace("Determined AuthenticationType={}", TOKEN_FROM_USER);
                return TOKEN_FROM_USER;
            }
            if (lowerCaseKey.contains(".")) {
                // This filtering is needed to catch elements that are part of a longer key, e.g.:
                // "authentication.certificate.keyStore.location"
                elements.add(lowerCaseKey.substring(0, lowerCaseKey.indexOf('.')).trim());
            } else {
                // This is for cases where there is only the name of the Authentication-type
                // without nested elements, e.g. 'authentication.certificate'
                elements.add(lowerCaseKey.trim());
            }

        }
        if (elements.isEmpty()) {
            log.trace("Check whether there is a key / value like 'authentication=certificate'. Otherwise add NONE");
            elements.add(
                    configuration.getOrDefault("authentication", AuthenticationType.NONE.name()).trim().toLowerCase());
        }
        log.debug("Connection='{}' extracted names {}", basename, elements);
        for (AuthenticationType type : AuthenticationType.values()) {
            if (elements.contains(type.keyName)) {
                log.debug("Determined Authentication-Type '{}' for connection '{}'", type, basename);
                return type;
            }
        }
        log.warn("Portal-131: Unable to determine AuthenticationType for connection='{}' and properties, returning "
                + "AuthenticationType.NONE",
                basename, configuration);
        return AuthenticationType.NONE;
    }

}
