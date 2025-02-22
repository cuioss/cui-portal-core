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

import de.cuioss.tools.logging.CuiLogger;

import static de.cuioss.portal.configuration.PortalConfigurationMessages.WARN;
import static de.cuioss.tools.string.MoreStrings.isEmpty;

/**
 * Defines the technical protocol or interface type used for external service connections.
 * Each type represents a different communication protocol with its own configuration
 * requirements and connection handling.
 *
 * @author Oliver Wolff
 */
public enum ConnectionType {

    /** 
     * Represents a JMX (Java Management Extensions) connection.
     * Used for managing and monitoring Java applications.
     */
    JMX,

    /** 
     * Represents a REST (Representational State Transfer) endpoint.
     * Used for HTTP-based API communications following REST principles.
     */
    REST,

    /** 
     * Represents a database connection endpoint.
     * Used for connecting to relational or NoSQL databases.
     */
    DATABASE,

    /** 
     * Represents an LDAP (Lightweight Directory Access Protocol) endpoint.
     * Used for accessing and maintaining distributed directory information.
     */
    LDAP,

    /** 
     * Represents a DSML (Directory Services Markup Language) client.
     * Used for LDAP operations using XML-based protocol.
     */
    DSML,

    /** 
     * Represents a SOAP (Simple Object Access Protocol) endpoint.
     * Used for XML-based web service communications.
     */
    SOAP,

    /** 
     * Represents a connection type not covered by the standard types.
     * Used as a fallback for custom or unsupported protocols.
     */
    UNDEFINED;

    private static final CuiLogger LOGGER = new CuiLogger(ConnectionType.class);

    /**
     * Resolves a connection type from its string representation.
     * The resolution is case-insensitive and falls back to {@link #UNDEFINED}
     * if the type cannot be resolved.
     *
     * @param name the name of the connection type to resolve, must not be {@code null}
     * @return the resolved {@link ConnectionType}, never null. Returns {@link #UNDEFINED}
     *         if the name is empty or cannot be mapped to a known type
     */
    public static ConnectionType resolveFrom(final String name) {
        if (isEmpty(name)) {
            return ConnectionType.UNDEFINED;
        }
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            LOGGER.warn(e, WARN.INVALID_CONNECTION_TYPE.format(name, values()));
            return ConnectionType.UNDEFINED;
        }
    }

}
