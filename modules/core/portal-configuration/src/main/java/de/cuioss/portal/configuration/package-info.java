/**
 * Core configuration package for the Portal, providing a comprehensive configuration
 * system based on MicroProfile Config.
 * 
 * <h2>Key Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.PortalConfigurationKeys} - Central repository of configuration keys</li>
 *   <li>{@link de.cuioss.portal.configuration.PortalConfigurationDefaults} - Default values for portal configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.CuiConstants} - Common constants used across the portal</li>
 * </ul>
 * 
 * <h2>Configuration Types</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsFileLoader} - Load file-based configurations</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsFileLoaderList} - Load multiple file configurations</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsList} - Parse list configurations</li>
 *   <li>{@link de.cuioss.portal.configuration.types.ConfigAsSet} - Parse set configurations</li>
 * </ul>
 * 
 * <h2>Basic Usage</h2>
 * <pre>
 * // Simple string property
 * &#64;Inject
 * &#64;ConfigProperty(name = "app.name")
 * private String appName;
 * 
 * // Integer with default
 * &#64;Inject
 * &#64;ConfigProperty(name = "app.port", defaultValue = "8080")
 * private Integer port;
 * 
 * // List of values
 * &#64;Inject
 * &#64;ConfigAsList(name = "app.allowed.origins")
 * private List<String> allowedOrigins;
 * 
 * // File configuration
 * &#64;Inject
 * &#64;ConfigAsFileLoader(name = "app.config.file")
 * private FileLoader configFile;
 * </pre>
 * 
 * <h2>Specialized Configurations</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.configuration.MetricsConfigKeys} - Metrics-specific configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.TracingConfigKeys} - Tracing configuration</li>
 *   <li>{@link de.cuioss.portal.configuration.HealthCheckConfigKeys} - Health check settings</li>
 * </ul>
 * 
 * <h2>Best Practices</h2>
 * <ul>
 *   <li>Use typed configuration where possible (Integer, Boolean, etc.)</li>
 *   <li>Provide sensible defaults for optional configurations</li>
 *   <li>Use specialized types for complex configurations (lists, sets, files)</li>
 *   <li>Reference configuration keys from {@link de.cuioss.portal.configuration.PortalConfigurationKeys}</li>
 * </ul>
 * 
 * @author Oliver Wolff
 */
package de.cuioss.portal.configuration;
