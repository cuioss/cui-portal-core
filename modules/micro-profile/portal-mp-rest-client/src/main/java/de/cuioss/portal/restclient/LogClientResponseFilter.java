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

import java.io.IOException;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;

import de.cuioss.tools.logging.CuiLogger;
import jakarta.annotation.Priority;

/**
 * A {@linkplain ClientResponseFilter} to log the response metadata received by
 * the rest-client.
 * <p>
 * This class is annotated with {@link Priority} with value
 * {@link Integer#MIN_VALUE} to ensure it is the very last filter that is
 * called.
 * <p>
 * This is an abstract class to allow multi-registering via anonymous class.
 */
@Priority(Integer.MIN_VALUE)
abstract class LogClientResponseFilter implements ClientResponseFilter {

    private static final String PATTERN = """
            -- Client response filter {}--
            Status: {}
            StatusInfo: {}
            Allowed Methods: {}
            EntityTag: {}
            Cookies: {}
            Date: {}
            Headers: {}
            Language: {}
            LastModified: {}
            Links: {}
            Location: {}
            MediaType: {}
            """;

    private final CuiLogger log;

    private final String name;

    protected LogClientResponseFilter(final CuiLogger logger) {
        this(logger, "");
    }

    protected LogClientResponseFilter(final CuiLogger logger, final String name) {
        log = logger;
        this.name = "[" + name + "] ";
    }

    @Override
    public void filter(final ClientRequestContext clientRequestContext,
            final ClientResponseContext clientResponseContext) throws IOException {
        try {
            log.info(PATTERN, name, clientResponseContext.getStatus(), clientResponseContext.getStatusInfo(),
                    clientResponseContext.getAllowedMethods(), clientResponseContext.getEntityTag(),
                    clientResponseContext.getCookies(), clientResponseContext.getDate(),
                    clientResponseContext.getHeaders(), clientResponseContext.getLanguage(),
                    clientResponseContext.getLastModified(), clientResponseContext.getLinks(),
                    clientResponseContext.getLocation(), clientResponseContext.getMediaType());
        } catch (final Exception e) {
            log.error("Portal-529: Could not trace-log response data", e);
        }
    }
}
