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
package de.cuioss.portal.tracing;

import static de.cuioss.portal.configuration.TracingConfigKeys.PORTAL_TRACING_SERVLET_ENABLED;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.eclipse.microprofile.config.ConfigProvider;

import brave.Tracing;
import brave.servlet.TracingFilter;
import de.cuioss.portal.configuration.PortalConfigurationChangeEvent;
import de.cuioss.tools.logging.CuiLogger;

/**
 * A servlet tracing filter for URL pattern {@code "/*"}. This ensures that
 * every (UI) request is enriched with tracing information. Subsequent tracing
 * spans, e.g. from a http client request, are using this trace info as their
 * parent.
 * <p>
 * The filter may not be the first in the servlet filter chain, but that should
 * be no problem as we have no need to provide tracing information to servlet
 * filters. If that should be a requirement in the future please use a servlet
 * listener which registers a filter using
 * {@link javax.servlet.FilterRegistration#addMappingForUrlPatterns(EnumSet, boolean, String...)}.
 *
 * @author Sven Haag
 */
@WebFilter(filterName = "BraveTracingFilter", urlPatterns = "/*", dispatcherTypes = { DispatcherType.REQUEST,
        DispatcherType.ERROR, DispatcherType.ASYNC, DispatcherType.FORWARD, DispatcherType.INCLUDE })
@ApplicationScoped
public class PortalTracingServletFilter implements Filter {

    private static final CuiLogger log = new CuiLogger(PortalTracingServletFilter.class);

    @Inject
    Provider<Tracing> tracing;

    private Filter filter;

    @Override
    public void init(final FilterConfig filterConfig) {
        if (Boolean.TRUE.equals(ConfigProvider.getConfig().getValue(PORTAL_TRACING_SERVLET_ENABLED, Boolean.class))) {
            log.info("Servlet tracing filter ENABLED");
            filter = TracingFilter.create(tracing.get());
        } else {
            log.info("Servlet tracing filter DISABLED");
            filter = null;
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (null != filter) {
            filter.doFilter(request, response, chain);
            tracing.get().close(); // disables Tracing.current()
        } else {
            chain.doFilter(request, response);
        }
    }

    void configurationChangeEventListener(
            @Observes @PortalConfigurationChangeEvent final Map<String, String> deltaMap) {
        if (deltaMap.containsKey(PORTAL_TRACING_SERVLET_ENABLED)) {
            log.debug("re-init due to config change");
            init(null);
        }
    }
}
