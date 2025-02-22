/**
 * Provides utility classes and helper components for the Portal Configuration module.
 * These utilities handle common configuration tasks such as property filtering,
 * placeholder resolution, and configuration value processing.
 * 
 * <h2>Key Components</h2>
 * 
 * <h3>Configuration Helpers</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigurationHelper} - Core utilities
 *       for configuration handling, property filtering, and value resolution</li>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigurationPlaceholderHelper} - 
 *       Handles placeholder resolution in configuration values</li>
 * </ul>
 * 
 * <h3>Placeholder Support</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigPlaceholder} - Represents
 *       configuration placeholders with optional default values</li>
 *   <li>Format: {@code ${key}} or {@code ${key:defaultValue}}</li>
 *   <li>Supports nested placeholders up to 5 levels deep</li>
 * </ul>
 * 
 * <h3>Exception Handling</h3>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.util.ConfigKeyNestingException} - 
 *       Thrown when placeholder nesting exceeds the maximum depth</li>
 * </ul>
 * 
 * <h2>Common Use Cases</h2>
 * <ul>
 *   <li>Filtering configuration properties by prefix</li>
 *   <li>Resolving placeholders in configuration values</li>
 *   <li>Processing configuration values with defaults</li>
 *   <li>Handling environment variable references</li>
 * </ul>
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration.util;
