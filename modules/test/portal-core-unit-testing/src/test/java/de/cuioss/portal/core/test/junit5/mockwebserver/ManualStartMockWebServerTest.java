package de.cuioss.portal.core.test.junit5.mockwebserver;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.Setter;
import mockwebserver3.Dispatcher;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import okhttp3.Headers;

@EnableMockWebServer(manualStart = true)
class ManualStartMockWebServerTest implements MockWebServerHolder {

    @Getter
    @Setter
    private MockWebServer mockWebServer;

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {
            @Override
            public @NotNull MockResponse dispatch(final @NotNull RecordedRequest request) {
                return new MockResponse(200, Headers.of("Content-Type", "text/plain"), "OK");
            }
        };
    }

    @Test
    void shouldInjectServerForManualStart() {
        assertNotNull(mockWebServer, "Server should be injected even for manual start");
    }
}
