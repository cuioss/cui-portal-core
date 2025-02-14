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
package de.cuioss.portal.core.servlet;

import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.juli.junit5.EnableTestLogger;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.LogAsserts;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.http.HttpServletRequest;
import org.jboss.weld.junit5.auto.ActivateScopes;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static de.cuioss.portal.core.PortalCoreLogMessages.SERVLET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the {@link CuiExternalHostname} qualifier in conjunction with the hostname producer
 */
@EnableAutoWeld
@EnableTestLogger(trace = ExternalHostnameProducer.class)
@AddBeanClasses({ExternalHostnameProducer.class})
@ActivateScopes(RequestScoped.class)
class ExternalHostnameProducerTest {

    public static final String PATH = "/context/more";
    private static final String SERVER_NAME = "test.example.com";
    private static final int SERVER_PORT = 8443;
    private static final String SERVER_NAME_AND_PORT = SERVER_NAME + ":" + SERVER_PORT;
    public static final String PROTOCOL = "https://";

    @Inject
    @CuiExternalHostname
    Provider<String> hostname;

    private boolean verifyAgainstXForward = false;

    @BeforeEach
    void setUp() {
        verifyAgainstXForward = false;
    }

    @Produces
    @RequestScoped
    HttpServletRequest produceRequest() {
        return new CuiMockHttpServletRequest() {
            @Override
            public int getServerPort() {
                return SERVER_PORT;
            }

            @Override
            public String getServerName() {
                return SERVER_NAME;
            }

            @Override
            public String getProtocol() {
                return PROTOCOL;
            }

            @Override
            public String getHeader(String name) {
                if (!verifyAgainstXForward) {
                    return super.getHeader(name);
                }
                return switch (name) {
                    case ExternalHostnameProducer.X_FORWARDED_HOST -> SERVER_NAME;
                    case ExternalHostnameProducer.X_FORWARDED_PORT -> String.valueOf(SERVER_PORT);
                    case ExternalHostnameProducer.X_FORWARDED_PROTO -> PROTOCOL;
                    default -> super.getHeader(name);
                };
            }
        };
    }

    @Test
    @DisplayName("Should inject external hostname from request")
    void shouldInjectExternalHostname() {
        var name = hostname.get();
        assertNotNull(name, "Hostname should be injected");
        assertEquals(SERVER_NAME_AND_PORT, name, "Hostname should match request server name and port");
        LogAsserts.assertSingleLogMessagePresentContaining( TestLogLevel.DEBUG, "Resolved hostname: ");
    }

    @Test
    @DisplayName("Should inject external hostname from X-Forwarded headers")
    void shouldInjectExternalHostnameFromXForward() {
        verifyAgainstXForward = true;
        var name = hostname.get();
        assertNotNull(name, "Hostname should be injected");
        assertEquals(SERVER_NAME_AND_PORT, name, "Hostname should match X-Forwarded host and port");
        LogAsserts.assertSingleLogMessagePresentContaining( TestLogLevel.DEBUG, "Resolved hostname: ");
    }
}
