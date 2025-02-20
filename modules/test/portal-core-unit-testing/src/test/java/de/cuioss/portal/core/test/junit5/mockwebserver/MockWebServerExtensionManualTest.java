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

import lombok.Getter;
import lombok.Setter;
import mockwebserver3.MockWebServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Test class for verifying that the {@link MockWebServerExtension} works correctly
 * when manually registered with {@link ExtendWith}.
 */
@ExtendWith(MockWebServerExtension.class)
@EnableMockWebServer(manualStart = true)
class MockWebServerExtensionManualTest implements MockWebServerHolder {

    @Getter
    @Setter
    private MockWebServer mockWebServer;

    @Test
    void shouldProvideServerNotStartedServer() throws Exception {
        assertNotNull(mockWebServer, "Server should be injected even for manual start");

        // Now start the server manually
        assertDoesNotThrow(() ->mockWebServer.start());

        // Clean up
        mockWebServer.shutdown();
    }
}
