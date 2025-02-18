/**
 * Provides JUnit 5 extensions and utilities for testing HTTP interactions using {@link okhttp3.mockwebserver.MockWebServer}.
 * 
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer} - Annotation to enable MockWebServer in tests</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerExtension} - JUnit 5 extension managing server lifecycle</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder} - Interface for accessing MockWebServer instance</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>
 * &#64;EnableMockWebServer
 * class HttpClientTest implements MockWebServerHolder {
 *     private MockWebServer server;
 *     
 *     &#64;Override
 *     public void setMockWebServer(MockWebServer mockWebServer) {
 *         this.server = mockWebServer;
 *     }
 *     
 *     &#64;Test
 *     void shouldHandleHttpRequest() {
 *         server.enqueue(new MockResponse().setBody("response"));
 *         // Test HTTP client...
 *     }
 * }
 * </pre>
 * 
 * <h2>Features</h2>
 * <ul>
 *   <li>Automatic server lifecycle management</li>
 *   <li>Custom request dispatching</li>
 *   <li>Response queuing</li>
 *   <li>Request verification</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see mockwebserver3.MockWebServer
 * @see de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher
 */
package de.cuioss.portal.core.test.junit5.mockwebserver;
