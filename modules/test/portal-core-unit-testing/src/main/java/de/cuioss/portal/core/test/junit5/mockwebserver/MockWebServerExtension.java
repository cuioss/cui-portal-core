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

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import de.cuioss.tools.logging.CuiLogger;
import mockwebserver3.MockWebServer;

/**
 * JUnit 5 extension that provides {@link MockWebServer} support for tests. It will
 * create a new {@link MockWebServer} instance for each test method and close it
 * afterwards. Supports nested test classes by sharing the parent's MockWebServer instance.
 */
public class MockWebServerExtension implements BeforeEachCallback, AfterEachCallback {

    private static final CuiLogger LOGGER = new CuiLogger(MockWebServerExtension.class);

    private MockWebServer server;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        var testInstance = context.getRequiredTestInstance();
        var testClass = testInstance.getClass();
        var annotation = testClass.getAnnotation(EnableMockWebServer.class);
        boolean manualStart = annotation != null && annotation.manualStart();

        // For manual start tests, create a new server instance but don't start it
        if (manualStart && testInstance instanceof MockWebServerHolder holder) {
            LOGGER.debug("Creating new MockWebServer instance for manual start");
            server = new MockWebServer();
            // Configure dispatcher if provided
            var dispatcher = holder.getDispatcher();
            if (dispatcher != null) {
                LOGGER.debug("Using custom dispatcher for MockWebServer");
                server.setDispatcher(dispatcher);
            }
            holder.setMockWebServer(server);
            return;
        }

        // For non-manual tests, create and start the server if needed
        if (!manualStart && server == null) {
            LOGGER.debug("Initializing new MockWebServer instance");
            server = new MockWebServer();
            
            // Configure dispatcher if provided
            if (testInstance instanceof MockWebServerHolder holder) {
                var dispatcher = holder.getDispatcher();
                if (dispatcher != null) {
                    LOGGER.debug("Using custom dispatcher for MockWebServer");
                    server.setDispatcher(dispatcher);
                }
            }

            // Start server for non-manual tests
            LOGGER.debug("Starting MockWebServer on random port");
            server.start();
        }

        // Inject server for non-manual tests
        if (!manualStart && testInstance instanceof MockWebServerHolder holder) {
            LOGGER.debug("Test class implements MockWebServerHolder, injecting server instance");
            holder.setMockWebServer(server);
        }
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var testInstance = context.getRequiredTestInstance();
        var testClass = testInstance.getClass();
        var annotation = testClass.getAnnotation(EnableMockWebServer.class);
        boolean manualStart = annotation != null && annotation.manualStart();

        // For manual start, don't try to shut down the server
        if (manualStart) {
            return;
        }

        if (server != null) {
            LOGGER.debug("Shutting down MockWebServer at %s", server.url("/"));
            server.shutdown();
            server = null;
        }
    }
}
