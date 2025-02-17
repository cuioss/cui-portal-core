/**
 * <p>
 * Provides the handling of the unified {@link java.util.ResourceBundle}
 * </p>
 * <h2>Usage</h2>
 * <p>
 * The central element is
 * {@link de.cuioss.portal.common.bundle.ResourceBundleWrapper} It unifies all
 * configured {@link java.util.ResourceBundle}s for the portal.
 * To use it within a bean use:
 * </p>
 *
 * <pre>
 *
 * &#064;Inject
 * private ResourceBundleWrapper resourceBundleWrapper;
 * </pre>
 * <p>
 * It is exposed as well as the named ResourceBundle "msgs" and can therefore
 * be used within xhtml as standard {@link java.util.ResourceBundle}:
 *
 * <pre>
 * {@code #(msgs['page.dashboard.title'])}
 * </pre>
 *
 * <h2>Configuration</h2>
 * <p>
 * Extending the {@link java.util.ResourceBundle}s is quite easy on a module
 * level. You need to provide an instance of
 * {@link de.cuioss.portal.common.bundle.ResourceBundleLocator} The actual
 * configuration will be done with
 * {@link de.cuioss.portal.common.bundle.ResourceBundleRegistry}
 * </p>
 */
package de.cuioss.portal.common.bundle;
