package de.cuioss.portal.core.test.junit5.mockwebserver;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MockWebServerHolderTest {

    @Test
    void shouldImplementDefaultMethods() {
        var holder = new MockWebServerHolder() {};
        assertDoesNotThrow(() -> holder.setMockWebServer(null));
        assertNull(holder.getDispatcher());
    }
}