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

import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.Closeable;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_ENABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the Bearer Token Authentication filter functionality in the REST client.
 * Verifies proper handling of authorization headers with bearer tokens.
 */
@EnableAutoWeld
@EnableMockWebServer
@EnablePortalConfiguration(configuration = PORTAL_TRACING_ENABLED + ":false")
@AddExtensions(ResteasyCdiExtension.class)
@DisplayName("Bearer Token Authentication Tests")
class BearerTokenAuthFilterTest implements MockWebServerHolder {

    private static final CuiLogger log = new CuiLogger(BearerTokenAuthFilterTest.class);
    private static final String TEST_TOKEN = "abcToken";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TEST_PATH = "/success/test";

    @Path("test")
    public interface TestResource extends Closeable {
        @GET
        @jakarta.ws.rs.Produces("application/json")
        String test();
    }

    @Setter
    private MockWebServer mockWebServer;
    private CuiRestClientBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new CuiRestClientBuilder(log);
    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {
            @Override
            public @NotNull MockResponse dispatch(final @NotNull RecordedRequest request) {
                if (TEST_PATH.equals(request.getPath())) {
                    return new MockResponse(HttpServletResponse.SC_OK);
                }
                return new MockResponse(HttpServletResponse.SC_NOT_FOUND);
            }
        };
    }

    @Test
    @DisplayName("Should add valid bearer token to authorization header")
    void shouldProvideAuthorizationHeader() throws InterruptedException {
        // Given
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken(TEST_TOKEN)
                .build(TestResource.class);

        // When
        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");

        // Then
        assertFalse(authHeader.isEmpty(), "Authorization header should be present");
        assertEquals(BEARER_PREFIX + TEST_TOKEN, authHeader.get(0), "Authorization header should have correct format");
    }

    @Test
    @DisplayName("Should handle null token values appropriately")
    void shouldHandleNullTokenValue() throws InterruptedException {
        // When/Then: Test with null token
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken(null)
                .build(TestResource.class);

        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertTrue(authHeader.isEmpty(), "Authorization header should be empty for null token");

    }

    @Test
    @DisplayName("Should handle invalid token values appropriately")
    void shouldHandleInvalidTokenValues() throws InterruptedException {


        // When/Then: Test with token containing special characters
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken("123\ntest: test2")
                .build(TestResource.class);

        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty(), "Authorization header should be present");
        assertEquals("Bearer 123 test: test2", authHeader.get(0), "Authorization header should have correct format");
    }
}
