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
