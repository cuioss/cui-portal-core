package de.cuioss.portal.configuration.connections.exception;

import lombok.Getter;

/**
 * Simple enum used for identifying the reason for a connection problem without
 * the need to refer to enclosed exception type.
 *
 * @author Oliver Wolff
 */
public enum ErrorReason {

    /** The credentials are invalid. */
    INVALID_CREDENTIALS("The credentials are invalid"),

    /** The token-configuration is invalid. */
    INVALID_TOKEN("The token is invalid"),

    /** The trust store is not configured properly. */
    INVALID_TRUSTSTORE("The trust store is not configured properly"),

    /** The key store is not configured properly. */
    INVALID_KEYSTORE("The key store is not configured properly."),

    /** The url is invalid. */
    INVALID_URL("The url is invalid."),

    /** The authentication type is not specified or is unknown. */
    INVALID_AUTHENTICATION_TYPE("The authentication type is not specified or is unknown."),

    /** The connection type is not specified or is unknown. */
    INVALID_CONNECTION_TYPE("The connection type is not specified or is unknown."),

    /** The connection id is not specified. */
    INVALID_CONNECTION_ID("The connection id is not specified"),

    /** A general configuration error. */
    INVALID_CONFIGURATION("A general configuration error."),

    /** The proxy configuration is invalid. */
    INVALID_PROXY("The proxy is invalid"),

    /**
     * before actually trying to establish a connection. If this fails without
     * {@link ConnectionException} this {@link ErrorReason} will be chosen.
     */
    CHECK_CONNECTION_FAILED("Establish a connection failed"),

    /** IO_Error. */
    IO_ERROR("I/O exception has occurred"),

    /** TimeOut */
    IO_TIMEOUT("Connection timeout");

    @Getter
    private final String errorText;

    ErrorReason(final String errorTextValue) {
        errorText = errorTextValue;
    }
}
