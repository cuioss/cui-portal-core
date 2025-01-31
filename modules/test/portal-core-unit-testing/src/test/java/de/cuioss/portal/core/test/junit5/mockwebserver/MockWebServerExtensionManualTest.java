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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@EnableMockWebServer(manualStart = true)
class MockWebServerExtensionManualTest implements MockWebServerHolder {

    @Setter
    private MockWebServer mockWebServer;

    @Test
    void shouldHandleMockWebServer() throws IOException {
        assertNotNull(mockWebServer);
        mockWebServer.start(0);
    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public @NotNull MockResponse dispatch(@NotNull RecordedRequest request) throws InterruptedException {
                assert request.getPath() != null;
                return switch (request.getPath()) {
                    case "/index" -> new MockResponse(200);
                    default -> new MockResponse(403);
                };
            }
        };
    }
}
