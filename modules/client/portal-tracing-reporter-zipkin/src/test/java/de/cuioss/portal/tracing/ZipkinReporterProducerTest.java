package de.cuioss.portal.tracing;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_REPORTER_URL;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAlternatives;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.configuration.PortalTestConfiguration;
import zipkin2.Span;
import zipkin2.reporter.Reporter;

/**
 * @author Sven Haag
 */
@EnableAutoWeld
@AddBeanClasses(ZipkinReporterProducer.class)
@EnableAlternatives(ZipkinReporterProducer.class)
@EnablePortalConfiguration
class ZipkinReporterProducerTest {

    @Inject
    private Provider<Reporter<Span>> reporter;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @Test
    void injects() {
        assertNull(reporter.get(), "Reporter<Span> must not be null");
        configuration.fireEvent(PORTAL_TRACING_REPORTER_URL, "http://localhost");
        assertNotNull(reporter.get(), "Reporter<Span> must not be null");
    }
}
