/**
 * Mock web server infrastructure for HTTP interaction testing, based on
 * {@link mockwebserver3.MockWebServer}.
 *
 * <h2>Package Organization</h2>
 * <ul>
 *   <li>Server Management
 *     <ul>
 *       <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer} - Server activation</li>
 *       <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerExtension} - Lifecycle management</li>
 *     </ul>
 *   </li>
 *   <li>Request Handling
 *     <ul>
 *       <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher} - Custom request dispatchers</li>
 *       <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder} - Server access</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>Key Concepts</h2>
 * <ul>
 *   <li><strong>Server Lifecycle</strong> - Automated server management</li>
 *   <li><strong>Request Handling</strong> - Custom response configuration</li>
 *   <li><strong>Test Integration</strong> - JUnit 5 extension support</li>
 * </ul>
 *
 * For detailed documentation and examples, see:
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer} for server setup</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher} for request handling</li>
 * </ul>
 *
 * @see de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher
 * @see mockwebserver3.MockWebServer
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.core.test.junit5.mockwebserver;
