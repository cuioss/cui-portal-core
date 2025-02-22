/**
 * Provides core authentication and authorization infrastructure for Portal applications.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.authentication.AuthenticatedUserInfo} - Interface defining user authentication state</li>
 *   <li>{@link de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo} - Base implementation for authenticated users</li>
 *   <li>{@link de.cuioss.portal.authentication.PortalUserEnricher} - Interface for enriching user information</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>User authentication state management</li>
 *   <li>Role and group-based authorization</li>
 *   <li>Extensible user context attributes</li>
 *   <li>Thread-safe implementations</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * The main entry point is the {@link de.cuioss.portal.authentication.AuthenticatedUserInfo} interface,
 * which provides access to all user-related information. For implementation, use
 * {@link de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo} or create a custom implementation.
 *
 * <h3>Common Use Cases</h3>
 * <ul>
 *   <li>Role-based access control in web applications</li>
 *   <li>Integration with SSO systems</li>
 *   <li>Custom authentication workflows</li>
 *   <li>User session management</li>
 * </ul>
 *
 * <h3>Configuration</h3>
 * <pre>
 * # Authentication Configuration
 * portal.authentication.enabled=true
 * portal.authentication.session.timeout=3600
 * portal.authentication.allowed-roles=USER,ADMIN
 * </pre>
 *
 * <h3>Security Considerations</h3>
 * <ul>
 *   <li>Always validate user input and roles</li>
 *   <li>Use HTTPS for authentication requests</li>
 *   <li>Implement proper session management</li>
 *   <li>Follow the principle of least privilege</li>
 * </ul>
 * 
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.authentication;
