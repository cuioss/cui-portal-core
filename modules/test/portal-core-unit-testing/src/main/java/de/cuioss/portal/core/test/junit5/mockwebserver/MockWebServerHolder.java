/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cuioss.portal.core.test.junit5.mockwebserver;

import mockwebserver3.Dispatcher;
import mockwebserver3.MockWebServer;

/**
 * Interface for test classes that need access to a {@link MockWebServer} instance.
 * This interface serves as a bridge between the test infrastructure and test classes,
 * providing access to the server instance and optional request dispatching.
 *
 * <h2>Basic Implementation</h2>
 * <pre>
 * &#64;EnableMockWebServer
 * class BasicHttpTest implements MockWebServerHolder {
 *     private MockWebServer server;
 *
 *     &#64;Override
 *     public void setMockWebServer(MockWebServer mockWebServer) {
 *         this.server = mockWebServer;
 *     }
 * }
 * </pre>
 *
 * <h2>Custom Request Dispatching</h2>
 * <pre>
 * &#64;EnableMockWebServer
 * class CustomDispatchTest implements MockWebServerHolder {
 *     private MockWebServer server;
 *
 *     &#64;Override
 *     public void setMockWebServer(MockWebServer mockWebServer) {
 *         this.server = mockWebServer;
 *     }
 *
 *     &#64;Override
 *     public Dispatcher getDispatcher() {
 *         return new Dispatcher() {
 *             &#64;Override
 *             public MockResponse dispatch(RecordedRequest request) {
 *                 if ("/api/data".equals(request.getPath())) {
 *                     return new MockResponse().setBody("{'data': 'test'}");
 *                 }
 *                 return new MockResponse().setResponseCode(404);
 *             }
 *         };
 *     }
 * }
 * </pre>
 *
 * <h2>Usage Notes</h2>
 * <ul>
 *   <li>The {@link #setMockWebServer(MockWebServer)} method must be implemented to receive the server instance</li>
 *   <li>Implement {@link #getDispatcher()} to provide custom request handling logic</li>
 *   <li>The server instance is managed by {@link MockWebServerExtension}</li>
 *   <li>Default dispatcher returns null, meaning requests are handled by the default MockWebServer behavior</li>
 * </ul>
 *
 * @author Oliver Wolff
 * @since 1.0
 * @see EnableMockWebServer
 * @see MockWebServerExtension
 */
public interface MockWebServerHolder {

    /**
     * Callback method to receive the {@link MockWebServer} instance.
     * This method is called by the test infrastructure to provide the server instance.
     *
     * @param mockWebServer the server instance to be used in tests, never {@code null}
     */
    default void setMockWebServer(MockWebServer mockWebServer) {
    }

    /**
     * Provides a custom {@link Dispatcher} for handling HTTP requests.
     * Override this method to implement custom request handling logic.
     *
     * @return a {@link Dispatcher} instance, or {@code null} to use default request handling
     * @see MockWebServer#setDispatcher(Dispatcher)
     */
    default Dispatcher getDispatcher() {
        return null;
    }
}
