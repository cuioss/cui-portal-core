/**
 * Provides type-safe configuration injection types for the Portal Configuration module.
 * 
 * <h2>Core Types</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsList} - Injects configuration as List</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsSet} - Injects configuration as Set</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsPath} - Injects configuration as Path</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsLocale} - Injects configuration as Locale</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsCacheConfig} - Injects cache configuration</li>
 * </ul>
 * 
 * <h2>Optional Types</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigPropertyNullable} - Injects optional configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsFileLoader} - Injects file loader configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata} - Injects connection metadata</li>
 * </ul>
 * 
 * <h2>Usage</h2>
 * <pre>
 * &#64;Inject
 * &#64;ConfigAsList(name = "portal.theme.available_themes")
 * private List<String> availableThemes;
 * 
 * &#64;Inject
 * &#64;ConfigAsLocale(name = "portal.locale.default")
 * private Locale defaultLocale;
 * 
 * &#64;Inject
 * &#64;ConfigPropertyNullable(name = "portal.custom.setting")
 * private String customSetting;
 * </pre>
 */
package de.cuioss.portal.configuration.types;
