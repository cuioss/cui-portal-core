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
import de.cuioss.test.mockwebserver.EnableMockWebServer;
import de.cuioss.test.mockwebserver.MockWebServerHolder;
import de.cuioss.test.mockwebserver.dispatcher.BaseAllAcceptDispatcher;
import de.cuioss.test.mockwebserver.dispatcher.CombinedDispatcher;
import de.cuioss.tools.logging.CuiLogger;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import lombok.Getter;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockWebServer;
import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.Closeable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Bearer Token Authentication filter functionality in the REST client.
 * Verifies the proper handling of authentication headers and token values.
 */
@EnableAutoWeld
@AddExtensions(ResteasyCdiExtension.class)
@EnablePortalConfiguration(configuration = "portal.tracing.enabled:false")
@EnableMockWebServer
@DisplayName("BearerTokenAuthFilter Tests")
class BearerTokenAuthFilterTest implements MockWebServerHolder {

    private static final CuiLogger LOGGER = new CuiLogger(BearerTokenAuthFilterTest.class);
    private static final String TEST_TOKEN = "test-token-123";
    private static final int CONCURRENT_THREADS = 5;

    @Getter
    @Setter
    private MockWebServer mockWebServer;
    private CuiRestClientBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new CuiRestClientBuilder(LOGGER);
    }

    @Path("/")
    public interface TestResource extends Closeable {
        @GET
        @Path("success")
        String test();
    }

    @Test
    @DisplayName("Should provide valid authorization header")
    void shouldProvideAuthorizationHeader() throws InterruptedException {
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken(TEST_TOKEN)
                .build(TestResource.class);

        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty(), "Authorization header should be present");
        assertEquals("Bearer " + TEST_TOKEN, authHeader.get(0), "Authorization header should have correct format");
    }

    @Test
    @DisplayName("Should handle concurrent requests properly")
    void shouldHandleConcurrentRequests() throws InterruptedException {
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken(TEST_TOKEN)
                .build(TestResource.class);

        // Setup concurrent requests
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);


        // Execute concurrent requests
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executor.execute(() -> {
                client.test();
                latch.countDown();
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS), "All requests should complete");

        // Verify all requests had correct headers
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            var request = mockWebServer.takeRequest();
            var authHeader = request.getHeaders().values("Authorization");
            assertFalse(authHeader.isEmpty(), "Authorization header should be present");
            assertEquals("Bearer " + TEST_TOKEN, authHeader.get(0),
                    "Authorization header should have correct format for all requests");
        }

        executor.shutdown();
    }

    @Test
    @DisplayName("Should handle null token values appropriately")
    void shouldHandleNullTokenValue() throws InterruptedException {
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken(null)
                .build(TestResource.class);

        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertTrue(authHeader.isEmpty(), "Authorization header should be empty for null token");
    }

    @Test
    @DisplayName("Should handle empty token appropriately")
    void shouldHandleEmptyToken() throws InterruptedException {
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken("")
                .build(TestResource.class);

        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertTrue(authHeader.isEmpty(), "Authorization header should be empty for empty token");
    }

    @ParameterizedTest(name = "Should handle token with special characters: {0}")
    @ValueSource(strings = {
            "token with spaces",
            "token\nwith\nnewlines",
            "token@#$%^&*"
    })
    void shouldHandleTokensWithSpecialCharacters(String token) throws InterruptedException {
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken(token)
                .build(TestResource.class);

        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty(), "Authorization header should be present");
        assertEquals("Bearer " + token.replaceAll("[\n\r\t]", " ").trim(),
                authHeader.get(0), "Authorization header should handle special characters");
    }

    @Test
    @DisplayName("Should handle very long token")
    void shouldHandleVeryLongToken() throws InterruptedException {
        String longToken = "x".repeat(4096); // 4KB token
        var client = builder.url(mockWebServer.url("success").toString())
                .bearerAuthToken(longToken)
                .build(TestResource.class);

        client.test();
        var request = mockWebServer.takeRequest();
        var authHeader = request.getHeaders().values("Authorization");
        assertFalse(authHeader.isEmpty(), "Authorization header should be present");
        assertEquals("Bearer " + longToken, authHeader.get(0),
                "Authorization header should handle long tokens");
    }

    @Override
    public Dispatcher getDispatcher() {
        return new CombinedDispatcher(new BaseAllAcceptDispatcher("/success"));
    }
}
