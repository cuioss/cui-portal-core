/**
 * Core components and infrastructure for building enterprise portal applications.
 * This package serves as the foundation for the CUI portal framework, providing
 * essential services, utilities, and base implementations.
 *
 * <p><strong>Core Functionalities:</strong></p>
 * <ul>
 *   <li>Authentication and authorization framework</li>
 *   <li>Session and storage management</li>
 *   <li>Servlet infrastructure</li>
 *   <li>Lifecycle management</li>
 * </ul>
 *
 * <p><strong>Key Packages:</strong></p>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.servlet} - Servlet components and request handling</li>
 *   <li>{@link de.cuioss.portal.core.storage} - Data storage abstractions</li>
 *   <li>{@link de.cuioss.portal.core.listener} - Application lifecycle management</li>
 *   <li>{@link de.cuioss.portal.core.user} - User authentication and context</li>
 * </ul>
 *
 * <p><strong>Core Services:</strong></p>
 * <ul>
 *   <li>User authentication and session management</li>
 *   <li>Role-based access control</li>
 *   <li>Configuration management</li>
 * </ul>
 *
 * <p><strong>Integration Points:</strong></p>
 * <ul>
 *   <li>Jakarta EE integration via CDI and Servlets</li>
 *   <li>MicroProfile configuration support</li>
 *   <li>Authentication provider integration</li>
 * </ul>
 *
 * <p><strong>Best Practices:</strong></p>
 * <ul>
 *   <li>Use provided abstractions instead of direct container access</li>
 *   <li>Leverage CDI for dependency injection and configuration</li>
 *   <li>Follow the security guidelines for authentication</li>
 *   <li>Utilize the logging framework for consistent monitoring</li>
 * </ul>
 *
 * @see de.cuioss.portal.core.servlet.AbstractPortalServlet
 * @see de.cuioss.portal.core.storage.SessionStorage
 * @see de.cuioss.portal.core.user.PortalUserProducer
 * @see de.cuioss.portal.core.listener.ServletLifecycleListener
 * @since 1.0
 */
package de.cuioss.portal.core;
