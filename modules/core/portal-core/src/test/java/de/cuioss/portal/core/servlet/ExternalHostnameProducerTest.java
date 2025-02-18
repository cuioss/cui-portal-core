package de.cuioss.portal.core.servlet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import de.cuioss.test.jsf.mocks.CuiMockHttpServletRequest;
import de.cuioss.test.juli.LogAsserts;
import de.cuioss.test.juli.TestLogLevel;
import de.cuioss.test.juli.junit5.EnableTestLogger;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests the {@link CuiExternalHostname} qualifier in conjunction with the hostname producer.
 * Verifies hostname resolution from both direct server properties and X-Forwarded headers.
 */
@EnableAutoWeld
@EnableTestLogger(trace = ExternalHostnameProducer.class)
@AddBeanClasses({ExternalHostnameProducer.class})
@ActivateScopes(RequestScoped.class)
@DisplayName("Testing ExternalHostnameProducer functionality")
class ExternalHostnameProducerTest {

    private static final String PATH = "/context/more";
    private static final String SERVER_NAME = "test.example.com";
    private static final int SERVER_PORT = 8443;
    private static final String SERVER_NAME_AND_PORT = SERVER_NAME + ":" + SERVER_PORT;
    private static final String PROTOCOL = "https://";

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

    @Nested
    @EnableTestLogger(trace = ExternalHostnameProducer.class)
    @DisplayName("Direct Server Properties Tests")
    class DirectServerPropertiesTests {
        
        @Test
        @DisplayName("Should resolve hostname from server properties")
        void shouldResolveFromServerProperties() {
            var name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(SERVER_NAME_AND_PORT, name, 
                "Hostname should match request server name and port");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.DEBUG, 
                "Resolved hostname: ");
        }
    }

    @Nested
    @EnableTestLogger(trace = ExternalHostnameProducer.class)
    @DisplayName("X-Forwarded Header Tests")
    class XForwardedHeaderTests {

        @BeforeEach
        void enableXForwarded() {
            verifyAgainstXForward = true;
        }
        
        @Test
        @DisplayName("Should resolve hostname from X-Forwarded headers")
        void shouldResolveFromXForwardedHeaders() {
            var name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(SERVER_NAME_AND_PORT, name, 
                "Hostname should match X-Forwarded host and port");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.DEBUG, 
                "Resolved hostname: ");
        }
    }
}
