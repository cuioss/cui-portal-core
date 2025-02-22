/**
 * Provides core servlet components and utilities for building portal-based web applications.
 * This package contains base classes and utilities for handling HTTP requests, managing
 * authentication, and resolving hostnames in a portal context.
 *
 * <p><strong>Key Components:</strong></p>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.servlet.AbstractPortalServlet} - Base servlet with authentication and authorization</li>
 *   <li>{@link de.cuioss.portal.core.servlet.ExternalHostnameProducer} - Resolves external hostnames for proxied environments</li>
 * </ul>
 *
 * <p><strong>Features:</strong></p>
 * <ul>
 *   <li>Role-based access control</li>
 *   <li>Standardized error handling</li>
 *   <li>Proxy-aware hostname resolution</li>
 *   <li>Authentication integration</li>
 * </ul>
 *
 * @see jakarta.servlet.Servlet
 * @see jakarta.servlet.http.HttpServlet
 * @see de.cuioss.portal.core.servlet.AbstractPortalServlet
 * @since 1.0
 */
package de.cuioss.portal.core.servlet;
