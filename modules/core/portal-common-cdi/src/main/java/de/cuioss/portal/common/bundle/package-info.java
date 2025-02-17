/**
 * Provides a unified resource bundle management system for Portal applications.
 *
 * <h2>Overview</h2>
 * This package provides a centralized way to handle {@link java.util.ResourceBundle}s
 * in Portal applications.
 * It enables unified access to messages and resources across different modules while supporting dynamic locale changes.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.common.bundle.ResourceBundleWrapper} - Core interface for accessing unified bundles</li>
 *   <li>{@link de.cuioss.portal.common.bundle.ResourceBundleRegistry} - Central registry for bundle configuration</li>
 *   <li>{@link de.cuioss.portal.common.bundle.ResourceBundleLocator} - Interface for locating module-specific bundles</li>
 * </ul>
 *
 * <h2>Usage Examples</h2>
 *
 * <h3>XHTML Usage</h3>
 * The bundle is exposed as "msgs" and can be used in XHTML:
 * <pre>
 * {@code #{msgs['page.title']}}
 * </pre>
 *
 * <h2>Configuration</h2>
 * To add module-specific bundles:
 * <ol>
 *   <li>Implement {@link de.cuioss.portal.common.bundle.ResourceBundleLocator}</li>
 *   <li>Register your implementation as CDI bean</li>
 *   <li>The {@link de.cuioss.portal.common.bundle.ResourceBundleRegistry} will automatically pick up your bundles</li>
 * </ol>
 *
 * @author Oliver Wolff
 */
package de.cuioss.portal.common.bundle;
