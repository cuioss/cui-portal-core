/**
 * Provides the core authentication facade and related services for Portal applications.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.authentication.facade.AuthenticationFacade} - Primary interface for authentication operations</li>
 *   <li>{@link de.cuioss.portal.authentication.facade.BaseAuthenticationFacade} - Base implementation with common functionality</li>
 *   <li>{@link de.cuioss.portal.authentication.facade.AuthenticationResults} - Constants and utility results</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Authentication context management</li>
 *   <li>User session handling</li>
 *   <li>Logout functionality</li>
 *   <li>Authentication source tracking</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * The {@link de.cuioss.portal.authentication.facade.AuthenticationFacade} is the main entry point
 * for authentication operations. Implementations should extend
 * {@link de.cuioss.portal.authentication.facade.BaseAuthenticationFacade} to leverage common functionality.
 * 
 * <h2>Thread Safety</h2>
 * All components in this package are designed to be thread-safe and suitable for use in concurrent
 * web applications. They rely on CDI-managed components and immutable state.
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.authentication.facade;
