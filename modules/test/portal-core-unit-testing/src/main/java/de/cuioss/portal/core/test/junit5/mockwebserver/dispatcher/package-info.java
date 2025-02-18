/**
 * Provides dispatcher implementations for handling HTTP requests in MockWebServer tests.
 * These dispatchers allow for flexible and modular request handling in test scenarios.
 *
 * <h2>Core Components</h2>
 * <ul>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.BaseAllAcceptDispatcher} - Base dispatcher providing default positive responses</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.EndpointAnswerHandler} - Handler for configuring endpoint responses</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.CombinedDispatcher} - Combines multiple dispatchers</li>
 *   <li>{@link de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher.ModuleDispatcherElement} - Interface for modular dispatchers</li>
 * </ul>
 *
 * <h2>Usage Example</h2>
 * <pre>
 * class MyDispatcherTest implements MockWebServerHolder {
 *     private final BaseAllAcceptDispatcher dispatcher = 
 *         new BaseAllAcceptDispatcher("/api");
 *
 *     &#64;Override
 *     public Dispatcher getDispatcher() {
 *         return dispatcher;
 *     }
 *
 *     &#64;Test
 *     void shouldHandleCustomResponse() {
 *         dispatcher.getGetResult()
 *             .setResponse(new MockResponse().setBody("custom"));
 *         // Test with custom response
 *     }
 * }
 * </pre>
 *
 * <h2>Features</h2>
 * <ul>
 *   <li>Modular request handling</li>
 *   <li>Pre-configured response handlers</li>
 *   <li>Support for all HTTP methods</li>
 *   <li>Customizable response content</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 */
package de.cuioss.portal.core.test.junit5.mockwebserver.dispatcher;
