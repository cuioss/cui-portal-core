package de.cuioss.portal.core.test.junit5.mockwebserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import lombok.Setter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@EnableMockWebServer
class MockWebServerExtensionTest implements MockWebServerHolder {

    @Setter
    private MockWebServer mockWebServer;

    @Test
    void shouldHandleMockWebServer() {
        assertNotNull(mockWebServer);
    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                switch (request.getPath()) {
                case "/index":
                    return new MockResponse().setResponseCode(200);
                default:
                    return new MockResponse().setResponseCode(403);
                }
            }
        };
    }
}
