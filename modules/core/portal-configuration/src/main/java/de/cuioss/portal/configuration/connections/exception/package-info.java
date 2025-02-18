/**
 * Provides exception classes for handling connection-related errors in the Portal
 * configuration system. This package defines a hierarchy of exceptions:
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.connections.exception.ConnectionException} -
 *       Base exception for all connection-related errors</li>
 *   <li>{@link de.cuioss.portal.configuration.connections.exception.ConnectionConfigurationException} -
 *       Specific to configuration validation failures</li>
 * </ul>
 * <p>
 * The exceptions work in conjunction with {@link de.cuioss.portal.configuration.connections.exception.ErrorReason}
 * to provide structured error information that can be:
 * <ul>
 *   <li>Logged consistently with appropriate severity</li>
 *   <li>Translated into user-friendly messages</li>
 *   <li>Used for automated error handling and recovery</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.connections.exception;
