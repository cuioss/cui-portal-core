/**
 * JUnit 5 specific extensions and test infrastructure for Portal Core testing.
 *
 * <h2>Package Organization</h2>
 * <ul>
 *   <li>Configuration Support
 *     <ul>
 *       <li>{@link de.cuioss.portal.core.test.junit5.EnablePortalConfiguration} - Test configuration activation</li>
 *       <li>{@link de.cuioss.portal.core.test.junit5.PortalTestConfigurationExtension} - Configuration extension</li>
 *     </ul>
 *   </li>
 *   <li>HTTP Testing Support
 *     <ul>
 *       <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver} - Mock server and request handling</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><strong>Portal Configuration</strong> - Automated test configuration setup</li>
 *   <li><strong>HTTP Mocking</strong> - Web service interaction testing</li>
 *   <li><strong>JUnit Integration</strong> - Seamless JUnit 5 extension support</li>
 * </ul>
 *
 * For detailed documentation and examples, see:
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.junit5.EnablePortalConfiguration} for configuration setup</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver} for HTTP testing</li>
 * </ul>
 *
 * @see de.cuioss.portal.core.test.junit5.mockwebserver
 * @see org.junit.jupiter.api.extension.Extension
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.core.test.junit5;
