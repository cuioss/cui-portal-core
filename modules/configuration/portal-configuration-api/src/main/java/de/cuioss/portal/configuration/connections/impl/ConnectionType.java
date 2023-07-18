package de.cuioss.portal.configuration.connections.impl;

import static de.cuioss.tools.string.MoreStrings.isEmpty;

import de.cuioss.tools.logging.CuiLogger;

/**
 * Defines the technical layer of the connection, e.g. JMX, Rest
 *
 * @author Oliver Wolff
 */
public enum ConnectionType {

    /** Represents a JMX Connection. */
    JMX,

    /** Represents a Rest-end-point. */
    REST,

    /** Represents a Database-end-point. */
    DATABASE,

    /** Represents a ldap-end-point. */
    LDAP,

    /** Represents a DSML-Client. */
    DSML,

    /** Represents a SOAP-endpoint. */
    SOAP,

    /** Represents an end-point not covered by the standard connections. */
    UNDEFINED;

    /**
     *
     * @param name must not be {@code null} or empty
     * @return {@linkplain ConnectionType}
     */
    public static ConnectionType resolveFrom(final String name) {
        if (isEmpty(name)) {
            return ConnectionType.UNDEFINED;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            new CuiLogger(ConnectionType.class).warn(
                    "Portal-131: Invalid configuration found for .type, actual '{}', expected one of '{}', defaulting to 'ConnectionType.UNDEFINED'",
                    name, values(), e);
            return ConnectionType.UNDEFINED;
        }
    }

}
