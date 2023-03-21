package de.cuioss.portal.tracing;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_SERVLET_ENABLED;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.servlet.ServletException;

import org.apache.myfaces.test.mock.MockHttpServletRequest;
import org.apache.myfaces.test.mock.MockHttpServletResponse;
import org.jboss.weld.junit5.auto.AddBeanClasses;
import org.jboss.weld.junit5.auto.EnableAutoWeld;
import org.junit.jupiter.api.Test;

import brave.Tracing;
import de.cuioss.portal.configuration.PortalConfigurationSource;
import de.cuioss.portal.core.test.junit5.EnablePortalConfiguration;
import de.cuioss.portal.core.test.mocks.PortalTestConfiguration;

@EnableAutoWeld
@AddBeanClasses(PortalTracing.class)
@EnablePortalConfiguration
class PortalTracingServletFilterTest {

    @Inject
    private PortalTracingServletFilter underTest;

    @Inject
    @PortalConfigurationSource
    private PortalTestConfiguration configuration;

    @Test
    void test() throws IOException, ServletException {
        configuration.fireEvent(PORTAL_TRACING_SERVLET_ENABLED, "true");
        underTest.init(null);

        final var chainProcessed = new AtomicBoolean(false);
        assertNotNull(Tracing.current());
        underTest.doFilter(new TraceTestRequest(), new MockHttpServletResponse(), (request, response) -> {
            chainProcessed.set(true);
            // request does not contain tracing headers yet!
            // only the servlet context is enriched with brave specific attributes!
        });
        assertTrue(chainProcessed.get());
        assertNull(Tracing.current());
    }

    @Test
    void proceedsChainIfDisabled() throws IOException, ServletException {
        configuration.fireEvent(PORTAL_TRACING_SERVLET_ENABLED, "false");
        underTest.init(null);

        final var chainProcessed = new AtomicBoolean(false);
        assertNull(Tracing.current());
        underTest.doFilter(new TraceTestRequest(), new MockHttpServletResponse(), (request, response) -> {
            chainProcessed.set(true);
            // request does not contain tracing headers yet!
            // only the servlet context is enriched with brave specific attributes!
        });
        assertTrue(chainProcessed.get());
        assertNull(Tracing.current());
    }

    private static final class TraceTestRequest extends MockHttpServletRequest {
        @Override
        public boolean isAsyncStarted() {
            return false;
        }
    }
}