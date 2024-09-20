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

import mockwebserver3.Dispatcher;
import mockwebserver3.MockWebServer;

/**
 * Holder class for instances of {@link MockWebServer}. It provides an injection
 * point for the actual {@link MockWebServer} and provides an optional Callback
 * for a Dispatcher to be used within {@link MockWebServer}, see
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
