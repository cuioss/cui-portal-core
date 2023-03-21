package de.cuioss.portal.restclient;

import static javax.ws.rs.RuntimeType.CLIENT;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

import brave.http.HttpTracing;
import brave.jaxrs2.TracingClientFilter;

/**
 * The {@link ClientRequestFilter} portion of {@link TracingClientFilter}.
 * Simply delegates the request filter to {@link TracingClientFilter}.
 *
 * Request filters with low {@link Priority} numbers are processed first.
 *
 * @author Sven Haag
 */
@Provider
@ConstrainedTo(CLIENT)
@Priority(0)
class TracingRequestFilter implements ClientRequestFilter {
    final TracingClientFilter delegate;

    TracingRequestFilter(final HttpTracing httpTracing) {
        delegate = TracingClientFilter.create(httpTracing);
    }

    @Override
    public void filter(final ClientRequestContext request) {
        delegate.filter(request);
    }
}
