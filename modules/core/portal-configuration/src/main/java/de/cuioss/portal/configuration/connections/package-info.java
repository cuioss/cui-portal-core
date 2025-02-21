/**
 * Provides the core framework for configuring and managing server-to-server connections
 * in the Portal environment. This package includes:
 * <ul>
 *   <li>Connection configuration and metadata management</li>
 *   <li>Authentication strategies (Basic, Certificate, Token)</li>
 *   <li>Transport security configuration (SSL/TLS)</li>
 *   <li>Connection type definitions (REST, JMX, etc.)</li>
 * </ul>
 * <p>
 * Key components:
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.connections.impl.ConnectionMetadata} - Core connection configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.connections.TokenResolver} - Token-based authentication</li>
 *   <li>{@link de.cuioss.portal.configuration.connections.impl.ConnectionType} - Supported connection types</li>
 * </ul>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.connections;
