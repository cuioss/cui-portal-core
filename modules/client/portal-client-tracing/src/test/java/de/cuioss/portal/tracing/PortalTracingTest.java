package de.cuioss.portal.tracing;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_REPORTER_URL;
import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_SAMPLER_PROBABILITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import brave.Tracing;
import brave.sampler.CountingSampler;
import brave.sampler.RateLimitingSampler;
import brave.sampler.Sampler;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.configuration.connections.impl.ConnectionMetadata;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

/**
 * @author Sven Haag
 */
@EnableAutoWeld
@EnablePortalConfiguration
@AddBeanClasses(PortalTracing.class)
class PortalTracingTest {

    @Inject
    private Tracing tracing;

    @Inject
    private Provider<Reporter<Span>> reporter;

    @Inject
    private Provider<Sampler> samplerProvider;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @AfterAll
    static void afterAll() {
        assertNull(Tracing.current(), "Tracing should be closed");
    }

    @Test
    void injects() {
        assertNotNull(tracing, "Tracing must not be null");
        assertNull(reporter.get(), "Reporter<Span> must be null due to missing config");
        assertNotNull(samplerProvider.get(), "Sampler must not be null");

        configuration.fireEvent(PORTAL_TRACING_REPORTER_URL, "http://localhost");
        assertNull(reporter.get(),
                "Reporter<Span> must not be produced in this module (but portal-tracing-xyz-reporter)");
    }

    @Test
    void extractsUriPath() {
        assertEquals("my-id", PortalTracing.getServiceName(ConnectionMetadata.builder().connectionId("my-id")
                .serviceUrl("foo://sub.domain.name/path/test/1").build()), "should use the connection id");

        assertEquals("path", PortalTracing.getServiceName(ConnectionMetadata.builder()
                // no connection id
                .serviceUrl("foo://sub.domain.name/path/test/2").build()), "should use the first path part");

        assertEquals("path", PortalTracing.getServiceName(ConnectionMetadata.builder()
                // no connection id
                .serviceUrl("foo://sub.domain.name:1234/path").build()), "should use the first path part");

        assertEquals("sub.domain.name",
                PortalTracing.getServiceName(ConnectionMetadata.builder().serviceUrl("foo://sub.domain.name:1234") // no
                                                                                                                   // trailing
                                                                                                                   // slash
                        .build()),
                "should use the uri host name");

        assertEquals("sub.domain.name",
                PortalTracing.getServiceName(ConnectionMetadata.builder().serviceUrl("foo://sub.domain.name:1234/") // trailing
                                                                                                                    // slash
                        .build()),
                "should use the uri host name");
    }

    @Test
    void configBasedSampler() {
        var underTest = samplerProvider.get();
        assertEquals(Sampler.ALWAYS_SAMPLE, underTest);

        configuration.fireEvent(PORTAL_TRACING_SAMPLER_PROBABILITY, "0.0");
        underTest = samplerProvider.get();
        assertTrue(underTest instanceof RateLimitingSampler);

        configuration.fireEvent(PORTAL_TRACING_SAMPLER_PROBABILITY, "0.66");
        underTest = samplerProvider.get();
        assertTrue(underTest instanceof CountingSampler);
    }
}
