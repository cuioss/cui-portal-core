package de.cuioss.portal.restclient;

import static javax.ws.rs.RuntimeType.CLIENT;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

import brave.http.HttpTracing;
import brave.jaxrs2.TracingClientFilter;

/**
 * Response priority is sorted in descending order, that means filters with high
 * priority numbers are processed first. The RestEasy
 * {@code org.jboss.resteasy.microprofile.client.ExceptionMapping} is set to
 * priority 1 at
 * {@code org.jboss.resteasy.microprofile.client.RestClientBuilderImpl#build(java.lang.Class)}.
 * Therefore Tracing must have a higher value as it must be properly finished in
 * order to be reported!
 *
 * @author Sven Haag
 */
@Provider
@ConstrainedTo(CLIENT)
@Priority(1000)
public class TracingResponseFilter implements ClientResponseFilter {
    final TracingClientFilter delegate;

    TracingResponseFilter(HttpTracing httpTracing) {
        delegate = TracingClientFilter.create(httpTracing);
    }

    @Override
    public void filter(ClientRequestContext request, ClientResponseContext response) {
        delegate.filter(request, response);
    }
}
