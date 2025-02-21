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
package de.cuioss.portal.restclient;

import de.cuioss.portal.configuration.impl.producer.ConnectionMetadataProducer;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.Getter;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.Closeable;

import static org.junit.jupiter.api.Assertions.*;

@EnableMockWebServer
@EnableAutoWeld
@EnablePortalConfiguration
@AddBeanClasses({ConnectionMetadataProducer.class, RestClientProducer.class})
@AddExtensions(ResteasyCdiExtension.class)
class RestClientProducerTest implements MockWebServerHolder {

    public interface TestResource extends Closeable {

        @GET
        @Path("test")
        String test();

    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public @NotNull MockResponse dispatch(@NotNull RecordedRequest request) {
                assert request.getPath() != null;
                return switch (request.getPath()) {
                    case "/success/test" ->
                        new MockResponse(HttpServletResponse.SC_OK, Headers.of("Content-Type", "application/fhir+xml", "ETag", "W/123"), "test");
                    case "/error/test" -> new MockResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    default -> new MockResponse(HttpServletResponse.SC_NOT_FOUND);
                };
            }
        };
    }

    @Inject
    @PortalRestClient(baseName = "abc", failOnInvalidConfiguration = false)
    @Getter
    private Provider<RestClientHolder<TestResource>> underTestProvider;

    @Inject
    private PortalTestConfiguration configuration;

    @Setter
    private MockWebServer mockWebServer;

    @Test
    void serviceNotAvailable() {
        assertNotNull(underTestProvider.get());
        assertFalse(underTestProvider.get().isServiceAvailable());
    }

    @Test
    void serviceAvailable() {
        configuration.update("abc.url", mockWebServer.url("success").toString());
        assertNotNull(underTestProvider.get());
        assertTrue(underTestProvider.get().isServiceAvailable());
        assertEquals("test", underTestProvider.get().get().test());
    }

}
