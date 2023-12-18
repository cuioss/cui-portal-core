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

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Closeable;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.tools.logging.CuiLogger;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;

@EnableAutoWeld
@EnableMockWebServer
@EnablePortalConfiguration(configuration = PORTAL_TRACING_ENABLED + ":false")
@AddExtensions(ResteasyCdiExtension.class)
class BearerTokenAuthFilterTest implements MockWebServerHolder {

    private static final CuiLogger log = new CuiLogger(BearerTokenAuthFilterTest.class);

    public interface TestResource extends Closeable {

        @GET
        @Path("test")
        @javax.ws.rs.Produces("application/json")
        String test();
    }

    @Setter
    private MockWebServer mockWebServer;

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(final RecordedRequest request) {
                switch (request.getPath()) {
                case "/success/test":
                    return new MockResponse(HttpServletResponse.SC_OK);
                }
                return new MockResponse(HttpServletResponse.SC_NOT_FOUND);
            }
        };
    }

    @Test
    void shouldProviderAuthorizationHeader() throws InterruptedException {
        final var underTest = new CuiRestClientBuilder(log).url(mockWebServer.url("success").toString())
                .bearerAuthToken("abcToken");
        underTest.build(CuiRestClientBuilderTest.TestResource.class).test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty());
        assertEquals("Bearer abcToken", authHeader.get(0));
    }

    @Test
    void shouldProviderAuthorizationHeaderWithInvalidValues() throws InterruptedException {
        new CuiRestClientBuilder(log).url(mockWebServer.url("success").toString()).bearerAuthToken(null)
                .build(CuiRestClientBuilderTest.TestResource.class).test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertTrue(authHeader.isEmpty());
        new CuiRestClientBuilder(log).url(mockWebServer.url("success").toString()).bearerAuthToken("123\ntest: test2")
                .build(CuiRestClientBuilderTest.TestResource.class).test();
        request = mockWebServer.takeRequest();
        authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty());
        assertEquals("Bearer 123 test: test2", authHeader.get(0));
    }
}
