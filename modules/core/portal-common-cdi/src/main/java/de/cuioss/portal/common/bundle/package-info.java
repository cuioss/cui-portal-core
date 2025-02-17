/**
 * Provides a unified resource bundle management system for Portal applications.
 *
 * <h2>Overview</h2>
 * This package provides a centralized way to handle {@link java.util.ResourceBundle}s
 * in Portal applications. It enables unified access to messages and resources across
 * different modules while supporting dynamic locale changes and hierarchical bundle
 * resolution.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.bundle.ResourceBundleWrapper} - Core interface
 *       for accessing unified bundles with support for dynamic locale changes</li>
 *   <li>{@link de.cuioss.portal.common.bundle.ResourceBundleRegistry} - Central registry
 *       managing bundle configuration and resolution order</li>
 *   <li>{@link de.cuioss.portal.common.bundle.ResourceBundleLocator} - Interface for
 *       locating and providing module-specific bundles with priority support</li>
 *   <li>{@link de.cuioss.portal.common.bundle.PortalResourceBundleBean} - JSF integration
 *       component exposing bundles to views</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>JSF View Usage</h3>
 * Access bundle messages in XHTML views:
 * <pre>
 * // Basic message access
 * #{msgs['page.title']}
 * 
 * // With parameters
 * #{msgs['welcome.message'].format(user.name)}
 * 
 * // Nested keys
 * #{msgs['errors.validation.required']}
 * </pre>
 *
 * <h3>Java Code Usage</h3>
 * Programmatic access to bundles:
 * <pre>
 * &#064;Inject
 * private ResourceBundleWrapper bundleWrapper;
 * 
 * public String getLocalizedMessage() {
 *     return bundleWrapper.getString("page.title");
 * }
 * </pre>
 *
 * <h2>Configuration</h2>
 * To add module-specific bundles:
 * <ol>
 *   <li>Implement {@link de.cuioss.portal.common.bundle.ResourceBundleLocator}</li>
 *   <li>Add {@link jakarta.annotation.Priority} to define bundle precedence</li>
 *   <li>Register your implementation as a CDI bean</li>
 *   <li>The {@link de.cuioss.portal.common.bundle.ResourceBundleRegistry} will
 *       automatically discover and integrate your bundles</li>
 * </ol>
 *
 * <h2>Thread Safety</h2>
 * All components in this package are thread-safe and support concurrent access.
 * The bundle resolution system is designed to handle concurrent locale changes
 * efficiently.
 *
 * @author Oliver Wolff
 * @see java.util.ResourceBundle
 * @see jakarta.faces.application.Application#getResourceBundle
 */
package de.cuioss.portal.common.bundle;
