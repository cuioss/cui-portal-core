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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    private String customServerName = SERVER_NAME;
    private int customServerPort = SERVER_PORT;
    private String customXForwardedHost = null;
    private String customXForwardedPort = null;
    private boolean verifyAgainstXForward = false;

    @BeforeEach
    void setUp() {
        verifyAgainstXForward = false;
        customServerName = SERVER_NAME;
        customServerPort = SERVER_PORT;
        customXForwardedHost = null;
        customXForwardedPort = null;
    }

    @Produces
    @RequestScoped
    HttpServletRequest produceRequest() {
        return new CuiMockHttpServletRequest() {
            @Override
            public int getServerPort() {
                return customServerPort;
            }

            @Override
            public String getServerName() {
                return customServerName;
            }

            @Override
            public String getProtocol() {
                return PROTOCOL;
            }

            @Override
            public String getHeader(String name) {
                if (!verifyAgainstXForward) {
                    return null;
                }
                return switch (name) {
                    case ExternalHostnameProducer.X_FORWARDED_HOST -> customXForwardedHost;
                    case ExternalHostnameProducer.X_FORWARDED_PORT -> customXForwardedPort;
                    case ExternalHostnameProducer.X_FORWARDED_PROTO -> PROTOCOL;
                    default -> null;
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

        @ParameterizedTest(name = "Should handle server name={0} and port={1}")
        @CsvSource({
            "localhost, 80",
            "127.0.0.1, 443",
            "'', 8080",
            "test-server.local, 0",
            "very.long.domain.name.example.com, 65535"
        })
        @DisplayName("Should handle edge case server properties")
        void shouldHandleEdgeCaseServerProperties(String serverName, int serverPort) {
            customServerName = serverName;
            customServerPort = serverPort;
            
            var name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(serverName + ":" + serverPort, name, 
                "Hostname should match custom server name and port");
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
            customXForwardedHost = SERVER_NAME;
            customXForwardedPort = String.valueOf(SERVER_PORT);
            
            var name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(SERVER_NAME_AND_PORT, name, 
                "Hostname should match X-Forwarded host and port");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.DEBUG, 
                "Resolved hostname: ");
        }

        @ParameterizedTest(name = "Should handle X-Forwarded host={0} and port={1}")
        @CsvSource({
            "proxy.example.com, 8080",
            "load-balancer.local, 443",
            "'', 80",
            "multiple.proxy.chain.example.com, 9000",
            "ipv6.example.com, 8443"
        })
        @DisplayName("Should handle edge case X-Forwarded headers")
        void shouldHandleEdgeCaseXForwardedHeaders(String forwardedHost, String forwardedPort) {
            customXForwardedHost = forwardedHost;
            customXForwardedPort = forwardedPort;
            
            var name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(forwardedHost + ":" + forwardedPort, name, 
                "Hostname should match X-Forwarded host and port");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.DEBUG, 
                "Resolved hostname: ");
        }

        @Test
        @DisplayName("Should fallback to server properties when X-Forwarded headers are null")
        void shouldFallbackToServerPropertiesWhenHeadersNull() {
            customXForwardedHost = null;
            customXForwardedPort = null;
            customServerName = "fallback.example.com";
            customServerPort = 9443;
            
            var name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(customServerName + ":" + customServerPort, name, 
                "Hostname should fallback to server properties");
            LogAsserts.assertSingleLogMessagePresentContaining(TestLogLevel.DEBUG, 
                "Resolved hostname: ");
        }

        @Test
        @DisplayName("Should handle partial X-Forwarded headers")
        void shouldHandlePartialXForwardedHeaders() {
            // Only X-Forwarded-Host is present
            customXForwardedHost = "partial.example.com";
            customXForwardedPort = null;
            customServerPort = 7443;
            
            var name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(customXForwardedHost + ":" + customServerPort, name, 
                "Hostname should use X-Forwarded-Host with server port");

            // Only X-Forwarded-Port is present
            customXForwardedHost = null;
            customXForwardedPort = "7080";
            customServerName = "server.example.com";
            
            name = hostname.get();
            assertNotNull(name, "Hostname should be injected");
            assertEquals(customServerName + ":" + customXForwardedPort, name, 
                "Hostname should use server name with X-Forwarded-Port");
        }
    }
}
