/**
 * <h2>Summary</h2>
 * <p>
 * Provides classes and structures to configure resource bundles.
 * </p>
 * <h2>Module configuration</h2>
 * <p>
 * Each module can provide additional resource bundles by implementing
 * {@link de.cuioss.portal.configuration.bundles.ResourceBundleLocator}
 * annotated with
 * {@link de.cuioss.portal.configuration.bundles.PortalResourceBundleLocator}.
 * The priority should be
 * com.icw.ehf.cui.portal.cdi.api.common.PortalPriorities.PORTAL_MODULE_LEVEL
 * </p>
 *
 * @author Matthias Walliczek
 */
package de.cuioss.portal.configuration.bundles;
