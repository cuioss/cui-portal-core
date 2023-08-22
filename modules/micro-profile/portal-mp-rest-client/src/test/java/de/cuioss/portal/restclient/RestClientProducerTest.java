package de.cuioss.portal.restclient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Closeable;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.AddExtensions;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.impl.producer.ConnectionMetadataProducer;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.junit5.mockwebserver.EnableMockWebServer;
import de.cuioss.portal.core.test.junit5.mockwebserver.MockWebServerHolder;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import de.cuioss.portal.tracing.PortalTracing;
import lombok.Getter;
import lombok.Setter;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@EnableMockWebServer
@EnableAutoWeld
@EnablePortalConfiguration
@AddBeanClasses({ ConnectionMetadataProducer.class, RestClientProducer.class, PortalTracing.class })
@AddExtensions(ResteasyCdiExtension.class)
class RestClientProducerTest implements MockWebServerHolder {

    public interface TestResource extends Closeable {

        @GET
        @Path("test")
        String test();

    }

    @Override
    public Dispatcher getDispatcher() {
        return new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) {
                return switch (request.getPath()) {
                case "/success/test" -> new MockResponse().setResponseCode(HttpServletResponse.SC_OK)
                                            .addHeader("Content-Type", "application/fhir+xml").addHeader("ETag", "W/123")
                                            .setBody("test");
                case "/error/test" -> new MockResponse().setResponseCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                default -> new MockResponse().setResponseCode(HttpServletResponse.SC_NOT_FOUND);
                };
            }
        };
    }

    @Inject
    @PortalRestClient(baseName = "abc", failOnInvalidConfiguration = false)
    @Getter
    private Provider<RestClientHolder<TestResource>> underTestProvider;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @Setter
    private MockWebServer mockWebServer;

    @Test
    void testServiceNotAvailable() {
        assertNotNull(underTestProvider.get());
        assertFalse(underTestProvider.get().isServiceAvailable());
    }

    @Test
    void testServiceAvailable() {
        configuration.fireEvent("abc.url", mockWebServer.url("success").toString());
        assertNotNull(underTestProvider.get());
        assertTrue(underTestProvider.get().isServiceAvailable());
        assertEquals("test", underTestProvider.get().get().test());
    }

}
