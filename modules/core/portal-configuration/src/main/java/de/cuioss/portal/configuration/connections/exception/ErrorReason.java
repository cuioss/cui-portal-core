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
package de.cuioss.portal.configuration.connections.exception;

import lombok.Getter;

/**
 * Enumerates the specific reasons for connection failures in the portal's connection system.
 * This enum provides a structured way to categorize connection errors without relying on
 * exception type inspection, enabling consistent error handling and user feedback.
 * <p>
 * The error reasons are grouped into several categories:
 * <ul>
 *   <li>Authentication failures (credentials, tokens)</li>
 *   <li>Security configuration issues (keystores, truststores)</li>
 *   <li>Connection configuration problems (URLs, types, IDs)</li>
 *   <li>Network and I/O errors</li>
 * </ul>
 * <p>
 * Each error reason includes a human-readable error message that can be used
 * for logging or user interface display.
 *
 * @author Oliver Wolff
 */
public enum ErrorReason {

    /** 
     * Indicates that the provided credentials (username/password) are invalid
     * or do not have sufficient permissions for the requested operation.
     */
    INVALID_CREDENTIALS("The credentials are invalid"),

    /** 
     * Indicates that the authentication token is invalid, expired,
     * or not properly configured for the target service.
     */
    INVALID_TOKEN("The token is invalid"),

    /** 
     * Indicates issues with the SSL/TLS truststore configuration,
     * such as missing certificates or incorrect truststore path/password.
     */
    INVALID_TRUSTSTORE("The trust store is not configured properly"),

    /** 
     * Indicates issues with the SSL/TLS keystore configuration,
     * such as missing private keys or incorrect keystore path/password.
     */
    INVALID_KEYSTORE("The key store is not configured properly."),

    /** 
     * Indicates that the service URL is malformed, unreachable,
     * or does not point to a valid endpoint.
     */
    INVALID_URL("The url is invalid."),

    /** 
     * Indicates that the specified authentication type is not supported
     * or is missing from the connection configuration.
     */
    INVALID_AUTHENTICATION_TYPE("The authentication type is not specified or is unknown."),

    /** 
     * Indicates that the specified connection type is not supported
     * or is missing from the connection configuration.
     */
    INVALID_CONNECTION_TYPE("The connection type is not specified or is unknown."),

    /** 
     * Indicates that the connection identifier is missing,
     * preventing proper connection tracking and management.
     */
    INVALID_CONNECTION_ID("The connection id is not specified"),

    /** 
     * Indicates a general configuration error that doesn't fall into
     * other more specific categories.
     */
    INVALID_CONFIGURATION("A general configuration error."),

    /** 
     * Indicates issues with proxy server configuration,
     * such as invalid proxy URL, credentials, or protocol.
     */
    INVALID_PROXY("The proxy is invalid"),

    /**
     * Indicates that the initial connection check failed before
     * attempting to establish the actual connection. This is used
     * when pre-connection validation fails without a more specific
     * {@link ConnectionException}.
     */
    CHECK_CONNECTION_FAILED("Establish a connection failed"),

    /** 
     * Indicates a general I/O error occurred during connection
     * operations, such as network or file system issues.
     */
    IO_ERROR("I/O exception has occurred"),

    /** 
     * Indicates that the connection attempt exceeded the configured
     * timeout period without establishing a connection.
     */
    IO_TIMEOUT("Connection timeout");

    /**
     * Human-readable description of the error reason.
     * This text is suitable for logging and user interface display.
     */
    @Getter
    private final String errorText;

    /**
     * Constructs a new error reason with the specified error message.
     *
     * @param errorTextValue the human-readable error message for this reason
     */
    ErrorReason(final String errorTextValue) {
        errorText = errorTextValue;
    }
}
