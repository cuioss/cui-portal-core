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
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

import brave.http.HttpTracing;
import brave.jaxrs2.TracingClientFilter;

/**
 * The {@link ClientRequestFilter} portion of {@link TracingClientFilter}.
 * Simply delegates the request filter to {@link TracingClientFilter}.
 * <p>
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
