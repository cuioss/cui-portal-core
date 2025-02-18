/**
 * Provides model classes for authentication and user information in Portal applications.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo} - Base implementation of authenticated user information</li>
 *   <li>{@link de.cuioss.portal.authentication.model.UserStore} - Storage interface for user information</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Immutable user information storage</li>
 *   <li>Builder pattern for user info creation</li>
 *   <li>Extensible attribute system</li>
 *   <li>Role and group management</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * The primary class in this package is {@link de.cuioss.portal.authentication.model.BaseAuthenticatedUserInfo},
 * which provides a concrete implementation of {@link de.cuioss.portal.authentication.AuthenticatedUserInfo}.
 * Use its builder to create new instances:
 * 
 * <pre>
 * var userInfo = BaseAuthenticatedUserInfo.builder()
 *     .identifier("user123")
 *     .displayName("John Doe")
 *     .addRole("USER")
 *     .authenticated(true)
 *     .build();
 * </pre>
 * 
 * <h2>Thread Safety</h2>
 * All model classes in this package are immutable and thread-safe by design.
 * The builder pattern ensures safe construction of user information objects.
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.authentication.model;
