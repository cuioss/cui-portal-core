/**
 * Provides testing infrastructure for Portal applications using JUnit 5 and Weld-JUnit.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.junit5.EnablePortalConfiguration} - Annotation for enabling portal configuration in tests</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.PortalTestConfigurationExtension} - JUnit 5 extension for portal configuration</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver} - Mock web server support for testing HTTP interactions</li>
 * </ul>
 * 
 * <h2>Usage Examples</h2>
 * <pre>
 * &#64;EnablePortalConfiguration
 * class MyPortalTest {
 *     &#64;Inject
 *     PortalTestConfiguration configuration;
 *     
 *     &#64;Test
 *     void shouldLoadConfig() {
 *         assertNotNull(configuration);
 *     }
 * }
 * </pre>
 * 
 * <h2>Test Support Features</h2>
 * <ul>
 *   <li>CDI-based configuration testing</li>
 *   <li>Mock implementations for portal services</li>
 *   <li>HTTP server mocking capabilities</li>
 *   <li>Extensible test infrastructure</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.core.test.junit5;
