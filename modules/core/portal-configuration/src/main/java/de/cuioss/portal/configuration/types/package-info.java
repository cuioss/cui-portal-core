/**
 * Provides type-safe configuration injection types for the Portal Configuration module.
 * Each type provides specific functionality for injecting configuration values in a
 * type-safe manner.
 * 
 * <h2>Available Configuration Types</h2>
 * 
 * <h3>Core Types</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsList} - List injection with separator support</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsSet} - Set injection with deduplication</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsPath} - Path injection with validation</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsLocale} - Locale injection with fallback</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsCacheConfig} - Cache configuration injection</li>
 * </ul>
 * 
 * <h3>Optional Types</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigPropertyNullable} - Nullable property injection</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsFileLoader} - File loader configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata} - Connection metadata</li>
 * </ul>
 * 
 * <p>
 * For detailed usage examples and configuration options, refer to the documentation
 * of each individual configuration type.
 * </p>
 * 
 * @see de.cuioss.portal.configuration.types.ConfigAsList
 * @see de.cuioss.portal.configuration.types.ConfigAsSet
 * @see de.cuioss.portal.configuration.types.ConfigAsPath
 * @see de.cuioss.portal.configuration.types.ConfigAsLocale
 * @see de.cuioss.portal.configuration.types.ConfigAsCacheConfig
 * @see de.cuioss.portal.configuration.types.ConfigPropertyNullable
 * @see de.cuioss.portal.configuration.types.ConfigAsFileLoader
 * @see de.cuioss.portal.configuration.types.ConfigAsConnectionMetadata
 */
package de.cuioss.portal.configuration.types;
