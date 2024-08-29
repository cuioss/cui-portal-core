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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.support.AnnotationSupport;

import mockwebserver3.MockWebServer;

/**
 * Handle the lifetime of an instance of {@link MockWebServer}, see
 * {@link EnableMockWebServer} for details on using
 *
 * @author Oliver Wolff
 */
public class MockWebServerExtension implements TestInstancePostProcessor, AfterEachCallback {

    private static final Logger log = LoggerFactory.getLogger(MockWebServerExtension.class);

    /**
     * Identifies the {@link Namespace} under which the concrete instance of
     * {@link MockWebServer} is stored.
     */
    public static final Namespace NAMESPACE = Namespace.create("test", "portal", "MockWebServer");

    @SuppressWarnings({ "squid:S2095" }) // owolff: Will be closed after all tests
    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {

        var mockWebServer = new MockWebServer();

        assertInstanceOf(MockWebServerHolder.class, testInstance, "In order to use within a test the test-class must implement de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder "
            + testInstance);

        var holder = (MockWebServerHolder) testInstance;
        holder.setMockWebServer(mockWebServer);
        Optional.ofNullable(holder.getDispatcher()).ifPresent(mockWebServer::setDispatcher);
        if (extractAnnotation(testInstance.getClass()).map(annotation -> !annotation.manualStart())
                .orElse(Boolean.FALSE)) {
            mockWebServer.start();
            log.info(() -> "Started MockWebServer at " + mockWebServer.url("/"));
        }
        put(mockWebServer, context);
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var server = get(context);
        if (server.isPresent()) {
            log.info(() -> "Shutting down MockWebServer at " + server.get().url("/"));
            server.get().shutdown();
        } else {
            log.error(() -> "Server not present, therefore can not be shutdown");
        }

    }

    private static void put(MockWebServer mockWebServer, ExtensionContext context) {
        context.getStore(NAMESPACE).put(MockWebServer.class.getName(), mockWebServer);
    }

    private Optional<MockWebServer> get(ExtensionContext context) {
        return Optional.ofNullable((MockWebServer) context.getStore(NAMESPACE).get(MockWebServer.class.getName()));
    }

    private static Optional<EnableMockWebServer> extractAnnotation(Class<?> testClass) {
        Optional<EnableMockWebServer> annotation = AnnotationSupport.findAnnotation(testClass,
                EnableMockWebServer.class);
        if (annotation.isPresent()) {
            return annotation;
        }
        if (Object.class.equals(testClass.getClass())) {
            return Optional.empty();
        }
        return extractAnnotation(testClass.getSuperclass());
    }

}
