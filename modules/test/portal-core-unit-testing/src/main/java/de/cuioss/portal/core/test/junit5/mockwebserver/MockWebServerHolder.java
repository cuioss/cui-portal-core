package de.cuioss.portal.core.test.junit5.mockwebserver;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Holder class for instances of {@link MockWebServer}. It provides an injection
 * point for the actual {@link MockWebServer} and provides an optional Callback
 * for an Dispatcher to be used within {@link MockWebServer}, see
 * {@link MockWebServer#setDispatcher(Dispatcher)}
 *
 * @author Oliver Wolff
 *
 */
public interface MockWebServerHolder {

    /**
     * @param mockWebServer to be set from
     */
    default void setMockWebServer(MockWebServer mockWebServer) {
    }

    /**
     * @return a {@link Dispatcher} to be used if needed.
     */
    default Dispatcher getDispatcher() {
        return null;
    }
}
