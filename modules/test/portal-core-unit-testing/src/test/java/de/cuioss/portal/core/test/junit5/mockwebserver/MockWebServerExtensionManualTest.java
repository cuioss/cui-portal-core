package de.cuioss.portal.core.test.junit5.mockwebserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lombok.Setter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

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
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                return switch (request.getPath()) {
                case "/index" -> new MockResponse().setResponseCode(200);
                default -> new MockResponse().setResponseCode(403);
                };
            }
        };
    }
}
